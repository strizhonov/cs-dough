package by.training.command.impl;

import by.training.command.ActionCommand;
import by.training.command.ActionCommandExecutionException;
import by.training.command.ActionCommandType;
import by.training.constant.AttributesContainer;
import by.training.service.PlayerService;
import by.training.service.ServiceException;
import by.training.servlet.ServletRouter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JoinTournamentCommand implements ActionCommand {

    private static final Logger LOGGER = LogManager.getLogger(JoinTournamentCommand.class);
    private final ActionCommandType type = ActionCommandType.JOIN_TOURNAMENT;
    private PlayerService playerService;

    public JoinTournamentCommand(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public ServletRouter execute(HttpServletRequest request, HttpServletResponse response) throws ActionCommandExecutionException {
        String sPlayerId = request.getParameter(AttributesContainer.PLAYER_ID.toString());
        long playerId = Long.parseLong(sPlayerId);
        String sTournamentId = request.getParameter(AttributesContainer.TOURNAMENT_ID.toString());
        long tournamentId = Long.parseLong(sTournamentId);
        try {
            playerService.join(playerId, tournamentId);
        } catch (ServiceException e) {
            LOGGER.error("Unable to perform tournament joining.", e);
            throw new ActionCommandExecutionException("Unable to perform tournament joining.", e);
        }
        return new ServletRouter("/jsp/tournament-page.jsp");
    }

    @Override
    public ActionCommandType getType() {
        return type;
    }
}