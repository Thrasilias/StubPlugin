package nl.thrasilias.muniverse.command;

import nl.thrasilias.muniverse.Main;
import nl.thrasilias.muniverse.configuration.Config;
import nl.thrasilias.muniverse.configuration.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;

public class CmdBot implements TabExecutor {
    private final Main plugin;

    public CmdBot(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "reload".startsWith(args[0].toLowerCase()) && sender.hasPermission("command.bot")) {
            return Collections.singletonList("reload");
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("command.bot")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        String response = "&6" + plugin.getName() + " &7v" + plugin.getDescription().getVersion();

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            Config.reload(plugin);
            Lang.reload(plugin);

            response += " reloaded";
        }

        Lang.send(sender, response);
        return true;
    }
}
