package fluxandmonoplayground;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {
	
	@Test
	public void simpleFlux() {
		Flux<String> stringFlux = Flux.just("Spring","Spring Boot");
		
		stringFlux
			.subscribe(System.out::println,
					(err)->System.err.println(err));//second arg for error handling
	}
	
	@Test
	public void fluxWithError() {
		Flux<String> stringFlux = Flux.just("Spring","Spring Boot")
					.concatWith(Flux.error(new RuntimeException("Exception occured")));//adding error to the flux
		
		stringFlux
			.subscribe(System.out::println,
					(err)->System.err.println(err));//second arg for error handling
	}
	
	@Test
	public void fluxWithErrorLog() {
		Flux<String> stringFlux = Flux.just("Spring","Spring Boot")
					.concatWith(Flux.error(new RuntimeException("Exception occured")))//adding error to the flux
					.log();
		stringFlux
			.subscribe(System.out::println,
					(err)->System.err.println(err));//second arg for error handling
	}
	
	@Test
	public void fluxWithCompleteLog() {
		Flux<String> stringFlux = Flux.just("Spring","Spring Boot")
					//.concatWith(Flux.error(new RuntimeException("Exception occured")))//adding error to the flux
					.log();
		stringFlux
			.subscribe(System.out::println,
					(err)->System.err.println(err));//second arg for error handling
	}
	
	@Test
	public void fluxAfterError() {
		Flux<String> stringFlux = Flux.just("Spring","Spring Boot")
					.concatWith(Flux.error(new RuntimeException("Exception occured")))//adding error to the flux
					.concatWith(Flux.just("After Error"))
					.log();
		stringFlux
			.subscribe(System.out::println,
					(err)->System.err.println(err));//second arg for error handling
	}
	
	@Test
	public void fluxWithCompleteEventHandler() {
		Flux<String> stringFlux = Flux.just("Spring","Spring Boot")
					.concatWith(Flux.just("Webflux"))
					.log();
		stringFlux
			.subscribe(System.out::println,
					(err)->System.err.println(err),
					()->System.out.println("completed"));//second arg for error handling
	}
	
	@Test
	public void fluxTestElements_WithoutErrors() {
		Flux<String> stringFlux = Flux.just("Spring","Spring Boot")
					.concatWith(Flux.just("Webflux"))
					.log();

		StepVerifier.create(stringFlux)
			.expectNext("Spring")
			.expectNext("Spring Boot")
			.expectNext("Webflux")
			.verifyComplete();//this is equivalent to subscribe...it causes the flow of the flux elements to start
	}
	
	@Test
	public void fluxTestElements_WithErrors() {
		Flux<String> stringFlux = Flux.just("Spring","Spring Boot")
					.concatWith(Flux.just("Webflux"))
					.concatWith(Flux.error(new RuntimeException("Exception occured")))//adding error to the flux
					.log();

		StepVerifier.create(stringFlux)
			.expectNext("Spring")
			.expectNext("Spring Boot")
			.expectNext("Webflux")
			//.expectError(RuntimeException.class)
			.expectErrorMessage("Exception occured")
			.verify();//this is needed to start the flow
	}
	@Test
	public void fluxTestElementsAnotherWay_WithErrors() {
		Flux<String> stringFlux = Flux.just("Spring","Spring Boot")
					.concatWith(Flux.just("Webflux"))
					.concatWith(Flux.error(new RuntimeException("Exception occured")))//adding error to the flux
					.log();

		StepVerifier.create(stringFlux)
			.expectNext("Spring","Spring Boot","Webflux")//just specify the expected values in one line
			.expectErrorMessage("Exception occured")
			.verify();//this is needed to start the flow
	}
	
	@Test
	public void fluxTestElementsCount_WithErrors() {
		Flux<String> stringFlux = Flux.just("Spring","Spring Boot")
					.concatWith(Flux.just("Webflux"))
					.concatWith(Flux.error(new RuntimeException("Exception occured")))//adding error to the flux
					.log();

		StepVerifier.create(stringFlux)
			.expectNextCount(3)
			.expectErrorMessage("Exception occured")
			.verify();//this is needed to start the flow
	}
	
	@Test
	public void monoTest() {
		Mono<String> mono = Mono.just("Spring")
					.log();
		
		StepVerifier.create(mono.log())
			.expectNext("Spring")
			.verifyComplete();
	}
	
	@Test
	public void monoTest_withError() {
		StepVerifier.create(Mono.error(new RuntimeException("Exception occured")).log())
			.expectError(RuntimeException.class)
			.verify();
	}
}
