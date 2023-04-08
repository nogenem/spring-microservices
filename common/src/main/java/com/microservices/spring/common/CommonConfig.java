package com.microservices.spring.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = { "classpath:common-application.properties" })
public class CommonConfig {

}
