package by.training.util;

import by.training.game.GameServerDto;
import by.training.game.PlainGameDto;
import by.training.organizer.OrganizerDto;
import by.training.player.PlayerDto;
import by.training.tournament.Tournament;
import by.training.tournament.TournamentDto;
import by.training.user.User;
import by.training.user.UserDto;
import by.training.user.Wallet;
import by.training.user.WalletDto;

import java.sql.ResultSet;
import java.sql.SQLException;

import static by.training.util.DaoMapper.Column.*;

public class DaoMapper {

    public enum Column {
        GAME_ID, BRACKET_INDEX, START_TIME, END_TIME, GAME_TYPE, FIRST_PLAYER_ID, SECOND_PLAYER_ID, TOURNAMENT_ID,
        GAME_SERVER_ID, POINTS_TO_WIN, PLAYER_ONE_POINTS, PLAYER_TWO_POINTS, SERVER_PATH, ORGANIZER_ID, ORGANIZER_NAME,
        ORGANIZER_LOGO, USER_ACCOUNT_ID, PARTICIPANTS_ID, PLAYER_ID, PLAYER_NAME, PLAYER_SURNAME, PLAYER_NICKNAME,
        PLAYER_PHOTO, TOTAL_WON, TOURNAMENT_NAME, TOURNAMENT_LOGO, PRIZE_POOL, ORGANIZER_REWARD_RATE, BUY_IN,
        PARTICIPANTS_NUMBER, TOURNAMENT_STATUS, USER_ID, ACCOUNT_AVATAR, USERNAME, USER_PASSWORD, PASSWORD_KEY,
        USER_EMAIL, USER_TYPE, LANG, WALLET_ID, CURRENCY, BALANCE;

        public String toString() {
            return this.name().toLowerCase();
        }

    }


    public static PlainGameDto mapGameDto(ResultSet rs) throws SQLException {
        long id = rs.getLong(GAME_ID.toString());
        int bracketIndex = rs.getInt(BRACKET_INDEX.toString());
        java.sql.Timestamp sqlStartTime = rs.getTimestamp(START_TIME.toString());
        java.util.Date startTime = DateConverter.toUtilDate(sqlStartTime);
        java.sql.Timestamp sqlEndTime = rs.getTimestamp(END_TIME.toString());
        java.util.Date endTime = DateConverter.toUtilDate(sqlEndTime);
        long firstPlayerId = rs.getLong(FIRST_PLAYER_ID.toString());
        long secondPlayerId = rs.getLong(SECOND_PLAYER_ID.toString());
        long tournamentId = rs.getLong(TOURNAMENT_ID.toString());
        long serverId = rs.getLong(GAME_SERVER_ID.toString());

        return new PlainGameDto.Builder()
                .id(id)
                .bracketIndex(bracketIndex)
                .startTime(startTime)
                .endTime(endTime)
                .firstPlayerId(firstPlayerId)
                .secondPlayerId(secondPlayerId)
                .tournamentId(tournamentId)
                .gameServerId(serverId)
                .build();
    }


    public static OrganizerDto mapOrganizerDto(ResultSet rs) throws SQLException {
        long id = rs.getLong(ORGANIZER_ID.toString());
        String name = rs.getString(ORGANIZER_NAME.toString());
        byte[] logo = rs.getBytes(ORGANIZER_LOGO.toString());
        long userId = rs.getLong(USER_ACCOUNT_ID.toString());

        return OrganizerDto.Builder.anOrganizerDto()
                .id(id)
                .name(name)
                .logo(logo)
                .userId(userId)
                .build();
    }


    public static GameServerDto mapGameServerDto(ResultSet rs) throws SQLException {
        long id = rs.getLong(GAME_SERVER_ID.toString());
        int pointToWin = rs.getInt(POINTS_TO_WIN.toString());
        int firstPlayerPoints = rs.getInt(PLAYER_ONE_POINTS.toString());
        int secondPlayerPoints = rs.getInt(PLAYER_TWO_POINTS.toString());
        String path = rs.getString(SERVER_PATH.toString());
        long gameId = rs.getLong(GAME_ID.toString());

        return new GameServerDto.Builder()
                .id(id)
                .pointsToWin(pointToWin)
                .playerOnePoints(firstPlayerPoints)
                .playerTwoPoints(secondPlayerPoints)
                .path(path)
                .gameId(gameId)
                .build();
    }


