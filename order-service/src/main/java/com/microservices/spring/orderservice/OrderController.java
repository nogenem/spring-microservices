package com.microservices.spring.orderservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.spring.orderservice.models.Order;
import com.microservices.spring.orderservice.requests.StoreOrderRequest;
import com.microservices.spring.orderservice.responses.OrderResponse;
import com.microservices.spring.orderservice.responses.PagedOrderResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/orders")
@Validated
@Tag(name = "Order API")
public class OrderController {

  private final OrderService orderService;
  private final MapStructMapper mapStructMapper;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Place an order")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Order placed"),
      @ApiResponse(responseCode = "422", description = "Validation errors", content = @Content) })
  public OrderResponse placeOrder(@Valid @RequestBody StoreOrderRequest request) {
    Order order = orderService.placeOrder(request);

    return mapStructMapper.orderToOrderResponse(order);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Get a page of orders")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "A page of orders"),
      @ApiResponse(responseCode = "422", description = "Validation errors", content = @Content) })
  public PagedOrderResponse findAllOrdersPaged(
      @RequestParam(defaultValue = "0") @Min(value = 0) int page,
      @RequestParam(defaultValue = "30") @Min(value = 5) int size,
      @RequestParam(defaultValue = "-createdAt") String sort) {
    Sort sortBy = getSortBy(sort);
    Pageable pageOptions = PageRequest.of(page, size, sortBy);

    Page<Order> pagedOrder = orderService.findAll(pageOptions);

    return mapStructMapper.pagedOrderToPagedOrderResponse(pagedOrder);
  }

  private Sort getSortBy(String sort) {
    Sort.Direction dir = Sort.Direction.ASC;
    if (sort.startsWith("-")) {
      dir = Sort.Direction.DESC;
      sort = sort.substring(1);
    } else if (sort.startsWith("+")) {
      sort = sort.substring(1);
    }

    return Sort.by(dir, sort);
  }

}
