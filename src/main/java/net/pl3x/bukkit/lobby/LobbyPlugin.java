package net.pl3x.bukkit.lobby;

import net.pl3x.bukkit.lobby.command.CmdLobby;
import net.pl3x.bukkit.lobby.configuration.Config;
import net.pl3x.bukkit.lobby.configuration.Lang;
import net.pl3x.bukkit.lobby.listener.BukkitListener;
import org.bukkit.plugin.java.JavaPlugin;

public class LobbyPlugin extends JavaPlugin {
    private static LobbyPlugin instance;

    public LobbyPlugin() {
        instance = this;
    }

    public void onEnable() {
        Config.reload(this);
        Lang.reload(this);

        getServer().getPluginManager().registerEvents(new BukkitListener(), this);

        getCommand("lobby").setExecutor(new CmdLobby(this));
    }

    public static LobbyPlugin getInstance() {
        return instance;
    }
}
