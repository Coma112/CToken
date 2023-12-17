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

@CommandInfo(name = "tokenpay", requiresPlayer = true)
public class CommandTokenPay extends PluginCommand {

    public CommandTokenPay() {
        super("tokenpay");
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageKeys.PLAYER_REQUIRED);
            return true;
        }

        if (args.length <= 1) {
            player.sendMessage(MessageKeys.PAY_RIGHT_USAGE);
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        int value;

        try {
            value = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            player.sendMessage(MessageKeys.NOT_NUMBER);
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

        if (!CToken.getDatabaseManager().exists(target)) {
            sender.sendMessage(MessageKeys.OFFLINE_PLAYER);
            return true;
        }

        if (target == player) {
            player.sendMessage(MessageKeys.CANT_BE_YOU);
            return true;
        }

        int playerBalance = CToken.getDatabaseManager().getBalance(player);

        if (playerBalance < value) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_MONEY);
            return true;
        }

        CToken.getDatabaseManager().takeFromBalance(player, value);
        CToken.getDatabaseManager().addToBalance(target, value);

        player.sendMessage(MessageKeys.PAY_PLAYER
                .replace("%name%", Objects.requireNonNull(target.getName()))
                .replace("%value%", String.valueOf(value)));

        Player onlinePlayer = target.getPlayer();
        if (onlinePlayer != null) onlinePlayer.sendMessage(MessageKeys.PAY_TARGET
                .replace("%name%", player.getName())
                .replace("%value%", String.valueOf(value)));
        return true;
    }
}
