package by.training.user.command;

import by.training.command.ActionCommand;
import by.training.command.ActionCommandExecutionException;
import by.training.command.ActionCommandType;
import by.training.core.ServiceException;
import by.training.resourse.AttributesContainer;
import by.training.resourse.LocalizationManager;
import by.training.resourse.PathsContainer;
import by.training.servlet.HttpForwarder;
import by.training.servlet.HttpRedirector;
import by.training.servlet.HttpRouter;
import by.training.user.UserDto;
import by.training.user.UserService;
import by.training.validation.UserDataValidator;
import by.training.validation.ValidationException;
import by.training.validation.ValidationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

public class UpdateEmailCommand implements ActionCommand {

    private static final Logger LOGGER = LogManager.getLogger(UpdateEmailCommand.class);
    private final ActionCommandType type = ActionCommandType.UPDATE_EMAIL;
    private final UserService userService;


    public UpdateEmailCommand(UserService userService) {
        this.userService = userService;
    }


    @Override
    public ActionCommandType getType() {
        return type;
    }


    @Override
    public Optional<HttpRouter> direct(HttpServletRequest request, HttpServletResponse response) throws ActionCommandExecutionException {

        String email = request.getParameter(AttributesContainer.EMAIL.toString());


        LocalizationManager manager = new LocalizationManager(AttributesContainer.I18N.toString(),
                (String) request.getSession().getAttribute(AttributesContainer.LANGUAGE.toString()));


        UserDataValidator validator = new UserDataValidator(userService, manager);


        try {

            ValidationResult result = validator.emailCorrectness(email).and(validator.emailUniqueness(email));
            if (!result.isValid()) {
                request.setAttribute(AttributesContainer.MESSAGE.toString(),
                        manager.getValue(result.getFirstValue()));
                return Optional.of(new HttpForwarder(PathsContainer.FILE_USER_PROFILE_PAGE));
            }

            HttpSession httpSession = request.getSession();
            UserDto user = (UserDto) httpSession.getAttribute(AttributesContainer.USER.toString());
            user.setEmail(email);


            userService.update(user);

            return Optional.of(new HttpRedirector( request.getContextPath()
                    + PathsContainer.FILE_USER_PROFILE_PAGE));

        } catch (ServiceException | ValidationException e) {
            LOGGER.error("User updating failed.", e);
            throw new ActionCommandExecutionException("User updating failed.", e);
        }

    }


}
