package com.github.dmitriylamzin.service;

import com.github.dmitriylamzin.model.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MessageConsumer {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private JavaMailSender mailSender;

  @JmsListener(destination = "outbox", containerFactory = "myFactory")
  public void receiveMessage(Email email) {
    sendEmail(email);
  }

  private void sendEmail(Email email) {
    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
    try {
      message.setSubject(email.getSubject());
      message.setFrom(email.getFrom());
      message.setTo(email.getTo());
      message.setText(email.getBody(), true /* isHtml */);
      this.mailSender.send(mimeMessage);

    } catch (MessagingException ex) {
      logger.error(ex.toString());
    }

  }
}
