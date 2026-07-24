package io.github.aktomik.redclocktower;

import io.github.aktomik.redclocktower.command.Vote;
import io.github.aktomik.redclocktower.command.setup.SetupCommand;
import io.github.aktomik.redclocktower.oldgame.OldBloodPlayer;
import io.github.aktomik.redclocktower.oldgame.OldPlayerListener;
import io.github.aktomik.redclocktower.utils.PlayerNameTagEditor;
import io.github.aktomik.redclocktower.utils.PlayerNameTagEditorListener;
import io.github.aktomik.redclocktower.command.BroadcastCommand;
import io.github.aktomik.redclocktower.command.storyteller.StorytellerCommand;
import io.github.aktomik.redclocktower.command.Tagme;
import io.github.aktomik.redclocktower.command.Whosend;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierToolbox;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        OldDataKey.init(this);
        DataKey.init(this);

        // setup events
        getServer().getPluginManager().registerEvents(new OldPlayerListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerNameTagEditorListener(), this);

        // startups
        PlayerNameTagEditor.startUpdateTask(this);

        // load brigadier commands
        BrigadierToolbox.loadCommands(this,
            List.of(new StorytellerCommand(), new SetupCommand(), new Vote(), new Tagme(), new Whosend(), new BroadcastCommand())
        );

        // message
        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // blood disconnect for all players
        for (Player player : Bukkit.getOnlinePlayers())
        {
            OldBloodPlayer bloodPlayer = OldBloodPlayer.get(player);
            bloodPlayer.disconnect();
        }

        // message
        getLogger().info("Disabled!");
    }
}
