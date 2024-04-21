package net.coma.ctoken.listeners;

import net.coma.ctoken.CToken;
import net.coma.ctoken.event.BalanceChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        CToken.getInstance().getDatabaseManager().createPlayer(event.getPlayer());
    }

    @EventHandler
    public void onBalanceChange(BalanceChangeEvent event) {
        CToken.getInstance().getDatabaseManager().handleBalanceChangeEvent(event);
    }
}
