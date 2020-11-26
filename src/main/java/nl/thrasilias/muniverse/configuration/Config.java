package nl.thrasilias.muniverse.configuration;

import com.google.common.base.Throwables;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

public class Config {
    public static final Map<String, Object> DISCORD_ROLES = null;
    public static String DEFAULT_RANK = "Member";
    public static ConfigurationSection MODLOGS;
    public static String CHAT_CHANNEL = "538807616189628427";
    public static String CONSOLE_CHANNEL = "777607153404870658";
    public static String MODLOGS_CHANNEL = "777586128446160937";
    public static boolean COLOR_LOGS = true;
    public static boolean DEBUG_MODE = false;
    public static String DISCORD_TOKEN = "discord.yml";
    public static String LANGUAGE_FILE = "lang-en.yml";

    private static void init() {
        LANGUAGE_FILE = getString("language-file", LANGUAGE_FILE);
        DISCORD_TOKEN = getString("discord.bot.token", DISCORD_TOKEN);
    }

    // ############################  DO NOT EDIT BELOW THIS LINE  ############################

    /**
     * Reload the configuration file
     */
    public static void reload(Plugin plugin) {
        plugin.saveDefaultConfig();

        MODLOGS = config.getConfigurationSection("discord.global-modlogs");

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load config.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header("This is the configuration file for " + plugin.getName());
        config.options().copyDefaults(true);

        Config.init();

        try {
            config.save(configFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + configFile, ex);
        }
    }

    private static YamlConfiguration config;

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    private static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }
}
