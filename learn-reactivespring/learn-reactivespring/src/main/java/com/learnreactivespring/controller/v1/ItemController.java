package com.learnreactivespring.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemController {

	@Autowired
	private ItemReactiveRepository itemReactiveRepo;
	
	@GetMapping(ItemConstants.ITEM_END_POINT_V1)
	public Flux<Item> getAllItems() {
		return itemReactiveRepo.findAll();
	}
	
	/*
	 * returning ResponseEntity because the given id may not be present and we might need to send another responseEntity 
	 * without any body
	 */
	@GetMapping(ItemConstants.ITEM_END_POINT_V1+"/{id}")
	public Mono<ResponseEntity<Item>> getItem(@PathVariable(required = true) String id) {
		return itemReactiveRepo.findById(id)
					.map(item->new ResponseEntity<Item>(item, HttpStatus.OK))
					.defaultIfEmpty(new ResponseEntity<Item>(HttpStatus.NO_CONTENT));
	}
	
	@PostMapping(ItemConstants.ITEM_END_POINT_V1)
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Item> createItem(@RequestBody Item item) {
		return itemReactiveRepo.save(item);
	}
	
	@DeleteMapping(ItemConstants.ITEM_END_POINT_V1+"/{id}")
	public Mono<Void> deleteItem(@PathVariable(required = true) String id) {
		return itemReactiveRepo.deleteById(id);
	}
	
	@GetMapping(ItemConstants.ITEM_END_POINT_V1+"/runtimeexception")
	public Flux<Item> generateRuntimeException() {
		return itemReactiveRepo.findAll()
					.concatWith(Mono.error(()->new RuntimeException("Runtime exception occured")));
	}
	
	@PutMapping(ItemConstants.ITEM_END_POINT_V1+"/{id}")
	public Mono<ResponseEntity<Item>> updateItem(@RequestBody(required = true) Item item,@PathVariable(required = true) String id) {
		return itemReactiveRepo.findById(id)
			.flatMap((retrievedItem)->{//using flatmap to get this task done async
				retrievedItem.setDescription(item.getDescription());
				retrievedItem.setPrice(item.getPrice());
				return itemReactiveRepo.save(retrievedItem);
			})
			.map(updateItem->new ResponseEntity<Item>(updateItem,HttpStatus.OK))
			.defaultIfEmpty(new ResponseEntity<Item>(HttpStatus.NO_CONTENT));
	}
}
