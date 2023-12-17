package net.coma.ctoken.utils;


import net.coma.ctoken.CToken;
import net.coma.ctoken.commands.*;
import net.coma.ctoken.subcommand.PluginCommand;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CommandRegister {
    @SuppressWarnings("deprecation")
    public static void registerCommands() {

        for (Class<? extends PluginCommand> clazz : getCommandClasses()) {
            try {
                PluginCommand commandInstance = clazz.getDeclaredConstructor().newInstance();
                Objects.requireNonNull(Bukkit.getCommandMap()).register(CToken.getInstance().getDescription().getName(), commandInstance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                exception.printStackTrace();
            }
        }
    }

    private static Set<Class<? extends PluginCommand>> getCommandClasses() {
        Set<Class<? extends PluginCommand>> commandClasses = new HashSet<>();
        commandClasses.add(CommandReload.class);
        commandClasses.add(CommandTokenAdd.class);
        commandClasses.add(CommandTokenBalance.class);
        commandClasses.add(CommandTokenPay.class);
        commandClasses.add(CommandTokenReset.class);
        commandClasses.add(CommandTokenSet.class);
        commandClasses.add(CommandTokenTake.class);
        commandClasses.add(CommandTokenTop.class);
        return commandClasses;
    }
}
