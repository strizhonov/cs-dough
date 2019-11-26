package by.training.validation;

import by.training.appentry.ApplicationConstantsContainer;
import by.training.service.PlayerService;
import by.training.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerDataValidator implements InputDataValidator {

    private static final Logger LOGGER = LogManager.getLogger(PlayerDataValidator.class);
    private PlayerService playerService;

    public PlayerDataValidator(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public ValidationResult validate(String... strings) throws ValidationException {
        checkInput(strings);
        int i = -1;
        return nicknameUniqueness(strings[++i]);
    }

    @Validation
    private ValidationResult nicknameUniqueness(String nickname) throws ValidationException {
        ValidationResult result = new ValidationResult();
        try {
            if (playerService.findByNickname(nickname) != null) {
                result.addIfAbsent(ApplicationConstantsContainer.NICKNAME_ERROR,
                        ApplicationConstantsContainer.NICKNAME_UNIQUENESS_ERROR_MESSAGE);
            }
        } catch (ServiceException e) {
            LOGGER.error("Player's name validation failed.", e);
            throw new ValidationException("Player's name validation failed.", e);
        }
        return result;
    }

}
