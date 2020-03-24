package by.training.tournament;

import by.training.connection.ConnectionProvider;
import by.training.core.Bean;
import by.training.core.DaoException;
import by.training.util.DaoMapper;
import by.training.util.EntityDtoConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Bean
public class TournamentDaoImpl implements TournamentDao {

    private static final String INSERT =
            "INSERT INTO tournament (tournament_name, tournament_logo, prize_pool, organizer_reward_rate, buy_in, participants_number, tournament_status, organizer_id) " +
                    "VALUES (?,?,?,?,?,?,?,?)";

    private static final String SELECT =
            "SELECT tournament.tournament_id, tournament_name, tournament_logo, prize_pool, organizer_reward_rate, buy_in, participants_number, tournament_status, organizer_id " +
                    "FROM tournament " +
                    "WHERE tournament.tournament_id = ?";

    private static final String UPDATE =
            "UPDATE tournament " +
                    "SET tournament_name=?, tournament_logo=?, prize_pool=?, organizer_reward_rate=?, buy_in=?, participants_number=?, tournament_status=?, organizer_id=? " +
                    "WHERE tournament_id = ?";

    private static final String DELETE =
            "DELETE FROM tournament " +
                    "WHERE tournament_id = ?";

    private static final String SELECT_ALL =
            "SELECT tournament_id, tournament_name, tournament_logo, prize_pool, organizer_reward_rate, buy_in, participants_number, tournament_status, organizer_id " +
                    "FROM tournament";

    private static final String INSERT_PARTICIPANT =
            "INSERT INTO participant (player_id, tournament_id) " +
                    "VALUES (?,?) ";

    private static final String SELECT_PARTICIPANT =
            "SELECT participant_id " +
                    "FROM participant " +
                    "WHERE tournament_id = ? " +
                    "AND player_id = ?";

    private static final String PARTICIPANT_DELETE =
            "DELETE FROM participant " +
                    "WHERE tournament_id = ? " +
                    "AND player_id = ?";

    private static final String SELECT_BY_NAME =
            "SELECT tournament_id, tournament_name, tournament_logo, prize_pool, organizer_reward_rate, buy_in, participants_number, tournament_status, organizer_id " +
                    "FROM tournament " +
                    "WHERE tournament_name = ?";

    private static final String SELECT_ALL_OF_PLAYER =
            "SELECT tournament.tournament_id, tournament_name, tournament_logo, prize_pool, organizer_reward_rate, buy_in, participants_number, tournament_status, organizer_id " +
                    "FROM tournament " +
                    "INNER JOIN participant p " +
                    "ON tournament.tournament_id = p.tournament_id " +
                    "WHERE p.player_id = ?";

    private static final String SELECT_ALL_OF_ORGANIZER =
            "SELECT tournament_id, tournament_name, tournament_logo, prize_pool, organizer_reward_rate, buy_in, participants_number, tournament_status, organizer_id " +
                    "FROM tournament " +
                    "WHERE organizer_id = ?";

    private static final String SELECT_TOURNAMENT_PARTICIPANTS_IDS =
            "SELECT player_id " +
                    "FROM participant " +
                    "WHERE tournament_id=?";

    private static final String SELECT_TOURNAMENT_GAMES_IDS =
            "SELECT game_id " +
                    "FROM game " +
                    "WHERE tournament_id=?";


    private static final Logger LOGGER = LogManager.getLogger(TournamentDaoImpl.class);
    private ConnectionProvider provider;


    public TournamentDaoImpl(ConnectionProvider provider) {
        this.provider = provider;
    }


