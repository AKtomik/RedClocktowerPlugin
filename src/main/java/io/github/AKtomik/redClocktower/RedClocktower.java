package io.github.AKtomik.redClocktower;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class RedClocktower extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Enabled!");

        // list bridge commands
        List<CommandBrigadierBase> bridgeCommandsSource = List
        .of(new TagmeCommand(), new WhosendCommand(), new StorytellerCommand());

        // load bridge commands
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            for (CommandBrigadierBase brigadier : bridgeCommandsSource)
            {
                LiteralCommandNode<CommandSourceStack> build = brigadier.build();
                commands.registrar().register(build, brigadier.aliases());
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Disabled!");
    }
}
