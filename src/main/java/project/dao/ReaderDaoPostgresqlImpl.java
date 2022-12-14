package project.dao;

import project.connector.ConnectionCreator;
import project.entity.Reader;
import project.exception.JdbcDaoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReaderDaoPostgresqlImpl implements ReaderDao {

    @Override
    public Reader save(Reader reader) {
        final String SQL_SAVE_READER = "INSERT INTO reader(name) VALUES(?)";

        try (var connection = ConnectionCreator.createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SAVE_READER, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, reader.getName());
            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                reader.setId(resultSet.getInt("id"));
                resultSet.close();
                return reader;
            } else {
                throw new JdbcDaoException("Failed to fetch generated ID from DB while saving new reader");
            }

        } catch (SQLException e) {
            throw new JdbcDaoException(e);
        }
    }

    @Override
    public Optional<Reader> findById(int readerId) {
        final String SQL_FIND_READER_BY_ID = "SELECT * FROM reader WHERE id = (?)";
        Reader reader;

        try (var connection = ConnectionCreator.createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_READER_BY_ID)) {

            preparedStatement.setInt(1, readerId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                reader = mapToReader(resultSet);
                return Optional.of(reader);
            }

            resultSet.close();

        } catch (SQLException e) {
            throw new JdbcDaoException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Reader> findAll() {
        List<Reader> readerList = new ArrayList<>();
        final String SQL_FIND_ALL_READERS = "SELECT * FROM reader";

        try (var connection = ConnectionCreator.createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ALL_READERS)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                readerList.add(mapToReader(resultSet));
            }

            resultSet.close();

        } catch (SQLException e) {
            throw new JdbcDaoException(e);
        }
        return readerList;
    }

    private Reader mapToReader(ResultSet resultSet) {
        Reader reader = new Reader();

        try {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");

            reader.setId(id);
            reader.setName(name);

        } catch (SQLException e) {
            throw new JdbcDaoException(e);
        }
        return reader;
    }
}
