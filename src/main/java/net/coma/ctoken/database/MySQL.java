package net.coma.ctoken.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.coma.ctoken.CToken;
import net.coma.ctoken.enums.BadgeType;
import net.coma.ctoken.event.BalanceChangeEvent;
import net.coma.ctoken.managers.TopManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MySQL extends DatabaseManager {

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

        hikariConfig.setPoolName("TokenPool");
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMaxLifetime(maxLifetime * 1000L);
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(pass);
        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(ssl));
        if (!certificateVerification)
            hikariConfig.addDataSourceProperty("verifyServerCertificate", String.valueOf(false));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("jdbcCompliantTruncation", "false");
        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "275");
        hikariConfig.addDataSourceProperty("useUnicode", "true");
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        connection = dataSource.getConnection();
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
                exception.printStackTrace();
            }
        }
    }

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS token (PLAYER VARCHAR(255) NOT NULL, BALANCE INT, XP INT DEFAULT 0, PRIMARY KEY (PLAYER))";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }


    @Override
    public void createPlayer(@NotNull OfflinePlayer player) {
        String query = "INSERT IGNORE INTO token (PLAYER, XP) VALUES (?, ?)";

        try {
            if (!exists(player)) {
                try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                    preparedStatement.setString(1, player.getName());
                    int initialTokenBalance = 0;
                    int initialXP = calculateXPFromTokens(initialTokenBalance);
                    preparedStatement.setInt(2, initialXP);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public boolean exists(@NotNull OfflinePlayer player) {
        String query = "SELECT * FROM token WHERE PLAYER = ?";

        try {
            if (!getConnection().isValid(2))
                reconnect(Objects.requireNonNull(CToken.getInstance().getCoinYML().getSection("database")));

            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());

                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public int getBalance(@NotNull OfflinePlayer player) {
        String query = "SELECT BALANCE FROM token WHERE PLAYER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());

                ResultSet resultSet = preparedStatement.executeQuery();
                int balance;

                if (resultSet.next()) {
                    balance = resultSet.getInt("BALANCE");
                    return balance;
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
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
                int xp;

                if (resultSet.next()) {
                    xp = resultSet.getInt("XP");
                    return xp;
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    @Override
    public String getFormattedBalance(@NotNull OfflinePlayer player) {
        int balance = getBalance(player);
        DecimalFormat formatter = new DecimalFormat("#.#");

        if (balance < 1000) {
            return String.valueOf(balance);
        } else if (balance < 1000000) {
            double formattedBalance = balance / 1000.0;
            return formatter.format(formattedBalance) + "K";
        } else {
            double formattedBalance = balance / 1000000.0;
            return formatter.format(formattedBalance) + "M";
        }
    }

    @Override
    public List<TopManager> getTop(int number) {
        List<TopManager> topBalances = new ArrayList<>();
        String query = "SELECT PLAYER, BALANCE FROM token ORDER BY BALANCE DESC LIMIT ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, number);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String playerName = resultSet.getString("PLAYER");
                    int balance = resultSet.getInt("BALANCE");
                    topBalances.add(new TopManager(playerName, balance));
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return topBalances;
    }

    @SuppressWarnings("all")
    public void resetConnection() {
        try {
            getConnection().endRequest();
            System.out.println("MySQL connection has been reseted successfully @ " + getConnection().getClientInfo());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
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
            exception.printStackTrace();
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
            exception.printStackTrace();
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
            exception.printStackTrace();
        }

        return -1;
    }

    @Override
    public String getFormattedCommasBalance(@NotNull OfflinePlayer player) {
        int balance = getBalance(player);
        return String.format("%,d", balance);
    }

    @Override
    public String getFormattedDotBalance(@NotNull OfflinePlayer player) {
        int balance = getBalance(player);
        return String.format("%,d", balance).replace(",", ".");
    }

    @Override
    public void setBalance(@NotNull OfflinePlayer player, int newBalance) {
        String query = "UPDATE token SET BALANCE = ?, XP = ? WHERE PLAYER = ?";
        int oldBalance = getBalance(player);

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, newBalance);

                int updatedXP = calculateXPFromTokens(newBalance);
                preparedStatement.setInt(2, updatedXP);

                preparedStatement.setString(3, player.getName());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        Bukkit.getServer().getPluginManager().callEvent(new BalanceChangeEvent(player, oldBalance, newBalance - oldBalance));
    }

    @Override
    public void addToBalance(@NotNull OfflinePlayer player, int newBalance) {
        String query = "UPDATE token SET BALANCE = ?, XP = ? WHERE PLAYER = ?";
        int oldBalance = getBalance(player);

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                int currentBalance = getBalance(player);
                int updatedBalance = currentBalance + newBalance;
                preparedStatement.setInt(1, updatedBalance);

                int updatedXP = calculateXPFromTokens(updatedBalance);
                preparedStatement.setInt(2, updatedXP);

                preparedStatement.setString(3, player.getName());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
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
            exception.printStackTrace();
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
            exception.printStackTrace();
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
            exception.printStackTrace();
        }
    }

    private void updateBalance(String playerName, int newBalance) {
        String updateQuery = "UPDATE token SET BALANCE = ?, XP = ? WHERE PLAYER = ?";

        try {
            try (PreparedStatement updateStatement = getConnection().prepareStatement(updateQuery)) {
                updateStatement.setInt(1, newBalance);

                int updatedXP = calculateXPFromTokens(newBalance);
                updateStatement.setInt(2, updatedXP);

                updateStatement.setString(3, playerName);
                updateStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
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
            exception.printStackTrace();
        }

        Bukkit.getServer().getPluginManager().callEvent(new BalanceChangeEvent(player, oldBalance, oldBalance - newBalance));
    }

    @Override
    public int calculateXPFromTokens(int tokenBalance) {
        return (int) (tokenBalance * 0.5);
    }

    @Override
    public void handleBalanceChangeEvent(BalanceChangeEvent event) {
        int oldBalance = event.getOldBalance();
        int newBalance = event.getNewBalance();

        int oldXP = calculateXPFromTokens(oldBalance);
        int newXP = calculateXPFromTokens(newBalance);

        BadgeType oldBadge = BadgeType.convertXPToBadge(oldXP);
        BadgeType newBadge = BadgeType.convertXPToBadge(newXP);
    }

    @Override
    public void reconnect(@NotNull ConfigurationSection section) {
        try {
            if (getConnection() != null && !getConnection().isClosed()) getConnection().close();
            new MySQL(Objects.requireNonNull(CToken.getInstance().getCoinYML().getSection("database.mysql")));
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to reconnect to the database", exception);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
