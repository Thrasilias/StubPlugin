package nl.thrasilias.muniverse;

import nl.thrasilias.muniverse.configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {

    private static void log(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', "&7[&6WildCore&7]&6 " + msg);
        if (!Config.COLOR_LOGS) {
            msg = ChatColor.stripColor(msg);
        }
        Bukkit.getServer().getConsoleSender().sendMessage(msg);
    }

    public static void debug(String msg) {
        if (Config.DEBUG_MODE) {
            log("&7[&eDEBUG&7]&e " + msg);
        }
    }

    public static void warn(String msg) {
        log("&e[&6WARN&e]&6 " + msg);
    }

    public static void error(String msg) {
        log("&e[&4ERROR&e]&4 " + msg);
    }

    public static void info(String msg) {
        log("&e[&fINFO&e]&r " + msg);
    }
}