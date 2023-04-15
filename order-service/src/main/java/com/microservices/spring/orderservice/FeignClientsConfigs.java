package com.microservices.spring.orderservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Capability;
import feign.Logger;
import feign.micrometer.MicrometerCapability;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class FeignClientsConfigs {

  @Bean
  public Logger.Level loggerLevel() {
    return Logger.Level.FULL;
  }

  @Bean
  public Capability capability(final MeterRegistry registry) {
    return new MicrometerCapability(registry);
  }

}
