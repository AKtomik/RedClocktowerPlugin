package io.github.aktomik.redclocktower;

import io.github.aktomik.redclocktower.command.BroadcastCommand;
import io.github.aktomik.redclocktower.command.TagmeCommand;
import io.github.aktomik.redclocktower.command.VoteCommand;
import io.github.aktomik.redclocktower.command.Whosend;
import io.github.aktomik.redclocktower.command.setup.SetupCommand;
import io.github.aktomik.redclocktower.command.storyteller.StorytellerCommand;
import io.github.aktomik.redclocktower.game.BloodGame;
import io.github.aktomik.redclocktower.game.GameListener;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierToolbox;
import io.github.aktomik.redclocktower.utils.renametag.PlayerRenameTagListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class RedClocktower extends JavaPlugin {

    private static Plugin plugin;
    public static Plugin plugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        // setup data keys
        DataKey.init(this);

        // setup events
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerRenameTagListener(), this);

        // load brigadier commands
        BrigadierToolbox.loadCommands(this,
            List.of(new StorytellerCommand(), new SetupCommand(), new VoteCommand(), new TagmeCommand(), new Whosend(), new BroadcastCommand())
        );

        // message
        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // game cleanup
        BloodGame.killAll();

        // message
        getLogger().info("Disabled!");
    }
}
