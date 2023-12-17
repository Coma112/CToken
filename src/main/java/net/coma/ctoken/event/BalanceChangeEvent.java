package net.coma.ctoken.event;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BalanceChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final OfflinePlayer player;
    private final int oldBalance;
    private final int newBalance;

    public BalanceChangeEvent(@NotNull OfflinePlayer player, int oldBalance, int newBalance) {
        this.player = player;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public int getOldBalance() {
        return oldBalance;
    }

    public int getNewBalance() {
        return newBalance;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
