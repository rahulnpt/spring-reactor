package fluxandmonoplayground;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {
	
	@Test
	public void backPressureTest() {
		
		Flux<Integer> finiteFlux = Flux.range(1, 10).log();
		
		StepVerifier.create(finiteFlux)
			.expectSubscription()
			.thenRequest(1)
			.expectNext(1)
			.thenRequest(1)
			.expectNext(2)
			.thenCancel()//once you cancel, you dont get onComplete event, so we can just verify
			.verify();
	}
	
	@Test
	public void backPressure_basic() {
		
		Flux<Integer> finiteFlux = Flux.range(1, 10).log();
		
		finiteFlux.subscribe(System.out::println //1st arg is subscriber
				,(exp)->System.out.println(exp)//2nd arg is exp handler
				,()->System.out.println("Done")//this is for onComplete
				,(subscription)->subscription.request(5));//this is the subscription object...which can be used to request certain items
			
	}

	@Test
	public void backPressure_cancel() {
		
		Flux<Integer> finiteFlux = Flux.range(1, 10).log();
		
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		
		System.out.println("availableProcessors "+availableProcessors);
		finiteFlux.subscribe(System.out::println //1st arg is subscriber
				,(exp)->System.out.println(exp)//2nd arg is exp handler
				,()->System.out.println("Done")//this is for onComplete
				,(subscription)->subscription.cancel());//calling cancel will not return any element from the flux...but this is not so useful
	}

	@Test
	public void backPressure_customized() {
		
		Flux<Integer> finiteFlux = Flux.range(1, 10).log();
		
		finiteFlux.subscribe(new BaseSubscriber<Integer>() {

			@Override
			protected void hookOnNext(Integer value) {
				request(1);
				System.out.println("value is "+value);
				if(value==4)
					cancel();
			}
			
		});
		
	}


}
