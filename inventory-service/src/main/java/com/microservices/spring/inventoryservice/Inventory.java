package com.microservices.spring.inventoryservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Inventory extends BaseEntity {

  @Column(nullable = false, unique = true, length = 40)
  private String sku;

  @Column(nullable = false)
  private Integer quantity;

}
