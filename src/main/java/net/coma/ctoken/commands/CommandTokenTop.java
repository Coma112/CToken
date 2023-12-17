package net.coma.ctoken.commands;

import net.coma.ctoken.language.MessageKeys;
import net.coma.ctoken.managers.TopManager;
import net.coma.ctoken.subcommand.CommandInfo;
import net.coma.ctoken.subcommand.PluginCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandInfo(name = "tokentop", requiresPlayer = false)
public class CommandTokenTop extends PluginCommand {

    public CommandTokenTop() {
        super("tokentop");
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean run(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            sender.spigot().sendMessage(TopManager.getTopDatabase(10));
            return true;
        }

        int value;


        try {
            value = Integer.parseInt(args[0]);
        } catch (NumberFormatException exception) {
            sender.sendMessage(MessageKeys.NOT_NUMBER);
            return true;
        }

        if (value < 0) {
            sender.sendMessage(MessageKeys.CANT_BE_NEGATIVE);
            return true;
        }

        if (value == 0) {
            sender.sendMessage(MessageKeys.CANT_BE_ZERO);
            return true;
        }

        if (value > 15) {
            sender.sendMessage(MessageKeys.CANT_BE_BIGGER);
            return true;
        }

        sender.spigot().sendMessage(TopManager.getTopDatabase(value));
        return true;
    }
}