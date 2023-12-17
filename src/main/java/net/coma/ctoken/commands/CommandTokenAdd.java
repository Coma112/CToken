package net.coma.ctoken.commands;

import net.coma.ctoken.CToken;
import net.coma.ctoken.language.MessageKeys;
import net.coma.ctoken.subcommand.CommandInfo;
import net.coma.ctoken.subcommand.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@CommandInfo(name = "tokenadd", permission = "ctoken.add", requiresPlayer = false)
public class CommandTokenAdd extends PluginCommand {

    public CommandTokenAdd() {
        super("tokenadd");
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @NotNull String[] args) {
        OfflinePlayer target;

        if (args.length <= 1) {
            sender.sendMessage(MessageKeys.ADD_RIGHT_USAGE);
            return true;
        }

        target = Bukkit.getOfflinePlayer(args[0]);
        int value;

        try {
            value = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            sender.sendMessage(MessageKeys.NOT_NUMBER);
            return true;
        }

        if (value == 0) {
            sender.sendMessage(MessageKeys.CANT_BE_ZERO);
            return true;
        }

        if (value < 0) {
            sender.sendMessage(MessageKeys.CANT_BE_NEGATIVE);
            return true;
        }

        if (args[0].equals("all") || args[0].equals("*")) {
            CToken.getDatabaseManager().addToEveryoneBalance(value);
            sender.sendMessage(MessageKeys.ADD_EVERYONE.replace("%value%", String.valueOf(value)));
            return true;
        }

        if (!CToken.getDatabaseManager().exists(target)) {
            sender.sendMessage(MessageKeys.OFFLINE_PLAYER);
            return true;
        }

        CToken.getDatabaseManager().addToBalance(target, value);

        sender.sendMessage(MessageKeys.ADD_PLAYER
                .replace("%value%", String.valueOf(value))
                .replace("%name%", Objects.requireNonNull(target.getName())));
        Objects.requireNonNull(target.getPlayer()).sendMessage(MessageKeys.ADD_TARGET.replace("%value%", String.valueOf(value)));
        return true;
    }
}
