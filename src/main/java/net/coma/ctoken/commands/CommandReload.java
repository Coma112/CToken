package net.coma.ctoken.commands;

import net.coma.ctoken.CToken;
import net.coma.ctoken.language.MessageKeys;
import net.coma.ctoken.subcommand.CommandInfo;
import net.coma.ctoken.subcommand.PluginCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandInfo(name = "tokenreload", requiresPlayer = false, permission = "ctoken.reload")
public class CommandReload extends PluginCommand {

    public CommandReload() {
        super("tokenreload");
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @NotNull String[] args) {

        CToken.getInstance().getLanguage().reload();
        CToken.getInstance().getCoinYML().reload();
        sender.sendMessage(MessageKeys.RELOAD);
        return true;

    }
}
