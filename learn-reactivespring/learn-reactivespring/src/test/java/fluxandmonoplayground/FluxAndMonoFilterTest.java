package fluxandmonoplayground;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoFilterTest {
	
	List<String> names = Arrays.asList("Adam","brinda","madhu","subala","lalit");
	
	@Test	
	public void fluxFilterTest() {
		
		Flux<String> filteredFlux = Flux.fromIterable(names)
					.filter(str->str.startsWith("m"))
					.log();
		
		StepVerifier.create(filteredFlux)
			.expectNext("madhu")
			.verifyComplete();
	}
	
}
