package io.github.aktomik.redclocktower;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.aktomik.redclocktower.command.VoteCommand;
import io.github.aktomik.redclocktower.command.setup.SetupCommand;
import io.github.aktomik.redclocktower.game.BloodPlayer;
import io.github.aktomik.redclocktower.game.PlayerListener;
import io.github.aktomik.redclocktower.utils.PlayerDisplayTagManager;
import io.github.aktomik.redclocktower.utils.brigadier.CommandBrigadierBase;
import io.github.aktomik.redclocktower.command.RichmessageCommand;
import io.github.aktomik.redclocktower.command.storyteller.StorytellerCommand;
import io.github.aktomik.redclocktower.command.TagmeCommand;
import io.github.aktomik.redclocktower.command.WhosendCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class RedClocktower extends JavaPlugin {

    public static Plugin plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        // setup data keys
        DataKey.init(this);

        // setup events
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDisplayTagManager(), this);

        // startups
        PlayerDisplayTagManager.startUpdateTask(this);

        // list bridge commands
        List<CommandBrigadierBase> bridgeCommandsSource = List
        .of(new StorytellerCommand(), new SetupCommand(), new VoteCommand(), new TagmeCommand(), new WhosendCommand(), new RichmessageCommand());

        // load bridge commands
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            for (CommandBrigadierBase brigadier : bridgeCommandsSource)
            {
                LiteralCommandNode<CommandSourceStack> build = brigadier.build();
                commands.registrar().register(build, brigadier.aliases());
            }
        });

        // message
        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // blood disconnect for all players
        for (Player player : Bukkit.getOnlinePlayers())
        {
            BloodPlayer bloodPlayer = BloodPlayer.get(player);
            bloodPlayer.disconnect();
        }

        // message
        getLogger().info("Disabled!");
    }
}
