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


@CommandInfo(name = "tokenbalance", requiresPlayer = false)
public class CommandTokenBalance extends PluginCommand {

    public CommandTokenBalance() {
        super("tokenbalance");
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @NotNull String[] args) {
        OfflinePlayer target = null;

        if (args.length > 0) {
            target = Bukkit.getOfflinePlayer(args[0]);

        } else if (sender instanceof OfflinePlayer player) {
            sender.sendMessage(MessageKeys.SELFBALANCE.replace("%balance%", String.valueOf(CToken.getDatabaseManager().getBalance(player))));
            return true;
        }

        sender.sendMessage(MessageKeys.OTHER_BALANCE
                .replace("%player%", Objects.requireNonNull(Objects.requireNonNull(target).getName()))
                .replace("%balance%", String.valueOf(CToken.getDatabaseManager().getBalance(target))));
        return true;

    }
}
