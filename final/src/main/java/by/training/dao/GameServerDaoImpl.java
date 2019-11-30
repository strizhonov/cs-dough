package by.training.dao;

import by.training.appentry.Bean;
import by.training.connection.ConnectionProvider;
import by.training.deleted.ResultSetManager;
import by.training.deleted.ResultSetManagerImpl;
import by.training.dto.GameServerDto;
import by.training.entity.GameServerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Bean
public class GameServerDaoImpl implements GameServerDao {

    private static final String INSERT =
            "INSERT INTO gameserver (points_to_win, player_one_points, player_two_points, server_path, game_id) " +
                    "VALUES (?,?,?,?,?)";
    private static final String SELECT =
            "SELECT game_server_id, points_to_win, player_one_points, player_two_points, server_path, game_id " +
                    "FROM gameserver " +
                    "WHERE game_id = ?";
    private static final String UPDATE =
            "UPDATE gameserver " +
                    "SET points_to_win=?, player_one_points=?, player_two_points=?, server_path=?, game_id=? " +
                    "WHERE game_server_id = ?";
    private static final String DELETE =
            "DELETE FROM gameserver " +
                    "WHERE game_server_id = ?";
    private static final String SELECT_ALL =
            "SELECT game_server_id, points_to_win, player_one_points, player_two_points, server_path, game_id " +
                    "FROM gameserver";

    private static final Logger LOGGER = LogManager.getLogger(GameServerDaoImpl.class);
    private ConnectionProvider provider;
    private DaoMapper mapper;

    public GameServerDaoImpl(ConnectionProvider provider) {
        this.provider = provider;
        this.mapper = new DaoMapper();
    }

    @Override
    public long save(GameServerDto gameServerDto) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            fillSaveStatement(statement, gameServerDto);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
                return 0;
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to perform entity saving.", e);
            throw new DaoException("Unable to perform entity saving.", e);
        }
    }

    @Override
    public GameServerDto get(long id) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                GameServerDto gameServerDto = null;
                if (resultSet.next()) {
                    gameServerDto = mapper.mapGameServerDto(resultSet);
                }
                if (gameServerDto == null) {
                    LOGGER.error("Unable to get game server with " + id + " id, not found.");
                    throw new DaoException("Unable to get game server with " + id + " id, not found.");
                }
                return gameServerDto;
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to perform entity retrieving.", e);
            throw new DaoException("Unable to perform entity retrieving.", e);
        }
    }

    @Override
    public boolean update(GameServerDto gameServerDto) throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {

            fillUpdateStatement(statement, gameServerDto);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
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
    public List<GameServerDto> findAll() throws DaoException {
        try (Connection connection = provider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {

            List<GameServerDto> result = new ArrayList<>();
            while (resultSet.next()) {
                GameServerDto gameServerDto = mapper.mapGameServerDto(resultSet);
                result.add(gameServerDto);
            }
            return result;

        } catch (SQLException e) {
            LOGGER.error("Unable to perform all entities retrieving.", e);
            throw new DaoException("Unable to perform all entities retrieving.", e);
        }
    }

    private void fillSaveStatement(PreparedStatement statement, GameServerDto gameServer) throws SQLException {
        int i = 0;
        statement.setInt(++i, gameServer.getPointsToWin());
        statement.setInt(++i, gameServer.getPlayerOnePoints());
        statement.setInt(++i, gameServer.getPlayerTwoPoints());
        statement.setString(++i, gameServer.getPath());
        statement.setLong(++i, gameServer.getGameId());
    }

    private void fillUpdateStatement(PreparedStatement statement, GameServerDto gameServer) throws SQLException {
        int i = 0;
        statement.setInt(++i, gameServer.getPointsToWin());
        statement.setInt(++i, gameServer.getPlayerOnePoints());
        statement.setInt(++i, gameServer.getPlayerTwoPoints());
        statement.setString(++i, gameServer.getPath());
        statement.setLong(++i, gameServer.getGameId());
        statement.setLong(++i, gameServer.getId());
    }


}