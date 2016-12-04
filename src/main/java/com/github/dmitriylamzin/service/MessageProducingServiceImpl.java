package com.github.dmitriylamzin.service;

import com.github.dmitriylamzin.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProducingServiceImpl implements MessageProducingService {

  @Autowired
  private JmsTemplate jmsTemplate;

  @Override
  public void sendMessage(String destination, Email email) {
    jmsTemplate.convertAndSend(destination, email);
  }
}
