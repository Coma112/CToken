package net.coma.ctoken.language;

import net.coma.ctoken.CToken;
import net.coma.ctoken.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Language extends ConfigUtils {
    public Language() {
        super(CToken.getInstance().getDataFolder().getPath() + File.separator + "locales", "messages_en");

        YamlConfiguration yml = getYml();

        yml.addDefault("prefix", "&e&lTOKEN &8| ");
        yml.addDefault("messages.no-permission", "&cYou do not have permission to do this!");
        yml.addDefault("messages.reload", "&aI have successfully reloaded the files!");
        yml.addDefault("messages.player-required", "&cPlayer is required!");
        yml.addDefault("messages.set-right-usage", "&cUsage: /tokenset [player] [amount]");
        yml.addDefault("messages.reset-right-usage", "&cUsage: /tokenreset [player or all]");
        yml.addDefault("messages.pay-right-usage", "&cUsage: /tokenpay [player] [amount]");
        yml.addDefault("messages.take-right-usage", "&cUsage: /tokentake [player] [amount]");
        yml.addDefault("messages.add-right-usage", "&cUsage: /tokenadd [player or all] [amount]");
        yml.addDefault("messages.not-a-number", "&cPlease enter a CORRECT number!");
        yml.addDefault("messages.add-player", "&aYou have successfully given %name% %value%!");
        yml.addDefault("messages.add-everyone", "&aYou gave %value% to everyone!");
        yml.addDefault("messages.negative", "&cCan't be negative!");
        yml.addDefault("messages.add-target", "&aYou got %value%!");
        yml.addDefault("messages.not-a-number", "&cPlease enter a CORRECT number!");
        yml.addDefault("messages.cant-be-zero", "&cThe number can not be zero!");
        yml.addDefault("messages.cant-be-you", "&cIt can't be you!");
        yml.addDefault("messages.everyone-reset", "&aEveryone's balance has been reset!");
        yml.addDefault("messages.cant-be-bigger", "&cCan not be bigger than 15!");
        yml.addDefault("messages.reset", "&aThe balance of &2%name% &ahas been reset!");
        yml.addDefault("messages.take-player", "&aYou have successfully taken &2%value% &afrom &2%name%&a!");
        yml.addDefault("messages.pay-player", "&aYou have successfully transferred &2%value% &ato &2%name%&a!");
        yml.addDefault("messages.pay-target", "&2%name% &areferred you &2%value%&a!");
        yml.addDefault("messages.not-enough-money", "&cYou do not have enough money!");
        yml.addDefault("messages.not-enough-money-target", "&c%name% does not have enough balance!");
        yml.addDefault("messages.take-target", "&2%value% &ahas been taken from you! Your new balance: &2%balance%&a!");
        yml.addDefault("messages.otherbalance", "&2%player% &abalance: &2%balance%&a!");
        yml.addDefault("messages.selfbalance", "&aYour balance is: &2%balance%&a!");
        yml.addDefault("messages.set-balance-player", "&2%name%&a's new balance is &2%balance%&a!");
        yml.addDefault("messages.offline-player", "&cCan't find the player in the database!");
        yml.addDefault("messages.set-balance-target", "&aYour balance has been updated! Your new balance: &2%balance%&a!");
        yml.addDefault("badges.novice", "Novice");
        yml.addDefault("badges.beginner", "Beginner");
        yml.addDefault("badges.competence", "Competence");
        yml.addDefault("badges.proficient", "Proficient");
        yml.addDefault("badges.expert", "Expert");

        yml.options().copyDefaults(true);
        save();
    }
}