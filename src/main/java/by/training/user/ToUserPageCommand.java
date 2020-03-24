package by.training.user;

import by.training.command.ActionCommand;
import by.training.command.ActionCommandExecutionException;
import by.training.command.ActionCommandType;
import by.training.core.ServiceException;
import by.training.resourse.AttributesContainer;
import by.training.resourse.PathsContainer;
import by.training.servlet.HttpForwarder;
import by.training.servlet.HttpRouter;
import by.training.tournament.ListTournamentsCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class ToUserPageCommand implements ActionCommand {

    private static final Logger LOGGER = LogManager.getLogger(ListTournamentsCommand.class);
    private final ActionCommandType type = ActionCommandType.TO_USER_PAGE;
    private final UserService userService;


    public ToUserPageCommand(UserService userService) {
        this.userService = userService;
    }


    @Override
    public ActionCommandType getType() {
        return type;
    }


    @Override
    public Optional<HttpRouter> direct(HttpServletRequest request, HttpServletResponse response)
            throws ActionCommandExecutionException {


        String sId = request.getParameter(AttributesContainer.ID.toString());
        long id = Long.parseLong(sId);

        try {

            UserDto user = userService.find(id);
            request.setAttribute(AttributesContainer.USER.toString(), user);

            return Optional.of(new HttpForwarder(PathsContainer.FILE_USER_PROFILE_PAGE));

        } catch (ServiceException e) {
            LOGGER.error("Unable to get user with " + sId + " id.", e);
            throw new ActionCommandExecutionException("Unable to get user with " + sId + " id.", e);
        }
    }

}
