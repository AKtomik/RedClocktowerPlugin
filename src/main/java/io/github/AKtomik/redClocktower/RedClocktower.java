package io.github.AKtomik.redClocktower;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class RedClocktower extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Enabled!");

//        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
//            commands.registrar().register(new TagmeCommand().root.build());
//        });

        // load bridge commands
        List<CommandBrigadierBase> bridgeCommandsSource = List.of(new TagmeCommand(), new WhosendCommand(), new StorytellerCommand());

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            for (CommandBrigadierBase brigadier : bridgeCommandsSource)
            {
                LiteralCommandNode<CommandSourceStack> build = brigadier.build();
                commands.registrar().register(build, brigadier.aliases());
            }
        });
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
