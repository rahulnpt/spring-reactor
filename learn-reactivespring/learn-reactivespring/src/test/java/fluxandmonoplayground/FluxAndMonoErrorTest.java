package fluxandmonoplayground;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import com.learnreactivespring.exception.CustomException;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoErrorTest {

	@Test
	public void fluxErrorHandling_onErrorResume() {
		Flux<String> stringFlux = Flux.just("A","B","C")
			.concatWith(Flux.error(new RuntimeException("Exception occured")))
			.concatWith(Flux.just("D"))//this will not be reached as errorresume flux will comtinue the sequence
			.onErrorResume(e->{ //this gets executed in case of exception. It goes ahead and resumes the sequence with this returned flux
				System.out.println("Exception is "+e);
				return Flux.just("Default","Default1");
			});
		
		StepVerifier.create(stringFlux.log())
			.expectSubscription()
			.expectNext("A","B","C")
			//.expectError(RuntimeException.class)
			//.verify();
			.expectNext("Default","Default1")
			.verifyComplete();
	}
	@Test
	public void fluxErrorHandling_onErrorReturn() {
		Flux<String> stringFlux = Flux.just("A","B","C")
			.concatWith(Flux.error(new RuntimeException("Exception occured")))
			.concatWith(Flux.just("D"))//this will not be reached as errorresume flux will comtinue the sequence
			.onErrorReturn("Default");
		
		StepVerifier.create(stringFlux.log())
			.expectSubscription()
			.expectNext("A","B","C")
			.expectNext("Default")
			.verifyComplete();
	}
	
	@Test
	public void fluxErrorHandling_onErrorMap() {
		Flux<String> stringFlux = Flux.just("A","B","C")
			.concatWith(Flux.error(new RuntimeException("Exception occured")))
			.concatWith(Flux.just("D"))//this will not be reached as errorresume flux will comtinue the sequence
			.onErrorMap(e->new CustomException(e));
		
		StepVerifier.create(stringFlux.log())
			.expectSubscription()
			.expectNext("A","B","C")
			.expectError(CustomException.class)
			.verify();
	}
	
	@Test
	public void fluxErrorHandling_onErrorMap_withRetry() {
		Flux<String> stringFlux = Flux.just("A","B","C")
			.concatWith(Flux.error(new RuntimeException("Exception occured")))
			.concatWith(Flux.just("D"))//this will not be reached as errorresume flux will comtinue the sequence
			.onErrorMap(e->new CustomException(e))
			.retry(2);//so total of 3 times the flux will be sent in case of exceptions...and then onErrorMap will be invoked
		
		StepVerifier.create(stringFlux.log())
			.expectSubscription()
			.expectNext("A","B","C")
			.expectNext("A","B","C")
			.expectNext("A","B","C")
			.expectError(CustomException.class)
			.verify();
	}
	
	@Test
	public void fluxErrorHandling_onErrorMap_withRetryBackOff() {
		Flux<String> stringFlux = Flux.just("A","B","C")
			.concatWith(Flux.error(new RuntimeException("Exception occured")))
			.concatWith(Flux.just("D"))//this will not be reached as errorresume flux will comtinue the sequence
			.onErrorMap(e->new CustomException(e))
			.retryBackoff(2, Duration.ofSeconds(2));//it is not gauranteed that backoff would be exactly 2 seconds...it could be more than that very often
		
		StepVerifier.create(stringFlux.log())
			.expectSubscription()
			.expectNext("A","B","C")
			.expectNext("A","B","C")
			.expectNext("A","B","C")
			.expectError(IllegalStateException.class)//we need to expect this 
			.verify();
	}
}
