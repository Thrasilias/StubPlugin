package nl.thrasilias.muniverse.chat;

import nl.thrasilias.muniverse.chat.command.CmdChat;
import nl.thrasilias.muniverse.chat.configuration.Config;
import nl.thrasilias.muniverse.chat.configuration.Lang;
import nl.thrasilias.muniverse.chat.listener.PurpurListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Chat extends JavaPlugin {
    private static Chat instance;

    public Chat() {
        instance = this;
    }

    public void onEnable() {
        Config.reload(this);
        Lang.reload(this);

        getServer().getPluginManager().registerEvents(new PurpurListener(), this);

        getCommand("chat").setExecutor(new CmdChat(this));
    }

    public static Chat getInstance() {
        return instance;
    }
}
