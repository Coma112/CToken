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

@CommandInfo(name = "tokentake", permission = "ctoken.take", requiresPlayer = false)
public class CommandTokenTake extends PluginCommand {

    public CommandTokenTake() {
        super("tokentake");
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length <= 1) {
            sender.sendMessage(MessageKeys.TAKE_RIGHT_USAGE);
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!CToken.getInstance().getDatabaseManager().exists(target)) {
            sender.sendMessage(MessageKeys.OFFLINE_PLAYER);
            return true;
        }

        int targetBalance = CToken.getInstance().getDatabaseManager().getBalance(target);
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

        if (targetBalance < value) {
            sender.sendMessage(MessageKeys.NOT_ENOUGH_MONEY_TARGET);
            return true;
        }

        CToken.getInstance().getDatabaseManager().takeFromBalance(target, value);

        sender.sendMessage(MessageKeys.TAKE_PLAYER
                .replace("%value%", String.valueOf(value))
                .replace("%name%", Objects.requireNonNull(target.getName())));

        Player onlinePlayer = target.getPlayer();
        if (onlinePlayer != null) {
            onlinePlayer.sendMessage(MessageKeys.TAKE_TARGET
                    .replace("%balance%", String.valueOf(CToken.getInstance().getDatabaseManager().getBalance(target)))
                    .replace("%value%", String.valueOf(value)));
        }

        return true;
    }
}
