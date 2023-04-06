package com.microservices.spring.orderservice.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseAuditingEntity {

  @Id
  @GeneratedValue
  public UUID id;

  @Column(name = "order_number", nullable = false)
  public UUID orderNumber;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderLineItem> lineItems;

  public void setLineItems(List<OrderLineItem> lineItems) {
    if (lineItems == null) {
      this.lineItems = null;
    } else {
      this.lineItems = lineItems;
      for (OrderLineItem lineItem : this.lineItems) {
        lineItem.setOrder(this);
      }
    }
  }

  public void addLineItem(OrderLineItem lineItem) {
    if (lineItems == null) {
      this.lineItems = new ArrayList<>();
    }

    lineItems.add(lineItem);
    lineItem.setOrder(this);
  }

  public void removeLineItem(OrderLineItem lineItem) {
    if (lineItems == null) {
      return;
    }

    lineItems.remove(lineItem);
    lineItem.setOrder(null);
  }

}