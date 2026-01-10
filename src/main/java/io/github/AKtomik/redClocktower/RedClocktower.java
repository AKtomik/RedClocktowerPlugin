package io.github.AKtomik.redClocktower;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class RedClocktower extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Enabled!");
        loadCommand("storyteller", new StorytellerCommand());
    }

    public PluginCommand loadCommand(String cmdStr, CommandExecutor cmdExe)
    {
        PluginCommand pluginCommand = getCommand(cmdStr);
        if (pluginCommand == null) throw new IllegalStateException("§ccommand '%s' not registered in yml".formatted(cmdStr));
        pluginCommand.setExecutor(cmdExe);
        return  pluginCommand;
    }
    public PluginCommand loadCommand(String cmdStr, CommandExecutor cmdExe, TabCompleter cmdComplete)
    {
        PluginCommand pluginCommand = loadCommand(cmdStr, cmdExe);
        pluginCommand.setTabCompleter(cmdComplete);
        return pluginCommand;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Disabled!");
    }
}
