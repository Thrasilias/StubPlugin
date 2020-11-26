package nl.thrasilias.muniverse;

import net.milkbowl.vault.chat.Chat;
import nl.thrasilias.muniverse.bot.Bot;
import nl.thrasilias.muniverse.command.CmdBot;
import nl.thrasilias.muniverse.configuration.Config;
import nl.thrasilias.muniverse.configuration.Lang;
import nl.thrasilias.muniverse.listener.BukkitListener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private final Bot bot;
    private static Chat chat = null;

    public Main() {
        super();
        instance = this;
        bot = new Bot(this);
    }

    public void onEnable() {
        Config.reload(this);
        Lang.reload(this);

        setupChat();
        bot.connect();

        getServer().getPluginManager().registerEvents(new BukkitListener(), this);

        getCommand("bot").setExecutor(new CmdBot(this));
    }

    public Bot getBot() {
        return bot;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    public static Chat getChat() {
        return chat;
    }

    public static Main getInstance() {
        return instance;
    }
}
