package net.coma112.ctoken.enums;

import net.coma112.ctoken.enums.keys.ConfigKeys;
import net.coma112.ctoken.utils.StartingUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public enum FormatType {
    BASIC, basic,
    COMMAS, commas,
    DOT, dot;

    public static String format(int price) {
        if (!ConfigKeys.FORMATTING_ENABLED.getBoolean()) return String.valueOf(price);

        return switch (FormatType.valueOf(ConfigKeys.FORMATTING_TYPE.getString())) {
            case DOT, dot -> String.format("%,d", price).replace(",", ".");
            case COMMAS, commas -> String.format("%,d", price);
            case BASIC, basic -> {
                List<Map.Entry<Long, String>> sortedEntries = new ArrayList<>(StartingUtils.getBasicFormatOverrides().entrySet());
                sortedEntries.sort(Collections.reverseOrder(Map.Entry.comparingByKey()));

                for (Map.Entry<Long, String> entry : sortedEntries) {
                    if (price >= entry.getKey()) {
                        double formattedPrice = (double) price / entry.getKey();
                        yield new DecimalFormat("#.#").format(formattedPrice) + entry.getValue();
                    }
                }

                yield String.valueOf(price);
            }
        };
    }
}