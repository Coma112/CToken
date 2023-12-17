package net.coma.ctoken.commands;

import net.coma.ctoken.CToken;
import net.coma.ctoken.language.MessageKeys;
import net.coma.ctoken.subcommand.CommandInfo;
import net.coma.ctoken.subcommand.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@CommandInfo(name = "tokenset", permission = "ctoken.set", requiresPlayer = false)
public class CommandTokenSet extends PluginCommand {

    public CommandTokenSet() {
        super("tokenset");
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length <= 1) {
            sender.sendMessage(MessageKeys.SET_RIGHT_USAGE);
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!CToken.getDatabaseManager().exists(target)) {
            sender.sendMessage(MessageKeys.OFFLINE_PLAYER);
            return true;
        }

        int value;

        try {
            value = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            sender.sendMessage(MessageKeys.NOT_NUMBER);
            return true;
        }

        if (value < 0) {
            sender.sendMessage(MessageKeys.CANT_BE_NEGATIVE);
            return true;
        }

        CToken.getDatabaseManager().setBalance(target, value);
        sender.sendMessage(MessageKeys.SET_BALANCE_PLAYER
                .replace("%name%", Objects.requireNonNull(target.getName()))
                .replace("%balance%", String.valueOf(CToken.getDatabaseManager().getBalance(target))));

        Player onlinePlayer = target.getPlayer();
        if (onlinePlayer != null) {
            onlinePlayer.sendMessage(MessageKeys.SET_BALANCE_TARGET
                    .replace("%balance%", String.valueOf(CToken.getDatabaseManager().getBalance(target))));
        }
        return true;
    }
}
