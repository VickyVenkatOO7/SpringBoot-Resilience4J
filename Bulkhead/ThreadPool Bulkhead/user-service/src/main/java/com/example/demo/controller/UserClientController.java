package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.entity.OrderDTO;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;

@RestController
@RequestMapping("/user-service")
public class UserClientController {
	
	@Autowired
    @Lazy
    private RestTemplate restTemplate;

    public static final String USER_SERVICE="userService";

    private static final String BASEURL = "http://localhost:9191/orders";

    private int attempt = 1;


    @GetMapping("/displayOrders")
    @Bulkhead(name = USER_SERVICE, type = Bulkhead.Type.THREADPOOL, fallbackMethod = "getAllAvailableProducts")
    public CompletableFuture<List<OrderDTO>> displayOrders(@RequestParam("category") String category) {
    	return CompletableFuture.supplyAsync(() -> {
    		String url = (category == null || category.isEmpty()) ? BASEURL : BASEURL + "/" + category;
    		try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
	            throw new RuntimeException("Thread was interrupted", e);
			}
            System.out.println("ThreadPool Bulkhead method called " + attempt++ + " times " + "on " + new Date());
            return restTemplate.getForObject(url, ArrayList.class);
    	});
    }


    public CompletableFuture<List<OrderDTO>> getAllAvailableProducts(String category, Throwable t){
    	System.out.println("Fallback due to ThreadPool Bulkhead: " + t.getMessage());
        return CompletableFuture.supplyAsync(() -> Stream.of(
        		new OrderDTO(119, "LED TV", "electronics", "white", 45000),
                new OrderDTO(345, "Headset", "electronics", "black", 7000),
                new OrderDTO(475, "Sound bar", "electronics", "black", 13000),
                new OrderDTO(574, "Puma Shoes", "foot wear", "black & white", 4600),
                new OrderDTO(678, "Vegetable chopper", "kitchen", "blue", 999),
                new OrderDTO(532, "Oven Gloves", "kitchen", "gray", 745)
        ).collect(Collectors.toList()));
    }
}
