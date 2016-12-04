package com.github.dmitriylamzin.controller;

import com.github.dmitriylamzin.buisness.ConfirmationService;
import com.github.dmitriylamzin.buisness.FailedToConfirmException;
import com.github.dmitriylamzin.buisness.UserRegistrationService;
import com.github.dmitriylamzin.model.User;
import com.github.dmitriylamzin.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
public class ConfirmationController {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private UserRegistrationService userRegistrationService;
  @Autowired
  private ConfirmationService confirmationService;
  @Autowired
  private UserRepository userRepository;

  /**
   * Method for confirmation of registration of particular user.
   * <p>If database contains user with this data, and confirmation is complete,
   * then redirection with user attributes performs to the /success method.</p>
   *
   * @param confirmationLink a string with encrypted user data.
   * @param redirectAttributes attributes to store object during redirection operation.
   *
   * @return redirection string to success method.
   * */
  @GetMapping("/confirm/{confirmationLink:.+}")
  public String confirmRegistration(@PathVariable String confirmationLink, RedirectAttributes redirectAttributes) {
    logger.info("\"Confirm registration\" method was called");

    User user = confirmationService.confirmRegistration(confirmationLink);
    logger.info("confirmation succeeded");
    redirectAttributes.addFlashAttribute("user", user);
    return "redirect:/success";

  }

  /**
   * Method returns successPage if user us confirmed.
   *
   * @param user a user data to identify confirmation.
   * @param locale particular local for template resolving.
   *
   * @return name of page with success content.
   *
   * @throws NotAuthorizedAccessException if user didn't confirm registration.
   * */
  @GetMapping("/success")
  public String getSuccessPage(User user, Locale locale) {
    if (user.isConfirmed()) {
      return "successPage";
    } else {
      throw new NotAuthorizedAccessException("user.not.authorized");
    }
  }

  /**
   * Takes control if FailedToConfirm exception occurred.
   * <p>Sets response status to HttpStatus.BAD_REQUEST, adds localized message to the view.</p>
   *
   * @param exception an exception which has occurred.
   *
   * @return error page with localized message of cause of exception.
   * */
  @ExceptionHandler(FailedToConfirmException.class)
  public ModelAndView handleConfirmationException(FailedToConfirmException exception) {
    logger.info("failed to confirm: " + exception.getLocalizedMessage());
    ModelAndView model = new ModelAndView();
    model.setStatus(HttpStatus.BAD_REQUEST);
    model.addObject("msg", exception.getLocalizedMessage());
    model.setViewName("error");
    return model;
  }

  /**
   * Takes control if NotAuthorizedAccessException exception occurred.
   * <p>Sets response status to HttpStatus.UNAUTHORIZED, adds localized message to the view.</p>
   *
   * @param exception an exception which has occurred.
   *
   * @return error page with localized message of cause of exception.
   * */
  @ExceptionHandler(NotAuthorizedAccessException.class)
  public ModelAndView handleNotAuthorizedAccessException(NotAuthorizedAccessException exception) {
    logger.info("user is not authorized: " + exception.getLocalizedMessage());
    ModelAndView model = new ModelAndView();
    model.setStatus(HttpStatus.UNAUTHORIZED);
    model.addObject("msg", exception.getLocalizedMessage());
    model.setViewName("error");
    return model;
  }
}