    @Override
    public long save(TournamentDto tournamentDto) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            Tournament tournament = EntityDtoConverter.fromDto(tournamentDto);
            fillSaveStatement(statement, tournament);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }

                return 0;
            }


        } catch (SQLException | IOException e) {
            LOGGER.error("Unable to perform entity saving.", e);
            throw new DaoException("Unable to perform entity saving.", e);
        }

    }


    @Override
    public TournamentDto get(long id) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return compile(resultSet);
                } else {
                    LOGGER.error("Unable to get tournament with " + id + " id, not found.");
                    throw new DaoException("Unable to get tournament with " + id + " id, not found.");
                }

            }

        } catch (SQLException e) {
            LOGGER.error("Unable to perform entity retrieving.", e);
            throw new DaoException("Unable to perform entity retrieving.", e);
        }
    }


    @Override
    public boolean update(TournamentDto tournamentDto) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {

            Tournament tournament = EntityDtoConverter.fromDto(tournamentDto);
            fillUpdateStatement(statement, tournament);
            return statement.executeUpdate() > 0;

        } catch (SQLException | IOException e) {
            LOGGER.error("Unable to perform entity updating.", e);
            throw new DaoException("Unable to perform entity updating.", e);
        }
    }


    @Override
    public boolean delete(long id) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {

            statement.setLong(1, id);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Unable to perform entity deleting.", e);
            throw new DaoException("Unable to perform entity deleting.", e);
        }
    }


    @Override
    public List<TournamentDto> findAll() throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {

            List<TournamentDto> result = new ArrayList<>();

            while (resultSet.next()) {
                TournamentDto tournamentDto = compile(resultSet);
                result.add(tournamentDto);
            }

            return result;

        } catch (SQLException e) {
            LOGGER.error("Unable to perform all entities retrieving.", e);
            throw new DaoException("Unable to perform all entities retrieving.", e);
        }
    }

    @Override
    public boolean isParticipantPresent(ParticipantDto dto) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_PARTICIPANT)) {

            int i = 0;
            statement.setLong(++i, dto.getTournamentId());
            statement.setLong(++i, dto.getPlayerId());

            try (ResultSet resultSet = statement.executeQuery()) {

                return resultSet.next();

            }

        } catch (SQLException e) {
            LOGGER.error("Unable to perform entity retrieving.", e);
            throw new DaoException("Unable to perform entity retrieving.", e);
        }
    }

    @Override
    public TournamentDto findByName(String name) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_NAME)) {

            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? compile(resultSet) : null;
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to perform entity search.", e);
            throw new DaoException("Unable to perform entity search.", e);
        }
    }


    @Override
    public void addParticipant(ParticipantDto dto) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_PARTICIPANT)) {

            int i = 0;
            statement.setLong(++i, dto.getPlayerId());
            statement.setLong(++i, dto.getTournamentId());
            statement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error("Unable to perform entity saving.", e);
            throw new DaoException("Unable to perform entity saving.", e);
        }
    }


    @Override
    public void removeParticipant(ParticipantDto dto) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(PARTICIPANT_DELETE)) {

            int i = 0;
            statement.setLong(++i, dto.getTournamentId());
            statement.setLong(++i, dto.getPlayerId());
            statement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error("Unable to perform entity saving.", e);
            throw new DaoException("Unable to perform entity saving.", e);
        }
    }


    @Override
    public List<TournamentDto> findAllOfPlayer(long playerId) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_OF_PLAYER)) {

            int i = 0;
            statement.setLong(++i, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {

                List<TournamentDto> result = new ArrayList<>();

                while (resultSet.next()) {
                    TournamentDto tournamentDto = compile(resultSet);
                    result.add(tournamentDto);
                }

                return result;
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to perform all entities retrieving.", e);
            throw new DaoException("Unable to perform all entities retrieving.", e);
        }
    }


    @Override
    public List<TournamentDto> findAllOfOrganizer(long organizerId) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_OF_ORGANIZER)) {

            int i = 0;
            statement.setLong(++i, organizerId);

            try (ResultSet resultSet = statement.executeQuery()) {

                List<TournamentDto> result = new ArrayList<>();

                while (resultSet.next()) {
                    TournamentDto tournamentDto = compile(resultSet);
                    result.add(tournamentDto);
                }

                return result;
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to perform all entities retrieving.", e);
            throw new DaoException("Unable to perform all entities retrieving.", e);
        }
    }


    private List<Long> findAllPlayersIdsByTournamentId(long tournamentId) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_TOURNAMENT_PARTICIPANTS_IDS)) {

            statement.setLong(1, tournamentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                List<Long> result = new ArrayList<>();

                while (resultSet.next()) {
                    long playerId = resultSet.getLong(DaoMapper.Column.PLAYER_ID.toString());
                    result.add(playerId);
                }

                return result;
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to perform entities retrieving.", e);
            throw new DaoException("Unable to perform entities retrieving.", e);
        }
    }


    private List<Long> findAllGamesIdsByTournamentId(long tournamentId) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_TOURNAMENT_GAMES_IDS)) {

            statement.setLong(1, tournamentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                List<Long> result = new ArrayList<>();

                while (resultSet.next()) {
                    long gameId = resultSet.getLong(DaoMapper.Column.GAME_ID.toString());
                    result.add(gameId);
                }

                return result;
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to perform entities retrieving.", e);
            throw new DaoException("Unable to perform entities retrieving.", e);
        }
    }


    private void fillSaveStatement(PreparedStatement statement, Tournament tournament) throws SQLException, IOException {
        int i = 0;
        statement.setString(++i, tournament.getName());
        try (InputStream stream = new ByteArrayInputStream(tournament.getLogo())) {
            statement.setBlob(++i, stream);
            statement.setDouble(++i, tournament.getPrizePool());
            statement.setDouble(++i, tournament.getOrganizerRewardPercentage());
            statement.setDouble(++i, tournament.getBuyIn());
            statement.setInt(++i, tournament.getParticipantsNumber());
            statement.setString(++i, tournament.getStatus().name());
            statement.setLong(++i, tournament.getOrganizerId());
        }
    }


    private void fillUpdateStatement(PreparedStatement statement, Tournament tournament) throws SQLException, IOException {
        int i = 0;
        statement.setString(++i, tournament.getName());
        try (InputStream stream = new ByteArrayInputStream(tournament.getLogo())) {
            statement.setBlob(++i, stream);
            statement.setDouble(++i, tournament.getPrizePool());
            statement.setDouble(++i, tournament.getOrganizerRewardPercentage());
            statement.setDouble(++i, tournament.getBuyIn());
            statement.setInt(++i, tournament.getParticipantsNumber());
            statement.setString(++i, tournament.getStatus().name());
            statement.setLong(++i, tournament.getOrganizerId());
            statement.setLong(++i, tournament.getId());
        }
    }


    private TournamentDto compile(ResultSet resultSet) throws SQLException, DaoException {
        TournamentDto tournament = DaoMapper.mapTournamentDto(resultSet);
        List<Long> gamesIds = findAllGamesIdsByTournamentId(tournament.getId());
        List<Long> participantsIds = findAllPlayersIdsByTournamentId(tournament.getId());
        tournament.setGamesIds(gamesIds);
        tournament.setParticipantsIds(participantsIds);
        return tournament;
    }


}
