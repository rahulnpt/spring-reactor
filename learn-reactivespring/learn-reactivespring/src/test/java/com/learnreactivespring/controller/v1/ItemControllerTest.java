package com.learnreactivespring.controller.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

	@Autowired
	ItemReactiveRepository itemReactiveRepository;
	
	@Autowired
	WebTestClient webTestClient;
	
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
		webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Item.class)
			.hasSize(5);
			
	}
	
	@Test
	public void getAllItems_approach2() {
		webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Item.class)
			.hasSize(5)
			.consumeWith(response->{
				List<Item> items = response.getResponseBody();
				items.forEach((item)->{
					assertNotNull(item.getId());
				});
			});
	}
	
	@Test
	public void getAllItems_approach3() {
		Flux<Item> responseBody = webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.returnResult(Item.class)
			.getResponseBody();
		
		StepVerifier.create(responseBody.log("value from network : "))
			.expectSubscription()
			.expectNextCount(5)
			.verifyComplete();
	}
	
	@Test
	public void getItem() {
		webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1+"/{id}","123")
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.price", 50000.0);
	}
	@Test
	public void getItem_notFound() {
		webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1+"/{id}","234")
			.exchange()
			.expectStatus().isNoContent();
	}
	
	@Test
	public void createItemTest() {
		Item item = new Item(null, "test item", 410.0);
		webTestClient.post().uri(ItemConstants.ITEM_END_POINT_V1)
			.contentType(MediaType.APPLICATION_JSON)
			.body(Mono.just(item), Item.class)
			.exchange()
			.expectStatus().isCreated()
			.expectBody(Item.class)
			.consumeWith(response->{
				assertTrue(response.getResponseBody().getId()!=null);
				System.out.println("created item :"+response.getResponseBody().getId());
			});
	}
	
	@Test
	public void createItemTest_approach2() {
		Item item = new Item(null, "test item", 410.0);
		webTestClient.post().uri(ItemConstants.ITEM_END_POINT_V1)
			.contentType(MediaType.APPLICATION_JSON)
			.body(Mono.just(item), Item.class)
			.exchange()
			.expectStatus().isCreated()
			.expectBody()
			.jsonPath("$.id").isNotEmpty()
			.jsonPath("$.description").isEqualTo("test item");
	}
	
	@Test
	public void deleteItemTest() {
		webTestClient.delete().uri(ItemConstants.ITEM_END_POINT_V1+"/{id}","123")
			.exchange()
			.expectStatus().isOk()
			.expectBody(Void.class);
	}
	
	@Test
	public void updateItem() {
		Item item  = new Item("123", "updated desc", 145.02);
		webTestClient.put().uri(ItemConstants.ITEM_END_POINT_V1+"/{id}","123")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.body(Mono.just(item), Item.class)
			.exchange()
			.expectStatus().isOk()
			.expectBody(Item.class)
			.consumeWith(response->{
				assertEquals(response.getResponseBody().getDescription(), "updated desc");
			});
	}
	
	@Test
	public void updateItem_approach2() {
		Item item  = new Item("123", "updated desc", 145.02);
		webTestClient.put().uri(ItemConstants.ITEM_END_POINT_V1+"/{id}","123")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.body(Mono.just(item), Item.class)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.description", "updated desc");
	}
	
	@Test
	public void updateItem_invalidId() {
		Item item  = new Item("123", "updated desc", 145.02);
		webTestClient.put().uri(ItemConstants.ITEM_END_POINT_V1+"/{id}","1234")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.body(Mono.just(item), Item.class)
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();
	}
	
	@Test
	public void runtimeException() {
		webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1+"/runtimeexception")
			.exchange()
			.expectStatus().is5xxServerError()
			.expectBody(String.class)
			.isEqualTo("Runtime exception occured");
	}
}
