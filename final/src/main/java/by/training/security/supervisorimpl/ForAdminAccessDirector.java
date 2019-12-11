package by.training.security.supervisorimpl;

import by.training.constant.AttributesContainer;
import by.training.constant.PathsContainer;
import by.training.security.AccessAllowedForType;
import by.training.servlet.HttpForwarder;
import by.training.servlet.HttpRedirector;
import by.training.servlet.HttpRouter;
import by.training.user.User;
import by.training.user.UserDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ForAdminAccessDirector extends BaseSecurityDirector {

    private static final String REDIRECT_TO = PathsContainer.ACCESS_DENIED;
    private final AccessAllowedForType type = AccessAllowedForType.ADMIN;

    @Override
    public AccessAllowedForType getType() {
        return type;
    }

    @Override
    protected boolean isAccessAllowed(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserDto user = (UserDto) session.getAttribute(AttributesContainer.USER.toString());
        return user != null && user.getType() == User.UserType.ADMIN;
    }

    @Override
    protected HttpRouter getHttpRouter(HttpServletRequest request, HttpServletResponse response) {
        return new HttpRedirector(request.getContextPath() + REDIRECT_TO);
    }

}