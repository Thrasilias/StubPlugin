package net.pl3x.bukkit.stub;

import net.pl3x.bukkit.stub.command.CmdStub;
import net.pl3x.bukkit.stub.configuration.Config;
import net.pl3x.bukkit.stub.configuration.Lang;
import net.pl3x.bukkit.stub.listener.BukkitListener;
import org.bukkit.plugin.java.JavaPlugin;

public class StubPlugin extends JavaPlugin {
    private static StubPlugin instance;

    public StubPlugin() {
        instance = this;
    }

    public void onEnable() {
        Config.reload(this);
        Lang.reload(this);

        getServer().getPluginManager().registerEvents(new BukkitListener(), this);

        getCommand("stub").setExecutor(new CmdStub(this));
    }

    public static StubPlugin getInstance() {
        return instance;
    }
}
