package com.microservices.spring.orderservice.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.microservices.spring.common.kafka.KafkaTopics;
import com.microservices.spring.inventoryservicecontracts.responses.InventoryQuantityResponse;
import com.microservices.spring.orderservice.MapStructMapper;
import com.microservices.spring.orderservice.OrderRepository;
import com.microservices.spring.orderservice.clients.InventoryServiceClient;
import com.microservices.spring.orderservice.clients.ProductServiceClient;
import com.microservices.spring.orderservice.exceptions.OneOrMoreProductsOutOfStockException;
import com.microservices.spring.orderservice.exceptions.OrderWithThisOrderNumberNotFoundException;
import com.microservices.spring.orderservice.models.Order;
import com.microservices.spring.orderservicecontracts.events.OrderPlacedEvent;
import com.microservices.spring.orderservicecontracts.requests.PlaceOrderLineItemRequest;
import com.microservices.spring.orderservicecontracts.requests.PlaceOrderRequest;
import com.microservices.spring.productservicecontracts.responses.ProductPriceResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {

  private KafkaProducerService kafkaService;
  private OrderRepository orderRepository;
  private final MapStructMapper mapStructMapper;

  private final InventoryServiceClient inventoryServiceClient;
  private final ProductServiceClient productServiceClient;

  public Order placeOrder(PlaceOrderRequest request, UUID userId) {
    if (!areAllProductsInStock(request.getLineItems())) {
      throw new OneOrMoreProductsOutOfStockException();
    }

    Map<String, Integer> productsSkuToPriceMap = findProductsPrices(request.getLineItems());

    Order order = mapStructMapper.placeOrderRequestToOrder(request);
    order.setUserId(userId);
    order.getLineItems().stream().forEach(item -> {
      item.setOrder(order);
      item.setProductPrice(productsSkuToPriceMap.get(item.getProductSku()));
    });

    orderRepository.save(order);

    UUID orderNumber = order.getOrderNumber();
    kafkaService.sendEventOnTopic(KafkaTopics.ORDER_TOPIC, orderNumber.toString(), new OrderPlacedEvent(orderNumber));

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

  private Map<String, Integer> findProductsPrices(List<PlaceOrderLineItemRequest> lineItems) {
    List<String> lineItemsSkus = lineItems.stream()
        .map(PlaceOrderLineItemRequest::getProductSku).toList();

    List<ProductPriceResponse> productPricesResponse = productServiceClient.findProductsPricesBySkus(lineItemsSkus);

    return productPricesResponse.stream()
        .collect(Collectors.toMap(ProductPriceResponse::getSku, ProductPriceResponse::getPrice));
  }

}
