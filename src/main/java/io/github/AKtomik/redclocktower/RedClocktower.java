package io.github.AKtomik.redclocktower;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.AKtomik.redclocktower.brigadier.CommandBrigadierBase;
import io.github.AKtomik.redclocktower.command.RichmessageCommand;
import io.github.AKtomik.redclocktower.command.storyteller.StorytellerCommand;
import io.github.AKtomik.redclocktower.command.TagmeCommand;
import io.github.AKtomik.redclocktower.command.WhosendCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class RedClocktower extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Enabled!");

        // setup data keys
        DataKey.init(this);

        // list bridge commands
        List<CommandBrigadierBase> bridgeCommandsSource = List
        .of(new TagmeCommand(), new WhosendCommand(), new StorytellerCommand(), new RichmessageCommand());

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
