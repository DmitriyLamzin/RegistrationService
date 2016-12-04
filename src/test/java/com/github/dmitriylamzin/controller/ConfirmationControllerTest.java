package com.github.dmitriylamzin.controller;

import com.github.dmitriylamzin.buisness.ConfirmationService;
import com.github.dmitriylamzin.buisness.FailedToConfirmException;
import com.github.dmitriylamzin.buisness.RepeatedRegistrationException;
import com.github.dmitriylamzin.buisness.UserRegistrationService;
import com.github.dmitriylamzin.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ConfirmationControllerTest {

  private final static User USER_TO_TEST = new User("test@test.com", "password12!", false);

  @Autowired
  private MockMvc mvc;

  @MockBean
  private ConfirmationService confirmationService;

  @Test
  public void shouldRedirectToSuccessControllerMethod() throws Exception {
    String encryptedUserData = "encryptedUserData";

    given(this.confirmationService.confirmRegistration(encryptedUserData))
            .willReturn(USER_TO_TEST);

    this.mvc.perform(get("/confirm/" + encryptedUserData))
            .andExpect(redirectedUrl("/success"));
  }


  @Test
  public void shouldReturnFailedToConfirmErrorPage() throws Exception {
    String encryptedUserData = "encryptedUserData";

    given(this.confirmationService.confirmRegistration(encryptedUserData))
            .willThrow(new FailedToConfirmException("User.not.found"));

    this.mvc.perform(get("/confirm/" + encryptedUserData))
            .andExpect(status().isBadRequest())
            .andExpect(view().name("error"));
  }


  @Test
  public void shouldReturnSuccessPage() throws Exception {
    User confirmedUser = new User("test@test.com", "password12!", true);

    this.mvc.perform(get("/success").flashAttr("user", confirmedUser))
            .andExpect(view().name("successPage"));
  }

  @Test
  public void shouldReturnNotAuthorizedErrorPage() throws Exception {
    User unauthorizedUser = new User("test@test.com", "password12!", false);


    this.mvc.perform(get("/success").flashAttr("user", unauthorizedUser))
            .andExpect(status().isUnauthorized())
            .andExpect(view().name("error"));
  }
}
