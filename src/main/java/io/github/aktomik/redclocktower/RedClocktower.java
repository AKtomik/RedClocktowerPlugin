package io.github.aktomik.redclocktower;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.aktomik.redclocktower.command.setup.SetupCommand;
import io.github.aktomik.redclocktower.game.PlayerListener;
import io.github.aktomik.redclocktower.utils.CommandBrigadierBase;
import io.github.aktomik.redclocktower.command.RichmessageCommand;
import io.github.aktomik.redclocktower.command.storyteller.StorytellerCommand;
import io.github.aktomik.redclocktower.command.TagmeCommand;
import io.github.aktomik.redclocktower.command.WhosendCommand;
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

        // setup events
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        // list bridge commands
        List<CommandBrigadierBase> bridgeCommandsSource = List
        .of(new StorytellerCommand(), new SetupCommand(), new TagmeCommand(), new WhosendCommand(), new RichmessageCommand());

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
