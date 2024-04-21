package net.coma.ctoken;

import lombok.Getter;
import net.coma.ctoken.config.CoinYML;
import net.coma.ctoken.database.DatabaseManager;
import net.coma.ctoken.database.MySQL;
import net.coma.ctoken.hooks.Placeholders;
import net.coma.ctoken.language.Language;
import net.coma.ctoken.utils.CommandRegister;
import net.coma.ctoken.utils.ListenerRegister;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

public final class CToken extends JavaPlugin {

    @Getter
    private static CToken instance;
    private static DatabaseManager databaseManager;
    private static Language language;
    private static CoinYML coinYML;

    @Override
    public void onEnable() {
        instance = this;

        initializeComponents();
        initializeDatabaseManager();
        registerEventsAndCommands();

        MySQL mysql = (MySQL) databaseManager;
        mysql.createTable();
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.disconnect();
    }

    public Language getLanguage() {
        return language;
    }

    public CoinYML getCoinYML() {
        return coinYML;
    }

    public DatabaseManager getDatabaseManager() { return databaseManager; }

    private void initializeComponents() {
        coinYML = new CoinYML();
        language = new Language();
    }

    private void registerEventsAndCommands() {
        ListenerRegister.registerEvents();
        CommandRegister.registerCommands();
        new Placeholders().register();
    }

    private void initializeDatabaseManager() {
        try {
            databaseManager = new MySQL(Objects.requireNonNull(getCoinYML().getSection("database.mysql")));
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
