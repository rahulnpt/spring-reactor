package fluxandmonoplayground;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoCombineTest {
	
	@Test
	public void combineUsingMerge() {
		Flux<String> first = Flux.just("A","B","C");
		Flux<String> second= Flux.just("D","E","F");
		
		Flux<String> merged = Flux.merge(first,second);
		
		StepVerifier.create(merged.log())
			.expectSubscription()//this is the first event that occurs therefore we can expect this
			.expectNext("A","B","C","D","E","F")
			.verifyComplete();
	}
	
	@Test
	public void combineWithDelay() {
		Flux<String> first = Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
		Flux<String> second= Flux.just("D","E","F").delayElements(Duration.ofSeconds(1));
		
		Flux<String> merged = Flux.merge(first,second);
		
		StepVerifier.create(merged.log())
			.expectSubscription()//this is the first event that occurs therefore we can expect this
			.expectNextCount(6)
			//.expectNext("A","B","C","D","E","F")//this will not work when we introduce delay as order might change again
			.verifyComplete();
	}
	@Test
	public void combineUsingConcat() {
		Flux<String> first = Flux.just("A","B","C");
		Flux<String> second= Flux.just("D","E","F");
		
		Flux<String> merged = Flux.concat(first,second);
		
		StepVerifier.create(merged.log())
			.expectSubscription()//this is the first event that occurs therefore we can expect this
			.expectNext("A","B","C","D","E","F")
			.verifyComplete();
	}
	@Test
	public void combineUsingConcatWithDelay() {
		Flux<String> first = Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
		Flux<String> second= Flux.just("D","E","F").delayElements(Duration.ofSeconds(1));
		
		Flux<String> merged = Flux.concat(first,second);
		
		StepVerifier.create(merged.log())
			.expectSubscription()//this is the first event that occurs therefore we can expect this
			//.expectNextCount(6)
			.expectNext("A","B","C","D","E","F")
			.verifyComplete();
	}
}
