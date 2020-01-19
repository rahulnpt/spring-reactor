package com.learnreactivespring.initialize;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;

import reactor.core.publisher.Flux;

@Component
@Profile("!test")//restricting this from inserting any data during test case execution
public class ItemDataInitializer implements CommandLineRunner{

	@Autowired
	ItemReactiveRepository itemReactiveRepository;
	
	@Override
	public void run(String... args) throws Exception {
		initializeData();		
	}

	public void initializeData() {
		List<Item> testItems = Arrays.asList(new Item(null, "TV", 10000.0),
				new Item(null, "WM", 20000.0),
				new Item(null, "Mac", 80000.0),
				new Item(null, "Mobile", 50000.0),
				new Item("123", "Mobile", 50000.0));//if we pass an id then it will use...otherwise generate one

		itemReactiveRepository.deleteAll()
			.thenMany(Flux.fromIterable(testItems))
			.flatMap(itemReactiveRepository::save) //saving items async
			.thenMany(itemReactiveRepository.findAll())
			.subscribe(System.out::println);
			
		
	}
}
