package com.github.dmitriylamzin.buisness;

import com.github.dmitriylamzin.buisness.FailedToConfirmException;
import com.github.dmitriylamzin.model.Email;
import com.github.dmitriylamzin.model.User;
import com.github.dmitriylamzin.repository.UserRepository;
import com.github.dmitriylamzin.buisness.ConfirmationServiceImpl;
import com.github.dmitriylamzin.service.EncryptionService;
import com.github.dmitriylamzin.service.MessageProducingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.ITemplateEngine;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfirmationServiceImplTest {

  private final static User USER_TO_TEST = new User("test@test.com", "password", false);

  @MockBean
  private UserRepository userRepository;
  @MockBean
  private MessageProducingService messageProducingService;
  @MockBean
  private EncryptionService encryptionService;
  @MockBean(name = "emailTemplateEngine")
  private ITemplateEngine emailTemplateEngine;

  @Autowired
  private ConfirmationServiceImpl confirmationService;

  @Test
  public void shouldSendEmailWithConfirmationData(){
    String mockedEmailBody = "mockedBody";

    given(this.emailTemplateEngine.process(anyString(), anyObject()))
            .willReturn(mockedEmailBody);

    ArgumentCaptor<Email> emailArgumentCaptor = ArgumentCaptor.forClass(Email.class);

    confirmationService.requestConfirmation(USER_TO_TEST, Locale.ENGLISH);

    verify(this.messageProducingService).sendMessage(anyString(), emailArgumentCaptor.capture());

    assertEquals(mockedEmailBody, emailArgumentCaptor.getValue().getBody());
  }

  @Test
  public void shouldConfirmUserRegistration() throws Exception {
    String encryptedData = "someEncryptedData";
    String userDataDivider = "&deviderSign&";
    String decryptedData = USER_TO_TEST.getEmail() + userDataDivider + USER_TO_TEST.getPassword();

    given(this.encryptionService.decrypt(encryptedData))
            .willReturn(decryptedData);
    given(this.userRepository.findOne(USER_TO_TEST.getEmail()))
            .willReturn(USER_TO_TEST);

    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

    confirmationService.confirmRegistration(encryptedData);

    verify(this.userRepository).save(userArgumentCaptor.capture());

    assertTrue(userArgumentCaptor.getValue().isConfirmed());
  }

  @Test(expected = FailedToConfirmException.class)
  public void shouldDeclineConfirmationUserRegistration() throws Exception {
    String encryptedData = "someEncryptedData";
    String userDataDivider = "&deviderSign&";
    String wrongPasswordToCheck = "wrongPassword";
    String decryptedData = USER_TO_TEST.getEmail() + userDataDivider + wrongPasswordToCheck;

    given(this.encryptionService.decrypt(encryptedData))
            .willReturn(decryptedData);
    given(this.userRepository.findOne(USER_TO_TEST.getEmail()))
            .willReturn(USER_TO_TEST);

    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    User user = confirmationService.confirmRegistration(encryptedData);
  }

  @Test(expected = FailedToConfirmException.class)
  public void shouldDeclineConfirmationBecausUserIsNull() throws Exception {
    String encryptedData = "someEncryptedData";
    String userDataDivider = "&deviderSign&";
    String wrongPasswordToCheck = "wrongPassword";
    String decryptedData = USER_TO_TEST.getEmail() + userDataDivider + wrongPasswordToCheck;

    given(this.encryptionService.decrypt(encryptedData))
            .willReturn(decryptedData);
    given(this.userRepository.findOne(USER_TO_TEST.getEmail()))
            .willReturn(null);

    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    User user = confirmationService.confirmRegistration(encryptedData);
  }
}
