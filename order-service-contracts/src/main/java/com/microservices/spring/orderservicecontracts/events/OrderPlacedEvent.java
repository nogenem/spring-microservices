package com.microservices.spring.orderservicecontracts.events;

import java.util.UUID;

import com.microservices.spring.common.kafka.IKafkaEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPlacedEvent implements IKafkaEvent {

  private UUID orderNumber;
  private UUID userId;

}
