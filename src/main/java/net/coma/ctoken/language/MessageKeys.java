package net.coma.ctoken.language;

import net.coma.ctoken.CToken;
import net.coma.ctoken.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

public class MessageKeys {
    public static String PREFIX = getString("prefix");
    public static String NO_PERMISSION = PREFIX + getString("messages.no-permission");
    public static String RELOAD = PREFIX + getString("messages.reload");
    public static String PLAYER_REQUIRED = PREFIX + getString("messages.player-required");
    public static String SET_RIGHT_USAGE = PREFIX + getString("messages.set-right-usage");
    public static String RESET_RIGHT_USAGE = PREFIX + getString("messages.reset-right-usage");
    public static String TAKE_RIGHT_USAGE = PREFIX + getString("messages.take-right-usage");
    public static String PAY_RIGHT_USAGE = PREFIX + getString("messages.pay-right-usage");
    public static String ADD_RIGHT_USAGE = PREFIX + getString("messages.add-right-usage");
    public static String OTHER_BALANCE = PREFIX + getString("messages.otherbalance");
    public static String EVERYONE_RESET = PREFIX + getString("messages.everyone-reset");
    public static String RESET = PREFIX + getString("messages.reset");
    public static String TAKE_PLAYER = PREFIX + getString("messages.take-player");
    public static String TAKE_TARGET = PREFIX + getString("messages.take-target");
    public static String NOT_NUMBER = PREFIX + getString("messages.not-a-number");
    public static String ADD_PLAYER = PREFIX + getString("messages.add-player");
    public static String ADD_EVERYONE = PREFIX + getString("messages.add-everyone");
    public static String ADD_TARGET = PREFIX + getString("messages.add-target");
    public static String CANT_BE_BIGGER = PREFIX + getString("messages.cant-be-bigger");
    public static String CANT_BE_NEGATIVE = PREFIX + getString("messages.negative");
    public static String PAY_TARGET = PREFIX + getString("messages.pay-target");
    public static String NOT_ENOUGH_MONEY = PREFIX + getString("messages.not-enough-money");
    public static String NOT_ENOUGH_MONEY_TARGET = PREFIX + getString("messages.not-enough-money-target");
    public static String PAY_PLAYER = PREFIX + getString("messages.pay-player");
    public static String CANT_BE_ZERO = PREFIX + getString("messages.cant-be-zero");
    public static String OFFLINE_PLAYER = PREFIX + getString("messages.offline-player");
    public static String SELFBALANCE = PREFIX + getString("messages.selfbalance");
    public static String SET_BALANCE_PLAYER = PREFIX + getString("messages.set-balance-player");
    public static String SET_BALANCE_TARGET = PREFIX + getString("messages.set-balance-target");
    public static String CANT_BE_YOU = PREFIX + getString("messages.cant-be-you");

    private static String getString(@NotNull String path) {
        return MessageProcessor.process(CToken.getInstance().getLanguage().getString(path));
    }
}
