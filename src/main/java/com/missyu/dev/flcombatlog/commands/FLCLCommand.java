package com.missyu.dev.flcombatlog.commands;

import com.missyu.dev.flcombatlog.FLCombatLog;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FLCLCommand implements CommandExecutor, TabCompleter {

    private final FLCombatLog plugin;
    public FLCLCommand(FLCombatLog plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This is a player-only command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("flcombatlog.reload"))
            return false;

        if (args.length < 1)
            return false;

        String action = args[0];
        switch (action.toLowerCase()) {
            case "reload":
                plugin.reloadConfig();
                player.sendMessage(ChatColor.GREEN + "Configuration reloaded successfully!");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown command. Use /" + label + " reload.");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String[] commands = new String[]{"reload"};
            for (String cmd : commands)
                if (cmd.toLowerCase().startsWith(args[0].toLowerCase()))
                    completions.add(cmd);
        }

        return completions;
    }
}
