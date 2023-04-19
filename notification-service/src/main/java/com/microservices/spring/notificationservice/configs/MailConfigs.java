package com.microservices.spring.notificationservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;

@org.springframework.context.annotation.Configuration
public class MailConfigs {

  @Bean
  public FreeMarkerConfigurer freemarkerClassLoaderConfig() {
    Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
    TemplateLoader templateLoader = new ClassTemplateLoader(this.getClass(), "/mail-templates");
    configuration.setTemplateLoader(templateLoader);
    FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
    freeMarkerConfigurer.setConfiguration(configuration);
    return freeMarkerConfigurer;
  }

}
