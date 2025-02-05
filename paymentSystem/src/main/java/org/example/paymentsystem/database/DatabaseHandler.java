package org.example.paymentsystem.database;

import org.example.customresponse.specific.AccountDetails;

import java.sql.*;

public class DatabaseHandler {
    private final String url, name, password;

    private static final String UNIQUE_CONSTRAINT_VIOLATION = "23505";
    private static final String CHECK_CONSTRAINT_VIOLATION = "23514";

    public DatabaseHandler(String url, String name, String password) {
        this.url = url;
        this.name = name;
        this.password = password;
    }

    public DatabaseReply<?> createNewAccount(String login, byte[] password_hash) {
        final String SQL_NEW_ACCOUNT = "INSERT INTO account (login, password_hash) VALUES (?, ?);";
        try (Connection connection = DriverManager.getConnection(url, name, password);
             PreparedStatement stmt = connection.prepareStatement(SQL_NEW_ACCOUNT)) {
            stmt.setString(1, login);
            stmt.setBytes(2, password_hash);
            stmt.executeUpdate();
            return new DatabaseReply<>(DatabaseReply.Status.SUCCESS);
        } catch (SQLException e) {
            return e.getSQLState().equals(UNIQUE_CONSTRAINT_VIOLATION)
                    ? new DatabaseReply<>(DatabaseReply.Status.FAILURE)
                    : new DatabaseReply<>(DatabaseReply.Status.NO_CONNECTION);
        }
    }

    public DatabaseReply<?> auth(String login, byte[] password_hash) {
        final String SQL_AUTH = "SELECT balance FROM account WHERE login = ? AND password_hash = ?";
        try (Connection connection = DriverManager.getConnection(url, name, password);
             PreparedStatement stmt = connection.prepareStatement(SQL_AUTH)) {
            stmt.setString(1, login);
            stmt.setBytes(2, password_hash);
            ResultSet rs = stmt.executeQuery();
            boolean thereIsARow = rs.next();
            return thereIsARow
                    ? new DatabaseReply<>(
                            DatabaseReply.Status.SUCCESS,
                            new AccountDetails(rs.getDouble("balance")))
                    : new DatabaseReply<>(DatabaseReply.Status.FAILURE);
        } catch (SQLException e) {
            return new DatabaseReply<>(DatabaseReply.Status.NO_CONNECTION);
        }
    }

    public DatabaseReply<?> findAccount(String login) {
        final String SQL_FIND = "SELECT * FROM account WHERE login = ?;";
        try (Connection connection = DriverManager.getConnection(url, name, password);
             PreparedStatement stmt = connection.prepareStatement(SQL_FIND)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            boolean thereIsARow = rs.next();
            return thereIsARow
                    ? new DatabaseReply<>(DatabaseReply.Status.SUCCESS)
                    : new DatabaseReply<>(DatabaseReply.Status.FAILURE);
        } catch (SQLException e) {
            return new DatabaseReply<>(DatabaseReply.Status.NO_CONNECTION);
        }
    }

    public DatabaseReply<?> deposit(String receiver, double sum) {
        final String SQL_DEPOSIT = "UPDATE account SET balance = balance + ? WHERE login = ?;";
        try (Connection connection = DriverManager.getConnection(url, name, password);
             PreparedStatement stmt = connection.prepareStatement(SQL_DEPOSIT)) {
            stmt.setDouble(1, sum);
            stmt.setString(2, receiver);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1
                    ? new DatabaseReply<>(DatabaseReply.Status.SUCCESS)
                    : new DatabaseReply<>(DatabaseReply.Status.CONCURRENT_MODIFICATION);
        } catch (SQLException e) {
            return new DatabaseReply<>(DatabaseReply.Status.NO_CONNECTION);
        }
    }

    public DatabaseReply<?> changeLogin(String login, String newLogin) {
        final String SQL_CHANGE_LOGIN = "UPDATE account SET login = ? WHERE login = ?;";
        try (Connection connection = DriverManager.getConnection(url, name, password);
             PreparedStatement stmt = connection.prepareStatement(SQL_CHANGE_LOGIN)) {
            stmt.setString(1, newLogin);
            stmt.setString(2, login);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1
                    ? new DatabaseReply<>(DatabaseReply.Status.SUCCESS)
                    : new DatabaseReply<>(DatabaseReply.Status.CONCURRENT_MODIFICATION);
        } catch (SQLException e) {
            return e.getSQLState().equals(UNIQUE_CONSTRAINT_VIOLATION)
                    ? new DatabaseReply<>(DatabaseReply.Status.FAILURE)
                    : new DatabaseReply<>(DatabaseReply.Status.NO_CONNECTION);
        }
    }

