package com.learnreactivespring.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
public class FluxAndMonoControllerTest {
	
	@Autowired
	private WebTestClient webTestClient;
	
	@Test
	public void flux_approach1() {
		Flux<Integer> responseBody = webTestClient.get()
			.uri("/flux")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()//this is the one that actually makes the call
			.expectStatus().isOk()
			.returnResult(Integer.class)
			.getResponseBody();
		
		StepVerifier.create(responseBody)
			.expectSubscription()
			.expectNext(1,2,3,4)
			.verifyComplete();
	}
	
	@Test
	public void flux_approach2() {
		webTestClient.get()
			.uri("/flux")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()//this is the one that actually makes the call
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Integer.class)
			.hasSize(4);
	}
	@Test
	public void flux_approach3() {
		List<Integer> expected = Arrays.asList(1,2,3,4);
		
		EntityExchangeResult<List<Integer>> response = webTestClient.get()
			.uri("/flux")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()//this is the one that actually makes the call
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Integer.class)
			.returnResult();
		
		assertEquals(expected, response.getResponseBody());
	}
	
	@Test
	public void flux_approach4() {
		List<Integer> expected = Arrays.asList(1,2,3,4);
		
		webTestClient.get()
			.uri("/flux")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()//this is the one that actually makes the call
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Integer.class)
			.consumeWith(response->{ //we consume the EntityExchangeResult in this consumer
				assertEquals(expected, response.getResponseBody());
			});
	}
	
	@Test
	public void flux_for_infinite_stream() {
		Flux<Long> infiniteFlux = webTestClient.get()
			.uri("/fluxStreamInfinite")
			.accept(MediaType.APPLICATION_STREAM_JSON)
			.exchange()//this is the one that actually makes the call
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_STREAM_JSON)
			.returnResult(Long.class)
			.getResponseBody();
			
		StepVerifier.create(infiniteFlux)
			.expectNext(0L)
			.expectNext(1L)
			.expectNext(2L)
			.thenCancel()//since this is infinite flux, we are cancelling it here..as it will just go on
			.verify();
	}
	
	@Test
	public void mono_basic() {
		webTestClient.get()
			.uri("/mono")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()//this is the one that actually makes the call
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody(Integer.class)
			.consumeWith(res->{
				assertEquals(new Integer(1), res.getResponseBody());
			});
	}
}
