package by.training.tournament;

import by.training.command.ActionCommand;
import by.training.command.ActionCommandExecutionException;
import by.training.command.ActionCommandType;
import by.training.core.ApplicationContext;
import by.training.core.ServiceException;
import by.training.resourse.AppSetting;
import by.training.resourse.AttributesContainer;
import by.training.resourse.LocalizationManager;
import by.training.resourse.PathsContainer;
import by.training.servlet.HttpForwarder;
import by.training.servlet.HttpRedirector;
import by.training.servlet.HttpRouter;
import by.training.user.NotEnoughFundsException;
import by.training.user.UserDto;
import by.training.util.CommandMapper;
import by.training.util.ServletUtil;
import by.training.util.TournamentUtil;
import by.training.validation.InputDataValidator;
import by.training.validation.TournamentDataValidator;
import by.training.validation.ValidationException;
import by.training.validation.ValidationResult;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CreateTournamentCommand implements ActionCommand {

    private static final Logger LOGGER = LogManager.getLogger(CreateTournamentCommand.class);
    private final ActionCommandType type = ActionCommandType.CREATE_TOURNAMENT;
    private final TournamentService tournamentService;


    public CreateTournamentCommand(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }


    @Override
    public ActionCommandType getType() {
        return type;
    }


    @Override
    public Optional<HttpRouter> direct(HttpServletRequest request, HttpServletResponse response)
            throws ActionCommandExecutionException {

        HttpSession httpSession = request.getSession();
        UserDto user = (UserDto) httpSession.getAttribute(AttributesContainer.USER.toString());

        LocalizationManager manager = new LocalizationManager(AttributesContainer.I18N.toString(),
                (Locale) request.getSession().getAttribute(AttributesContainer.LANGUAGE.toString()));

        try {

            List<FileItem> items = ServletUtil.parseRequest(request);
            TournamentValidationDto validationDto = CommandMapper.mapTournamentValidationDto(items);

            InputDataValidator<TournamentValidationDto> validator
                    = new TournamentDataValidator(tournamentService, manager);

            ValidationResult result = validator.validate(validationDto);
            if (!result.isValid()) {
                request.setAttribute(AttributesContainer.MESSAGE.toString(), manager.getValue(result.getFirstKey()));
                return Optional.of(new HttpForwarder(PathsContainer.FILE_TOURNAMENT_CREATION_PAGE));
            }


            TournamentDto genericDto = compile(user, items, validationDto, request);

            long tournamentId = tournamentService.create(genericDto);

            return Optional.of(new HttpRedirector(request.getContextPath()
                    + PathsContainer.COMMAND_TO_TOURNAMENT_PAGE + tournamentId));


        } catch (NotEnoughFundsException e) {
            LOGGER.error("Not enough funds.", e);

            request.setAttribute(AttributesContainer.MESSAGE.toString(),
                    manager.getValue(AttributesContainer.NOT_ENOUGH_FUNDS.toString()));

            return Optional.of(new HttpForwarder(PathsContainer.COMMAND_SHOW_ORGANIZER + user.getOrganizerId()));

        } catch (ServiceException | FileUploadException | IOException | ValidationException e) {
            LOGGER.error("Tournament creation failed.", e);
            throw new ActionCommandExecutionException("Tournament creation failed.", e);
        }

    }


    private TournamentDto compile(UserDto user, List<FileItem> items, TournamentValidationDto validationDto,
                                  HttpServletRequest request) throws IOException {

        int i = -1;
        byte[] logo = items.get(++i).get();
        if (logo == null || logo.length == 0) {
            File file = new File(request.getServletContext().getRealPath(PathsContainer.FILE_DEF_TOURNAMENT_LOGO));
            InputStream is = new FileInputStream(file);
            logo = IOUtils.toByteArray(is);
        }

        String name = validationDto.getName();

        // From percentage input to rate
        double reward = Double.parseDouble(validationDto.getOrganizerRewardPercentage()) / 100;
        double bonus = Double.parseDouble(validationDto.getFromOrganizerBonus());
        double buyIn = Double.parseDouble(validationDto.getBuyIn());
        int playersNumber = Integer.parseInt(validationDto.getPlayersNumber());


        double prizePool = TournamentUtil.calculatePrizePool(reward, bonus, buyIn, playersNumber);


        return new TournamentDto.Builder()
                .logo(logo)
                .name(name)
                .prizePool(prizePool)
                .buyIn(buyIn)
                .reward(reward)
                .playersNumber(playersNumber)
                .organizerId(user.getOrganizerId())
                .build();
    }

}
