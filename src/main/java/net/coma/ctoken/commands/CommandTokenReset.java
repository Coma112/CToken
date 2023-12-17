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

@CommandInfo(name = "tokenreset", permission = "ctoken.reset", requiresPlayer = false)
public class CommandTokenReset extends PluginCommand {

    public CommandTokenReset() {
        super("tokenreset");
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageKeys.RESET_RIGHT_USAGE);
            return true;
        }

        OfflinePlayer target;

        if (args[0].equals("all") || args[0].equals("*")) {
            CToken.getDatabaseManager().resetEveryone();
            sender.sendMessage(MessageKeys.EVERYONE_RESET);
            return true;
        } else {
            target = Bukkit.getOfflinePlayer(args[0]);

            if (!CToken.getDatabaseManager().exists(target)) {
                sender.sendMessage(MessageKeys.OFFLINE_PLAYER);
                return true;
            }
        }

        CToken.getDatabaseManager().resetBalance(target);
        sender.sendMessage(MessageKeys.RESET.replace("%name%", Objects.requireNonNull(target.getName())));
        return true;
    }
}
