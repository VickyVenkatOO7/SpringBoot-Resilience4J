package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Order;

public interface OrderRepo extends JpaRepository<Order, Integer>{

	List<Order> findByCategory(String category);
	
}
