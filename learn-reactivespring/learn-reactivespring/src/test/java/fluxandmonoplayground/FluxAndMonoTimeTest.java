package fluxandmonoplayground;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoTimeTest {
	
	@Test
	public void infiniteSequence() throws InterruptedException {
		Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100))
					.log();
		
		infiniteFlux.subscribe(System.out::println);
		
		//without putting the main thread to sleep, the program execution will end. The events are received on a separate thread. 
		Thread.sleep(3000);
	}
	
	@Test
	public void finiteSequence_usingTake() throws InterruptedException {
		Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(100))
					.take(3)
					.log();
		StepVerifier.create(finiteFlux)
			.expectSubscription()
			.expectNext(0L,1L,2L)
			.verifyComplete();
	}
	
	@Test
	public void infiniteSequenceMap() throws InterruptedException {
		Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
					.map(Long::intValue)
					.take(3)
					.log();
		StepVerifier.create(finiteFlux)
			.expectSubscription()
			.expectNext(0,1,2)
			.verifyComplete();
	}
	
	@Test
	public void infiniteSequenceMap_withDelay() throws InterruptedException {
		Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
					.delayElements(Duration.ofMillis(1000))	
					.map(Long::intValue)
					.take(3)
					.log();  
		StepVerifier.create(finiteFlux)
			.expectSubscription()
			.expectNext(0,1,2)
			.verifyComplete();
	}
}
