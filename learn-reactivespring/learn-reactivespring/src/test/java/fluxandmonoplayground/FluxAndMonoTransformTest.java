package fluxandmonoplayground;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class FluxAndMonoTransformTest {
	List<String> names = Arrays.asList("Adam", "brinda");

	@Test
	public void fluxTransformTest() {

		Flux<String> filteredFlux = Flux.fromIterable(names).map(str -> str.toUpperCase()).log();

		StepVerifier.create(filteredFlux).expectNext("ADAM", "BRINDA").verifyComplete();
	}

	@Test
	public void fluxTransform_usingRepeat() {

		Flux<String> filteredFlux = Flux.fromIterable(names).map(str -> str.toUpperCase()).repeat(1)// repeating the
																									// flux items 1 more
																									// time and
																									// expecting same
																									// below
				.log();

		StepVerifier.create(filteredFlux).expectNext("ADAM", "BRINDA", "ADAM", "BRINDA").verifyComplete();
	}

	@Test	
	public void fluxTransform_usingFlatMap() {
		
		Flux<String> someFlux = Flux.fromIterable(Arrays.asList("A","B","C"))
				.flatMap(s -> {
					return Flux.fromIterable(convertToList(s));
				}).log();
		
		StepVerifier.create(someFlux)
			.expectNextCount(6)
			.verifyComplete();
	}

	private List<String> convertToList(String name) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("name "+name);
		return Arrays.asList(name, "randomVal");
	}
	
	@Test	
	public void fluxTransform_usingParallel() {
		
		Flux<String> someFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
				.window(2) //Flux<Flux<String>> -> (A,B), (C,D), (E,F)
				.flatMap((s) ->s.map(this::convertToList).subscribeOn(Schedulers.parallel()))
				.flatMap(s->Flux.fromIterable(s))
				.log();
		
		StepVerifier.create(someFlux)
			.expectNextCount(12)
			.verifyComplete();
	}
	
	@Test	
	public void fluxTransform_parallel_maintain_order() {
		
		Flux<String> someFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
				.window(2) //Flux<Flux<String>> -> (A,B), (C,D), (E,F)
				.flatMapSequential((s) ->s.map(this::convertToList).subscribeOn(Schedulers.parallel()))
				.flatMap(s->Flux.fromIterable(s))
				.log();
		
		StepVerifier.create(someFlux)
			.expectNextCount(12)
			.verifyComplete();
	}
}
