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

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

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
    @TimeLimiter(name = USER_SERVICE, fallbackMethod = "getAllAvailableProducts")
    public CompletableFuture<List<OrderDTO>> displayOrders(@RequestParam("category") String category) {
    	return CompletableFuture.supplyAsync(() -> {
    		String url = (category == null || category.isEmpty()) ? BASEURL : BASEURL + "/" + category;
        	System.out.println("TimeLimiter method called " + attempt++ + " times " + "on " + new Date());
        	return restTemplate.getForObject(url, ArrayList.class);
    	});
    }


    public CompletableFuture<List<OrderDTO>> getAllAvailableProducts(Exception e){
    	System.out.println("Fallback due to TimeLimiter: " + e.getMessage());
        List<OrderDTO> fallbackOrders = Stream.of(
                new OrderDTO(119, "LED TV", "electronics", "white", 45000),
                new OrderDTO(345, "Headset", "electronics", "black", 7000),
                new OrderDTO(475, "Sound bar", "electronics", "black", 13000),
                new OrderDTO(574, "Puma Shoes", "foot wear", "black & white", 4600),
                new OrderDTO(678, "Vegetable chopper", "kitchen", "blue", 999),
                new OrderDTO(532, "Oven Gloves", "kitchen", "gray", 745)
        ).collect(Collectors.toList());
        return CompletableFuture.completedFuture(fallbackOrders);
    }
}
