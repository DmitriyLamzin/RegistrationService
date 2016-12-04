package com.github.dmitriylamzin.buisness;

import com.github.dmitriylamzin.model.User;
import com.github.dmitriylamzin.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  private UserRepository userRepository;

  @Autowired
  public UserRegistrationServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User createAccount(User user) {
    if (!userRepository.exists(user.getEmail())) {
      logger.info("user with email " + user.getEmail() + " does not exist");
      return userRepository.save(user);
    } else {
      logger.info("user with email " + user.getEmail() + " already exists");
      throw new RepeatedRegistrationException("user.already.registered");
    }
  }
}
