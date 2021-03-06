package by.training.user;

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
import by.training.validation.InputDataValidator;
import by.training.validation.ValidationException;
import by.training.validation.ValidationResult;
import by.training.validation.WalletDataValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;
import java.util.Optional;

public class DepositCommand implements ActionCommand {


    private static final Logger LOGGER = LogManager.getLogger(DepositCommand.class);
    private final ActionCommandType type = ActionCommandType.DEPOSIT;
    private final UserService userService;


    public DepositCommand(UserService userService) {
        this.userService = userService;
    }


    @Override
    public ActionCommandType getType() {
        return type;
    }


    @Override
    public Optional<HttpRouter> direct(HttpServletRequest request, HttpServletResponse response)
            throws ActionCommandExecutionException {

        String valueToDeposit = request.getParameter(AttributesContainer.DEPOSIT.toString());

        LocalizationManager manager = new LocalizationManager(AttributesContainer.I18N.toString(),
                (Locale) request.getSession().getAttribute(AttributesContainer.LANGUAGE.toString()));

        InputDataValidator<String> validator = new WalletDataValidator(manager);

        try {
            ValidationResult result = validator.validate(valueToDeposit);
            if (!result.isValid()) {
                request.setAttribute(AttributesContainer.MESSAGE.toString(), manager.getValue(result.getFirstKey()));
                return Optional.of(new HttpForwarder(PathsContainer.FILE_WALLET_PAGE));
            }

            HttpSession session = request.getSession();
            UserDto user = (UserDto) session.getAttribute(AttributesContainer.USER.toString());

            userService.deposit(Double.parseDouble(valueToDeposit), user);
            user = userService.find(user.getId());
            session.setAttribute(AttributesContainer.USER.toString(), user);

            return Optional.of(new HttpRedirector(request.getContextPath() + PathsContainer.FILE_WALLET_PAGE));

        } catch (ServiceException | ValidationException e) {
            LOGGER.error("Unable to perform depositing operation.", e);
            throw new ActionCommandExecutionException("Unable to perform depositing operation.", e);
        }

    }


}
