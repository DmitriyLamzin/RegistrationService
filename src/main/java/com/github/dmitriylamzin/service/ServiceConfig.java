package com.github.dmitriylamzin.service;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;
import java.util.Properties;
import javax.jms.ConnectionFactory;

@Configuration
@PropertySource("classpath:security/encryption.properties")
public class ServiceConfig implements ApplicationContextAware, EnvironmentAware {

  private static final String JAVA_MAIL_FILE = "classpath:mail/javamail.properties";

  private static final String HOST = "mail.server.host";
  private static final String PORT = "mail.server.port";
  private static final String PROTOCOL = "mail.server.protocol";
  private static final String USERNAME = "mail.server.username";
  private static final String PASSWORD = "mail.server.password";

  private ApplicationContext applicationContext;
  private Environment environment;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }


  /**
   * Set up of mail sender.
   * <p>Host, Port, Protocol, Username, Password, additional javamail properties.</p>
   * */
  @Bean
  public JavaMailSender mailSender() throws IOException {
    final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    // Basic mail sender configuration, based on emailconfig.properties
    mailSender.setHost(this.environment.getProperty(HOST));
    mailSender.setPort(Integer.parseInt(this.environment.getProperty(PORT)));
    mailSender.setProtocol(this.environment.getProperty(PROTOCOL));
    mailSender.setUsername(this.environment.getProperty(USERNAME));
    mailSender.setPassword(this.environment.getProperty(PASSWORD));

    // JavaMail-specific mail sender configuration, based on javamail.properties
    final Properties javaMailProperties = new Properties();
    javaMailProperties.load(this.applicationContext.getResource(JAVA_MAIL_FILE).getInputStream());
    mailSender.setJavaMailProperties(javaMailProperties);

    return mailSender;

  }

  /**
   * Set up JmsListenerContainerFactory.
   * */
  @Bean
  public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
                                                  DefaultJmsListenerContainerFactoryConfigurer configurer) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    // This provides all boot's default to this factory, including the message converter
    configurer.configure(factory, connectionFactory);
    // You could still override some of Boot's default if necessary.
    return factory;
  }

  /**
   * Serialize message content to json using TextMessage.
   * */
  @Bean
  public MessageConverter jacksonJmsMessageConverter() {
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("_type");
    return converter;
  }
}
