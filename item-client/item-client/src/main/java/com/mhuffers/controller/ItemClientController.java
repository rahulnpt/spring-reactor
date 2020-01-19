package com.mhuffers.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mhuffers.dto.Item;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemClientController {

	WebClient webClient = WebClient.create("http://localhost:8080");
	
	/*
	 * retrieve will directly return the response body but exchange will return response entity
	 */
			
	@GetMapping("/client/retrieve")
	public Flux<Item> getAllItemsUsingRetrieve() {
		return webClient.get().uri("/v1/items")
			.retrieve()
			.bodyToFlux(Item.class)
			.log("getting using retrieve");
	}
	
	@GetMapping("/client/exchange")
	public Flux<Item> getAllItemsUsingExchange() {
		return webClient.get().uri("/v1/items")
			.exchange()
			.flatMapMany(clientResponse->clientResponse.bodyToFlux(Item.class))
			.log("getting using retrieve");
	}
	
	@GetMapping("/client/retrieve/singleItemUsingRetrieve")
	public Mono<Item> getItemUsingRetrieve() {
		return webClient.get().uri("/v1/items/{id}","123")
			.retrieve()
			.bodyToMono(Item.class)
			.log("getItemUsingRetrieve");
	} 
	
	@GetMapping("/client/retrieve/singleItemUsingExchange")
	public Mono<Item> getItemUsingExchange() {
		return webClient.get().uri("/v1/items/{id}","123")
			.exchange()
			.flatMap(clientResponse->clientResponse.bodyToMono(Item.class))
			.log("getItemUsingExchange");
	}
	
	@PostMapping("/client/create")
	public Mono<Item> createItem(@RequestBody Item item) {
		return webClient.post().uri("/v1/items/")
			.body(Mono.just(item), Item.class)
			.exchange()
			.flatMap(clientResponse->clientResponse.bodyToMono(Item.class))
			.log("createItem");
	}
	
	@PutMapping("/client/update/{id}")
	public Mono<Item> updateItem(@PathVariable String id,@RequestBody Item item) {
		
		return webClient.put().uri("/v1/items/{id}",id)
			.body(Mono.just(item), Item.class)
			.exchange()
			.flatMap(clientResponse->clientResponse.bodyToMono(Item.class))
			.log("createItem");
	}
	
	@DeleteMapping("/client/delete/{id}")
	public Mono<Void> deleteItem(@PathVariable String id) {
		
		return webClient.delete().uri("/v1/items/{id}",id)
			.retrieve()
			.bodyToMono(Void.class)
			.log("item delete");
	}
	
	@GetMapping("/client/error/retrieve")
	public Flux<Item> retrieveError() {
		
		return webClient.get().uri("/v1/items/runtimeexception")
			.retrieve()
			.onStatus(HttpStatus::is5xxServerError, clientResponse->{
				return clientResponse.bodyToMono(String.class)
						.flatMap(errorMsg->{
							throw new RuntimeException(errorMsg);	
				});
			}).bodyToFlux(Item.class);
	}
	
	@GetMapping("/client/error/exchange")
	public Flux<Item> exchangeError() {
		
		return webClient.get().uri("/v1/items/runtimeexception")
			.exchange()
			.flatMapMany(clientResponse->{
				if(clientResponse.statusCode().is5xxServerError()){
					return clientResponse.bodyToMono(String.class)
						.flatMap(errorMsg->{
							log.error("exception caught ",errorMsg);
							throw new RuntimeException(errorMsg);
						});
				}else {
					return clientResponse.bodyToFlux(Item.class);
				}
			});
	}
}
