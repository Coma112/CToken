package net.coma112.ctoken.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import net.coma112.ctoken.enums.BadgeType;
import net.coma112.ctoken.enums.FormatType;
import net.coma112.ctoken.enums.keys.ConfigKeys;
import net.coma112.ctoken.events.BalanceChangeEvent;
import net.coma112.ctoken.manager.TokenTop;
import net.coma112.ctoken.utils.TokenLogger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public class MySQL extends AbstractDatabase {
    private final Connection connection;

    public MySQL(@NotNull ConfigurationSection section) throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();

        String host = section.getString("host");
        String database = section.getString("database");
        String user = section.getString("username");
        String pass = section.getString("password");
        int port = section.getInt("port");
        boolean ssl = section.getBoolean("ssl");
        boolean certificateVerification = section.getBoolean("certificateverification");
        int poolSize = section.getInt("poolsize");
        int maxLifetime = section.getInt("lifetime");

        hikariConfig.setPoolName("TemplatePool");
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMaxLifetime(maxLifetime * 1000L);
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(pass);

        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(ssl));
        if (!certificateVerification) hikariConfig.addDataSourceProperty("verifyServerCertificate", String.valueOf(false));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("jdbcCompliantTruncation", "false");
        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "275");
        hikariConfig.addDataSourceProperty("useUnicode", "true");

        try (HikariDataSource dataSource = new HikariDataSource(hikariConfig)) {
            connection = dataSource.getConnection();
        }
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException exception) {
                TokenLogger.error(exception.getMessage());
            }
        }
    }

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS token (PLAYER VARCHAR(255) NOT NULL, BALANCE INT, XP INT DEFAULT 0, PRIMARY KEY (PLAYER))";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
    }


    @Override
    public void createPlayer(@NotNull OfflinePlayer player) {
        String query = "INSERT INTO token (PLAYER, BALANCE, XP) VALUES (?, ?, ?)";

        try {
            if (!exists(player)) {
                try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                    preparedStatement.setString(1, player.getName());
                    preparedStatement.setInt(2, ConfigKeys.STARTING_BALANCE.getInt());
                    preparedStatement.setInt(3, calculateXPFromTokens(ConfigKeys.STARTING_BALANCE.getInt()));
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
    }

    @Override
    public boolean exists(@NotNull OfflinePlayer player) {
        String query = "SELECT * FROM token WHERE PLAYER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());

                return preparedStatement.executeQuery().next();
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }

        return false;
    }

    @Override
    public int getBalance(@NotNull OfflinePlayer player) {
        String query = "SELECT BALANCE FROM token WHERE PLAYER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("BALANCE");
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
        return 0;
    }

    @Override
    public int getXP(@NotNull OfflinePlayer player) {
        String query = "SELECT XP FROM token WHERE PLAYER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("XP");
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
        return 0;
    }

    @Override
    public List<TokenTop> getTop(int number) {
        List<TokenTop> topBalances = new ArrayList<>();
        String query = "SELECT PLAYER, BALANCE FROM token ORDER BY BALANCE DESC LIMIT ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, number);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    topBalances.add(new TokenTop(resultSet.getString("PLAYER"), resultSet.getInt("BALANCE")));
                }
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
        return topBalances;
    }

    @Override
    public String getTopPlayer(int top) {
        String playerName = null;
        String query = "SELECT PLAYER FROM token ORDER BY BALANCE DESC LIMIT ?, 1";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, top - 1);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) playerName = resultSet.getString("PLAYER");
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }

        return playerName;
    }

    @Override
    public int getTopBalance(int top) {
        String query = "SELECT BALANCE FROM token ORDER BY BALANCE DESC LIMIT ?, 1";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, top - 1);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("BALANCE");
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }

        return 0;
    }

    @Override
    public int getTopPlace(@NotNull OfflinePlayer player) {
        String query = "SELECT COUNT(*) FROM token WHERE BALANCE > (SELECT BALANCE FROM token WHERE PLAYER = ?)";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return resultSet.getInt(1) + 1;
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }

        return -1;
    }

    @Override
    public void setBalance(@NotNull OfflinePlayer player, int newBalance) {
        String query = "UPDATE token SET BALANCE = ?, XP = ? WHERE PLAYER = ?";
        int oldBalance = getBalance(player);

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, newBalance);
                preparedStatement.setInt(2, calculateXPFromTokens(newBalance));
                preparedStatement.setString(3, player.getName());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }

        Bukkit.getServer().getPluginManager().callEvent(new BalanceChangeEvent(player, oldBalance, newBalance - oldBalance));
    }

    @Override
    public void addToBalance(@NotNull OfflinePlayer player, int newBalance) {
        String query = "UPDATE token SET BALANCE = ?, XP = ? WHERE PLAYER = ?";
        int oldBalance = getBalance(player);

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                int updatedBalance = getBalance(player) + newBalance;

                preparedStatement.setInt(1, updatedBalance);
                preparedStatement.setInt(2, calculateXPFromTokens(updatedBalance));
                preparedStatement.setString(3, player.getName());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }

        Bukkit.getServer().getPluginManager().callEvent(new BalanceChangeEvent(player, oldBalance, oldBalance + newBalance));
    }

    @Override
    public void addToEveryoneBalance(int newBalance) {
        String query = "SELECT PLAYER, BALANCE FROM token";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String playerName = resultSet.getString("PLAYER");
                    int currentBalance = resultSet.getInt("BALANCE");
                    int updatedBalance = currentBalance + newBalance;

                    updateBalance(playerName, updatedBalance);
                    Bukkit.getServer().getPluginManager().callEvent(new BalanceChangeEvent(Bukkit.getOfflinePlayer(playerName), currentBalance, updatedBalance));
                }
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
    }

    @Override
    public void resetBalance(@NotNull OfflinePlayer player) {
        String query = "UPDATE token SET BALANCE = ? WHERE PLAYER = ?";
        int oldBalance = getBalance(player);

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setNull(1, Types.INTEGER);
                preparedStatement.setString(2, player.getName());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }

        Bukkit.getServer().getPluginManager().callEvent(new BalanceChangeEvent(player, oldBalance, 0));
    }

    @Override
    public void resetEveryone() {
        String query = "SELECT PLAYER, BALANCE FROM token";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String playerName = resultSet.getString("PLAYER");
                    int currentBalance = resultSet.getInt("BALANCE");
                    int newBalance = 0;

                    if (currentBalance != newBalance) {
                        updateBalance(playerName, newBalance);
                        Bukkit.getServer().getPluginManager().callEvent(new BalanceChangeEvent(Bukkit.getOfflinePlayer(playerName), currentBalance, newBalance));
                    }
                }
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
    }

    @Override
    public void takeFromBalance(@NotNull OfflinePlayer player, int newBalance) {
        String query = "UPDATE token SET BALANCE = ? WHERE PLAYER = ?";
        int oldBalance = getBalance(player);

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, oldBalance - newBalance);
                preparedStatement.setString(2, player.getName());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }

        Bukkit.getServer().getPluginManager().callEvent(new BalanceChangeEvent(player, oldBalance, oldBalance - newBalance));
    }

    @Override
    public int calculateXPFromTokens(int tokenBalance) {
        return (int) (tokenBalance * ConfigKeys.BADGES_MULTIPLIER.getDouble());
    }

    @Override
    public void handleBalanceChangeEvent(@NotNull final BalanceChangeEvent event) {
        calculateXPFromTokens(event.getOldBalance());
        calculateXPFromTokens(event.getNewBalance());
    }

    private void updateBalance(String playerName, int newBalance) {
        String updateQuery = "UPDATE token SET BALANCE = ?, XP = ? WHERE PLAYER = ?";

        try {
            try (PreparedStatement updateStatement = getConnection().prepareStatement(updateQuery)) {
                updateStatement.setInt(1, newBalance);
                updateStatement.setInt(2, calculateXPFromTokens(newBalance));
                updateStatement.setString(3, playerName);
                updateStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            TokenLogger.error(exception.getMessage());
        }
    }
}
