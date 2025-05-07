package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Order;
import com.example.demo.repository.OrderRepo;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/orders")
public class CatalogServiceController {
	
	@Autowired
    private OrderRepo orderRepo;

    @PostConstruct
    public void initOrdersTable() {
        orderRepo.saveAll(Stream.of(
                        new Order("mobile", "electronics", "white", 20000),
                        new Order("T-Shirt", "clothes", "black", 999),
                        new Order("Jeans", "clothes", "blue", 1999),
                        new Order("Laptop", "electronics", "gray", 50000),
                        new Order("digital watch", "electronics", "black", 2500),
                        new Order("Fan", "electronics", "black", 50000)
                ).
                collect(Collectors.toList()));
    }

	@GetMapping
	public List<Order> getOrders(){
		return orderRepo.findAll();
	}
	
	@GetMapping("/{category}")
	public List<Order> getOrdersByCategory(@PathVariable("category") String category){
		return orderRepo.findByCategory(category);
	}

}
