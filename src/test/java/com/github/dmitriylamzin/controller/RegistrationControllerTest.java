package com.github.dmitriylamzin.controller;

import com.github.dmitriylamzin.buisness.ConfirmationService;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class RegistrationControllerTest {

  private final static User USER_TO_TEST = new User("test@test.com", "password12!", false);

  @Autowired
  private MockMvc mvc;

  @MockBean
  private UserRegistrationService userRegistrationService;
  @MockBean
  private ConfirmationService confirmationService;


  @Test
  public void shouldReturnModelWithEmailFieldError() throws Exception {
    User userWithWrongEmail = new User("wrongEmail", "password12!", false);

    given(this.userRegistrationService.createAccount(USER_TO_TEST))
            .willReturn(USER_TO_TEST);


    this.mvc.perform(post("/registration/form")
            .param("email", userWithWrongEmail.getEmail())
            .param("password", userWithWrongEmail.getPassword()))
            .andExpect(status().isBadRequest())
            .andExpect(view().name("registrationForm"))
            .andExpect(model().errorCount(1))
            .andExpect(model().attributeHasFieldErrors("user", "email"));
  }

  @Test
  public void shouldReturnModelWithPasswordFieldError() throws Exception {
    User userWithWrongPass = new User("right@email", "password", false);

    this.mvc.perform(post("/registration/form")
            .param("email", userWithWrongPass.getEmail())
            .param("password", userWithWrongPass.getPassword()))
            .andExpect(status().isBadRequest())
            .andExpect(view().name("registrationForm"))
            .andExpect(model().errorCount(1))
            .andExpect(model().attributeHasFieldErrors("user", "password"));
  }

  @Test
  public void shouldReturnModelWithoutFieldErrors() throws Exception {
    User userWithWrongPass = new User("right@email", "password12!", false);

    this.mvc.perform(post("/registration/form")
            .param("email", userWithWrongPass.getEmail())
            .param("password", userWithWrongPass.getPassword()))
            .andExpect(status().isOk())
            .andExpect(view().name("greetingForm"))
            .andExpect(model().hasNoErrors());

  }

  @Test
  public void shouldReturnErrorPage() throws Exception {
    given(this.userRegistrationService.createAccount(any(User.class)))
            .willThrow(new RepeatedRegistrationException("user.already.registered"));

    this.mvc.perform(post("/registration/form")
            .param("email", USER_TO_TEST.getEmail())
            .param("password", USER_TO_TEST.getPassword()))
            .andExpect(status().isBadRequest())
            .andExpect(view().name("error"))
    ;

  }
}
