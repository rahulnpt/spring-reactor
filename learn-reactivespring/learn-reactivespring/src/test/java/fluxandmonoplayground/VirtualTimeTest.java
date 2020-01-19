package fluxandmonoplayground;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

public class VirtualTimeTest {
	
	/**
	 * this test would use actual system clock and would thus increase the time of build process. We can reduce it using VirtualTIme
	 */
	@Test
	public void testingWithoutVirtualTime() {
		Flux<Long> finiteFlux = Flux.interval(Duration.ofSeconds(1)).take(3);
		
		StepVerifier.create(finiteFlux)
			.expectSubscription()
			.expectNext(0L,1L,2L)
			.verifyComplete();
	}
	
	@Test
	public void testingWithVirtualTime() {
		
		VirtualTimeScheduler.getOrSet();
		
		Flux<Long> finiteFlux = Flux.interval(Duration.ofSeconds(1)).take(3);
		
		StepVerifier.withVirtualTime(()->finiteFlux)
			.expectSubscription()
			.thenAwait(Duration.ofSeconds(3))
			.expectNext(0L,1L,2L)
			.verifyComplete();
	}
	
	@Test
	public void combine_virtualTime() {
		
		VirtualTimeScheduler.getOrSet();
		
		Flux<String> first = Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
		Flux<String> second= Flux.just("D","E","F").delayElements(Duration.ofSeconds(1));
		
		Flux<String> merged = Flux.concat(first,second);
		
		StepVerifier.withVirtualTime(()->merged.log())
			.expectSubscription()
			.thenAwait(Duration.ofSeconds(6))
			.expectNext("A","B","C","D","E","F")
			.verifyComplete();
		
	}
}
