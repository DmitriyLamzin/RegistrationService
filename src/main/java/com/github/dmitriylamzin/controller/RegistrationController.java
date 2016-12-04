package com.github.dmitriylamzin.controller;

import com.github.dmitriylamzin.buisness.ConfirmationService;
import com.github.dmitriylamzin.buisness.RepeatedRegistrationException;
import com.github.dmitriylamzin.buisness.UserRegistrationService;
import com.github.dmitriylamzin.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Locale;
import javax.validation.Valid;

@Controller
@RequestMapping("/registration")
public class RegistrationController extends WebMvcConfigurerAdapter {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private UserRegistrationService userRegistrationService;
  @Autowired
  private ConfirmationService confirmationService;


  @GetMapping
  public String getFirstPage() {
    return "index";
  }

  @GetMapping("/form")
  public String getRegistrationForm(User user) {
    return "registrationForm";
  }

  /**
   * Method to register new user in the service.
   * <p>User fields have next constrains:</p>
   * <ul>
   *   <li>email field should be a valid email.</li>
   *   <li>password field should have at least two digits and one exclamation mark.</li>
   * </ul>
   * <p>If there is no constraint violations, then user will be stored in the data base.</p>
   * @param user user fields: email and password.
   * @param bindingResult parameter to store constraint violation errors.
   * @param locale locale to chose client language.
   * @param model model to store response data
   *
   * @return if there are constraint violations, then registration form will be returned
   *        to give user one more attempt. If there is no violations, then greeting form will be returned.
   * */
  @PostMapping("/form")
  public ModelAndView registerUser(@Valid User user,
                                   BindingResult bindingResult,
                                   Locale locale,
                                   ModelAndView model) {

    logger.info("\"Register User\" method was called");
    if (bindingResult.hasErrors()) {
      logger.info("Binding result has errors for user: " + user);
      for (FieldError fieldError: bindingResult.getFieldErrors()) {
        logger.debug("Binding result" + fieldError.toString());
      }
      model.setStatus(HttpStatus.BAD_REQUEST);
      model.setViewName("registrationForm");
      return model;
    }

    userRegistrationService.createAccount(user);
    confirmationService.requestConfirmation(user, locale);
    model.setViewName("greetingForm");

    return model;
  }

  /**
   * Takes control if RepeatedRegistrationException exception occurred.
   * <p>Sets response status to HttpStatus.BAD_REQUEST, adds localized message to the view.</p>
   *
   * @param exception an exception which has occurred.
   *
   * @return error page with localized message of cause of exception.
   * */
  @ExceptionHandler(RepeatedRegistrationException.class)
  public ModelAndView handleRepeatedRegistrationException(RepeatedRegistrationException exception) {
    logger.info("user with this data is already created to confirm: " + exception.getLocalizedMessage());
    ModelAndView model = new ModelAndView();
    model.setStatus(HttpStatus.BAD_REQUEST);
    model.addObject("msg", exception.getLocalizedMessage());
    model.setViewName("error");
    return model;
  }
}


