package com.github.dmitriylamzin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class RegistrationServiceApplication {
  public static final String VIEW_TEMPLATE_ENCODING = "UTF-8";

  /**
   * Configuring the message source for view templates.
   * */
  @Bean
  public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasenames("view/validationMessages", "view/viewMessages");
    messageSource.setDefaultEncoding(VIEW_TEMPLATE_ENCODING);
    return messageSource;
  }

  public static void main(String[] args) {
    SpringApplication.run(RegistrationServiceApplication.class, args);
  }
}
