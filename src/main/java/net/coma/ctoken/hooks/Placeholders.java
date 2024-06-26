package net.coma.ctoken.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.coma.ctoken.CToken;
import net.coma.ctoken.enums.BadgeType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "ct";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Coma112";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(@NotNull Player player, @NotNull String params) {
        switch (params) {
            case "balance" -> {
                return String.valueOf(CToken.getInstance().getDatabaseManager().getBalance(player));
            }

            case "formatted_balance" -> {
                return CToken.getInstance().getDatabaseManager().getFormattedBalance(player);
            }


            case "commas_formatted_balance" -> {
                return CToken.getInstance().getDatabaseManager().getFormattedCommasBalance(player);
            }

            case "dot_formatted_balance" -> {
                return CToken.getInstance().getDatabaseManager().getFormattedDotBalance(player);
            }

            case "top_place" -> {
                return CToken.getInstance().getDatabaseManager().getTopPlace(player) + ".";
            }

            case "badge" -> {
                int playerXP = CToken.getInstance().getDatabaseManager().getXP(player);
                BadgeType badge = BadgeType.convertXPToBadge(playerXP);

                if (badge != null) return badge.getDisplayName();
                return "Unknown";
            }
        }

        if (params.startsWith("top_")) {
            try {
                int pos = Integer.parseInt(params.split("_")[1]);

                if (CToken.getInstance().getDatabaseManager().getTopPlayer(pos) != null)
                    return CToken.getInstance().getDatabaseManager().getTopPlayer(pos);
                return "---";
            } catch (Exception exception) {
                return "";
            }
        }

        if (params.startsWith("topbal_")) {
            try {
                int pos = Integer.parseInt(params.split("_")[1]);

                if (CToken.getInstance().getDatabaseManager().getTopBalance(pos) != 0)
                    return String.valueOf(CToken.getInstance().getDatabaseManager().getTopBalance(pos));
                return "---";
            } catch (Exception exception) {
                return "";
            }
        }

        return null;
    }
}
