package com.github.dmitriylamzin.buisness;

import com.github.dmitriylamzin.model.User;
import com.github.dmitriylamzin.repository.UserRepository;
import com.github.dmitriylamzin.buisness.RepeatedRegistrationException;
import com.github.dmitriylamzin.buisness.UserRegistrationServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRegistrationServiceImplTest {

  private final static User USER_TO_TEST = new User("test@test.com", "password", false);

  @MockBean
  private UserRepository userRepository;

  @Autowired
  private UserRegistrationServiceImpl userRegistrationService ;

  @Test
  public void shouldReturnSavedUser(){
    given(this.userRepository.exists(USER_TO_TEST.getEmail()))
            .willReturn(false);

    userRegistrationService.createAccount(USER_TO_TEST);

    verify(this.userRepository).save(USER_TO_TEST);
  }

  @Test (expected = RepeatedRegistrationException.class)
  public void shouldThrowRepeatedRegistrationException(){
    given(this.userRepository.exists(USER_TO_TEST.getEmail()))
            .willReturn(true);

    userRegistrationService.createAccount(USER_TO_TEST);
  }
}
