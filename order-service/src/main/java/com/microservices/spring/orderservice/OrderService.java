package com.microservices.spring.orderservice;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.microservices.spring.inventoryservicecontracts.responses.InventoryQuantityResponse;
import com.microservices.spring.orderservice.clients.InventoryServiceClient;
import com.microservices.spring.orderservice.exceptions.OneOrMoreProductsOutOfStockException;
import com.microservices.spring.orderservice.exceptions.OrderWithThisOrderNumberNotFoundException;
import com.microservices.spring.orderservice.models.Order;
import com.microservices.spring.orderservicecontracts.requests.PlaceOrderLineItemRequest;
import com.microservices.spring.orderservicecontracts.requests.PlaceOrderRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {

  private OrderRepository orderRepository;
  private final MapStructMapper mapStructMapper;
  private final InventoryServiceClient inventoryServiceClient;

  public Order placeOrder(PlaceOrderRequest request, UUID userId) {
    // TODO: Get product prices from the product-service

    if (!areAllProductsInStock(request.getLineItems())) {
      throw new OneOrMoreProductsOutOfStockException();
    }

    Order order = mapStructMapper.placeOrderRequestToOrder(request);
    order.setUserId(userId);
    order.getLineItems().stream().forEach(item -> {
      item.setOrder(order);
      item.setProductPrice(1000); // TODO: Change this
    });

    orderRepository.save(order);

    log.info("Order saved. Id: {} - OrderNumber: {}", order.getId(), order.getOrderNumber());

    return order;
  }

  public Page<Order> findAll(Pageable pageOptions) {
    return orderRepository.findAll(pageOptions);
  }

  public Order findByOrderNumber(UUID orderNumber) {
    return orderRepository.findByOrderNumber(orderNumber)
        .orElseThrow(() -> new OrderWithThisOrderNumberNotFoundException(orderNumber));
  }

  private boolean areAllProductsInStock(List<PlaceOrderLineItemRequest> lineItems) {
    List<String> lineItemsSkus = lineItems.stream()
        .map(PlaceOrderLineItemRequest::getProductSku).toList();

    List<InventoryQuantityResponse> inventoriesQuantitiesResponse = inventoryServiceClient
        .findInventoriesQuantitiesBySkus(lineItemsSkus);

    Map<String, Integer> lineItemsSkuToQuantityMap = lineItems.stream()
        .collect(Collectors.toMap(PlaceOrderLineItemRequest::getProductSku, PlaceOrderLineItemRequest::getQuantity));

    return inventoriesQuantitiesResponse.stream()
        .allMatch(inventory -> inventory.getQuantity() - lineItemsSkuToQuantityMap.get(inventory.getSku()) >= 0);
  }

}
