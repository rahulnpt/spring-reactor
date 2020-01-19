package com.learnreactivespring.controller;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * Browser is the client which subscribes to the Flux when invoking any of below URLs.  
 * 
 * @author Rahul
 *
 */
@RestController
public class FluxAndMonoController {

	/*
	 *  the client in this case would just take the response as normal JSON
	 */
	@GetMapping("/flux")
	public Flux<Integer> basicFlux() {
		return Flux.just(1,2,3,4).log();
	}
	/**
	 * in this case, there occurs a delay of 4 seconds before browser renders the output
	 * 
	 */
	@GetMapping("/fluxWithDelay")
	public Flux<Integer> basicFlux_withDelay() {
		return Flux.just(1,2,3,4).delayElements(Duration.ofSeconds(1)).log();
	}
	
	/**
	 * in this case, the browser renders the response as and when its available
	 * 
	 */
	@GetMapping(value="/fluxStream",produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<Integer> basicFlux_stream() {
		return Flux.just(1,2,3,4,1,2,3,4).delayElements(Duration.ofSeconds(1)).log();
	}
	
	@GetMapping(value="/fluxStreamInfinite",produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<Long> basicFlux_infiniteStream() {
		return Flux.interval(Duration.ofMillis(200)).log();
	}
	
	@GetMapping(value="/mono")
	public Mono<Integer> getMono() {
		return Mono.just(1).log();
	}
}
