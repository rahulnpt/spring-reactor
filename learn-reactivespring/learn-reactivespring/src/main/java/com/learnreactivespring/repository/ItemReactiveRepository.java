package com.learnreactivespring.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.learnreactivespring.document.Item;

import reactor.core.publisher.Flux;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String>{
	
	//we need to return always either FLux or Mono to keep it non blocking
	Flux<Item> findItemByDescription(String description);
	
}
