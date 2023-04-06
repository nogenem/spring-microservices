package com.microservices.spring.orderservice.models;

import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "order_line_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderLineItem {

  @Id
  @GeneratedValue
  public UUID id;

  @Column(name = "product_sku", nullable = false, length = 40)
  private String productSku;

  @Column(name = "product_price", nullable = false)
  private Integer productPrice;

  @Column(nullable = false)
  private Integer quantity;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @ToString.Exclude
  private Order order;

}
