package net.coma.ctoken.enums;

import net.coma.ctoken.CToken;
import org.bukkit.configuration.file.YamlConfiguration;

public enum BadgeType {
    NOVICE,
    BEGINNER,
    COMPETENCE,
    PROFICIENT,
    EXPERT;

    public static BadgeType convertXPToBadge(int xp) {
        if (xp <= 1000) {
            return BadgeType.NOVICE;
        } else if (xp <= 2000) {
            return BadgeType.BEGINNER;
        } else if (xp <= 3000) {
            return BadgeType.COMPETENCE;
        } else if (xp <= 4000) {
            return BadgeType.PROFICIENT;
        } else {
            return EXPERT;
        }
    }

    public String getDisplayName() {
        String key = this.name().toLowerCase();
        YamlConfiguration language = CToken.getInstance().getLanguage().getYml();

        if (language.contains("badges." + key)) return language.getString("badges." + key, this.name());
        return this.name();
    }
}
