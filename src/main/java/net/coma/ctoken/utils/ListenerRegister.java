package net.coma.ctoken.utils;

import net.coma.ctoken.CToken;
import net.coma.ctoken.listeners.PlayerListener;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class ListenerRegister {
    @SuppressWarnings("deprecation")
    public static void registerEvents() {
        Set<Class<? extends Listener>> listenerClasses = getListenerClasses();

        for (Class<? extends Listener> clazz : listenerClasses) {
            try {
                CToken.getInstance().getServer().getPluginManager().registerEvents(clazz.newInstance(), CToken.getInstance());
            } catch (InstantiationException | IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private static Set<Class<? extends Listener>> getListenerClasses() {
        Set<Class<? extends Listener>> listenerClasses = new HashSet<>();
        listenerClasses.add(PlayerListener.class);
        return listenerClasses;
    }
}
