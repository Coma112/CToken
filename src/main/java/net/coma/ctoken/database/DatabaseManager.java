package net.coma.ctoken.database;

import net.coma.ctoken.event.BalanceChangeEvent;
import net.coma.ctoken.managers.TopManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class DatabaseManager {
    public abstract boolean exists(@NotNull OfflinePlayer player);

    public abstract int getBalance(@NotNull OfflinePlayer player);

    public abstract int getXP(@NotNull OfflinePlayer player);

    public abstract String getFormattedBalance(@NotNull OfflinePlayer player);

    public abstract String getTopPlayer(int top);

    public abstract int getTopBalance(int top);

    public abstract int getTopPlace(@NotNull OfflinePlayer player);

    public abstract String getFormattedCommasBalance(@NotNull OfflinePlayer player);

    public abstract String getFormattedDotBalance(@NotNull OfflinePlayer player);

    public abstract List<TopManager> getTop(int value);

    public abstract void setBalance(@NotNull OfflinePlayer player, int newBalance);

    public abstract void addToBalance(@NotNull OfflinePlayer player, int newBalance);

    public abstract void addToEveryoneBalance(int newBalance);

    public abstract void resetBalance(@NotNull OfflinePlayer player);

    public abstract void resetEveryone();

    public abstract void takeFromBalance(@NotNull OfflinePlayer player, int newBalance);

    public abstract void createPlayer(@NotNull OfflinePlayer player);

    public abstract boolean isConnected();

    public abstract void disconnect();

    public abstract int calculateXPFromTokens(int tokenBalance);

    public abstract void handleBalanceChangeEvent(BalanceChangeEvent event);

    public abstract void reconnect(@NotNull ConfigurationSection section);
}
