//package com.example.springsecurityapplication.services;
//
//import com.example.springsecurityapplication.models.Order;
//import com.example.springsecurityapplication.repositories.OrderRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@Transactional(readOnly = true)
//public class OrderService {
//    private final OrderRepository orderRepository;
//
//    public OrderService(OrderRepository orderRepository) {
//        this.orderRepository = orderRepository;
//    }
//
//    public List<Order> getAllOrder() {
//        return orderRepository.findAll();
//    }
//
//}
