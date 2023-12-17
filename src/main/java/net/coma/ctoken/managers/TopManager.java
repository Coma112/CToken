package net.coma.ctoken.managers;

import org.jetbrains.annotations.NotNull;
import java.util.List;
import net.coma.ctoken.CToken;
import net.coma.ctoken.processor.MessageProcessor;
import net.md_5.bungee.api.chat.TextComponent;

public record TopManager(@NotNull String playerName, int balance) {
    @SuppressWarnings("deprecation")
    public static TextComponent getTopDatabase(int value) {
        List<TopManager> topBalance = CToken.getDatabaseManager().getTop(value);
        TextComponent message = new TextComponent(MessageProcessor.process("\n&aTop " + value + " Balances:&f\n\n"));

        for (int i = 0; i < topBalance.size(); i++) {
            TopManager balanceManager = topBalance.get(i);
            message.addExtra(MessageProcessor.process(String.format("&f%d. &a%s &f- &7(&a%d&7)", i + 1, balanceManager.playerName(), balanceManager.balance())));
            if (i < topBalance.size() - 1) message.addExtra("\n");
        }

        return message;
    }
}