    public static PlayerDto mapPlayerDto(ResultSet rs) throws SQLException {
        long id = rs.getLong(PLAYER_ID.toString());
        String name = rs.getString(PLAYER_NAME.toString());
        String surname = rs.getString(PLAYER_SURNAME.toString());
        String nickname = rs.getString(PLAYER_NICKNAME.toString());
        byte[] photo = rs.getBytes(PLAYER_PHOTO.toString());
        double totalWon = rs.getDouble(TOTAL_WON.toString());
        long userId = rs.getLong(USER_ACCOUNT_ID.toString());

        return new PlayerDto.Builder()
                .id(id)
                .photo(photo)
                .name(name)
                .surname(surname)
                .nickname(nickname)
                .totalWon(totalWon)
                .userId(userId)
                .build();
    }


    public static UserDto mapUserDto(ResultSet rs) throws SQLException {
        long id = rs.getLong(USER_ID.toString());
        byte[] avatar = rs.getBytes(ACCOUNT_AVATAR.toString());
        String username = rs.getString(USERNAME.toString());
        String password = rs.getString(USER_PASSWORD.toString());
        String passwordKey = rs.getString(PASSWORD_KEY.toString());
        String email = rs.getString(USER_EMAIL.toString());
        String sType = rs.getString(USER_TYPE.toString());
        User.UserType type = User.UserType.fromString(sType).orElse(User.UserType.getDefault());
        String sLanguage = rs.getString(LANG.toString());
        User.Language language = User.Language.fromLocaleString(sLanguage).orElse(User.Language.getDefault());
        long playerId = rs.getLong(PLAYER_ID.toString());
        long organizerId = rs.getLong(ORGANIZER_ID.toString());


        return new UserDto.Builder()
                .id(id)
                .avatar(avatar)
                .username(username)
                .password(password)
                .passwordKey(passwordKey)
                .email(email)
                .type(type)
                .language(language)
                .organizerId(organizerId)
                .playerAccountId(playerId)
                .build();
    }


    public static TournamentDto mapTournamentDto(ResultSet rs) throws SQLException {
        long id = rs.getLong(TOURNAMENT_ID.toString());
        String name = rs.getString(TOURNAMENT_NAME.toString());
        byte[] logo = rs.getBytes(TOURNAMENT_LOGO.toString());
        double prizePool = rs.getDouble(PRIZE_POOL.toString());
        double reward = rs.getDouble(ORGANIZER_REWARD_RATE.toString());
        double buyIn = rs.getDouble(BUY_IN.toString());
        int participantsNumber = rs.getInt(PARTICIPANTS_NUMBER.toString());
        String sTournamentStatus = rs.getString(TOURNAMENT_STATUS.toString());
        Tournament.TournamentStatus tournamentStatus = Tournament.TournamentStatus
                .fromString(sTournamentStatus)
                .orElse(Tournament.TournamentStatus.getDefault());
        long organizerId = rs.getLong(ORGANIZER_ID.toString());

        return new TournamentDto.Builder()
                .id(id)
                .name(name)
                .logo(logo)
                .prizePool(prizePool)
                .reward(reward)
                .buyIn(buyIn)
                .playersNumber(participantsNumber)
                .status(tournamentStatus)
                .organizerId(organizerId)
                .build();
    }


    public static WalletDto mapWalletDto(ResultSet rs) throws SQLException {
        long id = rs.getLong(WALLET_ID.toString());
        double balance = rs.getDouble(BALANCE.toString());
        String sCurrency = rs.getString(CURRENCY.toString());
        Wallet.Currency currency = Wallet.Currency
                .fromString(sCurrency)
                .orElse(Wallet.Currency.getDefault());
        long userId = rs.getLong(USER_ID.toString());

        return WalletDto.Builder.aWalletDto()
                .id(id)
                .currency(currency)
                .balance(balance)
                .userId(userId)
                .build();
    }

}
