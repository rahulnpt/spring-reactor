package com.learnreactivespring.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.learnreactivespring.document.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest//Using this annotation will disable full auto-configuration and instead apply only configuration relevant to MongoDB tests.
@ExtendWith(SpringExtension.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {

	@Autowired
	private ItemReactiveRepository itemReactiveRepository;

	List<Item> testItems = Arrays.asList(new Item(null, "TV", 10000.0),
			new Item(null, "WM", 20000.0),
			new Item(null, "Mac", 80000.0),
			new Item(null, "Mobile", 50000.0),
			new Item("123", "Mobile", 50000.0));//if we pass an id then it will use...otherwise generate one

	@BeforeEach
	public void setup() {
		itemReactiveRepository.deleteAll()//deleting all data for making correct assertions
			.thenMany(Flux.fromIterable(testItems))
			.flatMap(itemReactiveRepository::save)
			.doOnNext(item->System.out.println("saved Item is "+item))
			.blockLast();//this will ensure all the items are inserted before the test case starts. Never use this in the actual code. Meant for test cases only
	}

	@Test
	public void getAllItems() {
		StepVerifier.create(itemReactiveRepository.findAll())
			.expectSubscription()
			.expectNextCount(5)
			.verifyComplete();
	}
	
	@Test
	public void getItemById() {
		StepVerifier.create(itemReactiveRepository.findById("123"))
			.expectSubscription()
			.expectNextMatches(item->item.getDescription().equalsIgnoreCase("Mobile"))
			.verifyComplete();
	}

	@Test
	public void getItemByDescription() {
		StepVerifier.create(itemReactiveRepository.findItemByDescription("Mobile").log("findItemByDescription :"))
			.expectSubscription()
			.expectNextCount(2)
			.verifyComplete();
	}
	
	@Test
	public void saveItem() {
		StepVerifier.create(itemReactiveRepository.save(new Item(null, "Socks", 10.0)).log("saveItem :"))
			.expectSubscription()
			.expectNextMatches(item->item!=null && item.getDescription().equals("Socks"))
			.verifyComplete();
	}
	
	@Test
	public void updateItem() {
		
		Flux<Item> updatedItem = itemReactiveRepository.findItemByDescription("Mac")
		.map(item->{
			item.setPrice(15.0);
			return item;
		})
		.flatMap(item->itemReactiveRepository.save(item)).log("updateItem"); //using flatmap just to get this done async...otherwise map is also fine
		
		StepVerifier.create(updatedItem)
		.expectSubscription()
		.expectNextMatches(item->item.getPrice().equals(15.0))
		.verifyComplete();
				
	}
	
	@Test
	public void deleteItem() {
		Mono<Void> deletedItem = itemReactiveRepository.deleteById("123");
		
		StepVerifier.create(deletedItem)
			.expectSubscription()
			.verifyComplete();
	}
}
