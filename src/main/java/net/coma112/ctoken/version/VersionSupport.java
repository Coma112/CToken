package net.coma112.ctoken.version;

import lombok.Getter;
import net.coma112.ctoken.utils.TokenLogger;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

@Getter
public class VersionSupport {
    private final ServerVersionSupport versionSupport;

    public VersionSupport(@NotNull Plugin plugin, @NotNull MinecraftVersion version) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (version == MinecraftVersion.UNKNOWN) throw new IllegalArgumentException("VERSION NOT FOUND!!!");


        Class<?> clazz = Class.forName("net.coma112.ctoken.version.nms." + version.name() + ".Version");
        versionSupport = (ServerVersionSupport) clazz.getConstructor(Plugin.class).newInstance(plugin);

        if (!versionSupport.isSupported()) {
            TokenLogger.warn("---   VERSION IS SUPPORTED BUT,   ---");
            TokenLogger.warn("The version you are using is badly");
            TokenLogger.warn("implemented. Many features won't work.");
            TokenLogger.warn("Please consider updating your server ");
            TokenLogger.warn("version to a newer version. (like 1.19_R2)");
            TokenLogger.warn("---   PLEASE READ THIS MESSAGE!   ---");
        }

        TokenLogger.info("Version support for {} loaded!", version);
    }
}