    public DatabaseReply<?> changePassword(String login, byte[] newPasswordHash) {
        final String SQL_CHANGE_PASSWORD = "UPDATE account SET password_hash = ? WHERE login = ?;";
        try (Connection connection = DriverManager.getConnection(url, name, password);
             PreparedStatement stmt = connection.prepareStatement(SQL_CHANGE_PASSWORD)) {
            stmt.setBytes(1, newPasswordHash);
            stmt.setString(2, login);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1
                    ? new DatabaseReply<>(DatabaseReply.Status.SUCCESS)
                    : new DatabaseReply<>(DatabaseReply.Status.CONCURRENT_MODIFICATION);
        } catch (SQLException e) {
            return new DatabaseReply<>(DatabaseReply.Status.NO_CONNECTION);
        }
    }

    public DatabaseReply<?> deleteAccount(String login) {
        final String SQL_DELETE_ACCOUNT = "DELETE FROM account WHERE login = ?;";
        try (Connection connection = DriverManager.getConnection(url, name, password);
             PreparedStatement stmt = connection.prepareStatement(SQL_DELETE_ACCOUNT)) {
            stmt.setString(1, login);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1
                    ? new DatabaseReply<>(DatabaseReply.Status.SUCCESS)
                    : new DatabaseReply<>(DatabaseReply.Status.CONCURRENT_MODIFICATION);
        } catch (SQLException e) {
            return new DatabaseReply<>(DatabaseReply.Status.NO_CONNECTION);
        }
    }

    public DatabaseReply<?> withdraw(String login, double sum) {
        final String SQL_WITHDRAW = "UPDATE account SET balance = balance - ? WHERE login = ?;";
        try (Connection connection = DriverManager.getConnection(url, name, password);
             PreparedStatement stmt = connection.prepareStatement(SQL_WITHDRAW)) {
            stmt.setDouble(1, sum);
            stmt.setString(2, login);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1
                    ? new DatabaseReply<>(DatabaseReply.Status.SUCCESS)
                    : new DatabaseReply<>(DatabaseReply.Status.CONCURRENT_MODIFICATION);
        } catch (SQLException e) {
            return e.getSQLState().equals(CHECK_CONSTRAINT_VIOLATION)
                    ? new DatabaseReply<>(DatabaseReply.Status.CONCURRENT_MODIFICATION)
                    : new DatabaseReply<>(DatabaseReply.Status.NO_CONNECTION);
        }
    }

    public DatabaseReply<?> transfer(String sourceLogin, String destinationLogin, double sum) {
        final String SQL_TRANSFER_TAKE = "UPDATE account SET balance = balance - ? WHERE login = ?;";
        final String SQL_TRANSFER_GIVE = "UPDATE account SET balance = balance + ? WHERE login = ?;";
        try (Connection connection = DriverManager.getConnection(url, name, password)) {
            try (PreparedStatement stmtTake = connection.prepareStatement(SQL_TRANSFER_TAKE);
                 PreparedStatement stmtGive = connection.prepareStatement(SQL_TRANSFER_GIVE)) {
                connection.setAutoCommit(false);
                stmtTake.setDouble(1, sum);
                stmtTake.setString(2, sourceLogin);
                int rowsAffected = stmtTake.executeUpdate();
                if (rowsAffected != 1) {
                    connection.rollback();
                    return new DatabaseReply<>(DatabaseReply.Status.CONCURRENT_MODIFICATION);
                }
                stmtGive.setDouble(1, sum);
                stmtGive.setString(2, destinationLogin);
                int rowsAffected2 = stmtGive.executeUpdate();
                if (rowsAffected2 != 1) {
                    connection.rollback();
                    return new DatabaseReply<>(DatabaseReply.Status.CONCURRENT_MODIFICATION);
                }
                connection.commit();
                return new DatabaseReply<>(DatabaseReply.Status.SUCCESS);
            } catch (SQLException e) {
                if (!connection.isClosed()) connection.rollback();
                return e.getSQLState().equals(CHECK_CONSTRAINT_VIOLATION)
                        ? new DatabaseReply<>(DatabaseReply.Status.CONCURRENT_MODIFICATION)
                        : new DatabaseReply<>(DatabaseReply.Status.NO_CONNECTION);
            }
        } catch (SQLException e) {
            return new DatabaseReply<>(DatabaseReply.Status.NO_CONNECTION);
        }
    }
}
