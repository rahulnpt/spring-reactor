package fluxandmonoplayground;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

public class ColdAndHotPublisherTest {
	
	/**
	 * Cold Publisher starts the elements of the flux from beginning for all subscribers
	 * @throws InterruptedException
	 */
	@Test
	public void coldPublisherTest() throws InterruptedException {
		Flux<String> finiteFlux = Flux.just("A","B","C","D","E","F")
			.delayElements(Duration.ofSeconds(1));
			//.log();
		
		finiteFlux.subscribe(ele->System.out.println("Subscriber 1 next element"+ele)); //will start the elements from beginning
		
		Thread.sleep(2000);
		
		finiteFlux.subscribe(ele->System.out.println("Subscriber 2 next element"+ele)); //again will start the elements from beginning
		
		Thread.sleep(4000);
	}
	
	/**
	 * Hot publishers will start generating new data right away and do not depend upon publishers.
	 * @throws InterruptedException
	 */
	@Test
	public void hotPublisherTest() throws InterruptedException {
		Flux<String> finiteFlux = Flux.just("A","B","C","D","E","F")
			.delayElements(Duration.ofSeconds(1));
			//.log();
		
		//below step will make this a hot publisher
		ConnectableFlux<String> connectableFlux = finiteFlux.publish();
		connectableFlux.connect();
		
		connectableFlux.subscribe(ele->System.out.println("Subscriber 1 next element"+ele)); 
		
		Thread.sleep(2000);
		
		connectableFlux.subscribe(ele->System.out.println("Subscriber 2 next element"+ele)); 
		
		Thread.sleep(4000);
	}
}
