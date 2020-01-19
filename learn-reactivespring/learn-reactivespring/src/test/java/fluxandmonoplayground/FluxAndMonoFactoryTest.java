package fluxandmonoplayground;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoFactoryTest {

	List<String> names = Arrays.asList("Adam","brinda","madhu","subala","lalit");
	
	@Test	
	public void fluxUsingIterable() {
		Flux<String> namesFlux = Flux.fromIterable(names).log();
		
		StepVerifier.create(namesFlux)
			.expectNext("Adam","brinda","madhu","subala","lalit")
			.verifyComplete();
	}
	
	@Test	
	public void fluxUsingArray() {
		
		String names[] = new String[] {"Adam","brinda","madhu","subala","lalit"};
		
		Flux<String> namesFlux = Flux.fromArray(names).log();
		
		StepVerifier.create(namesFlux)
			.expectNext("Adam","brinda","madhu","subala","lalit")
			.verifyComplete();
	}
	
	@Test	
	public void fluxUsingStream() {
		
		Flux<String> namesFlux = Flux.fromStream(names.stream()).log();
		
		StepVerifier.create(namesFlux)
			.expectNext("Adam","brinda","madhu","subala","lalit")
			.verifyComplete();
	}
	
	@Test	
	public void fluxUsingRange() {
		
		Flux<Integer> integerFlux = Flux.range(1, 5).log();
		
		StepVerifier.create(integerFlux)
			.expectNext(1,2,3,4,5)
			.verifyComplete();
	}
	
	@Test	
	public void monoUsingJustOrEmpty() {
		Mono<Object> emptyMono = Mono.justOrEmpty(null);
		
		StepVerifier.create(emptyMono.log())
			.verifyComplete();
	}
	
	@Test	
	public void monoUsingSupplier() {
		
		Supplier<String> stringSupplier = ()->"krsna";
		
		StepVerifier.create(Mono.fromSupplier(stringSupplier).log())
			.expectNext("krsna")
			.verifyComplete();
	}
}
