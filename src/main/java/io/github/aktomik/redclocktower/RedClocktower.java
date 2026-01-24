package io.github.aktomik.redclocktower;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.aktomik.redclocktower.command.Vote;
import io.github.aktomik.redclocktower.command.setup.Setup;
import io.github.aktomik.redclocktower.game.BloodPlayer;
import io.github.aktomik.redclocktower.game.PlayerListener;
import io.github.aktomik.redclocktower.utils.PlayerNameTagEditor;
import io.github.aktomik.redclocktower.utils.PlayerNameTagEditorListener;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierCommand;
import io.github.aktomik.redclocktower.command.BroadcastCommand;
import io.github.aktomik.redclocktower.command.storyteller.Storyteller;
import io.github.aktomik.redclocktower.command.Tagme;
import io.github.aktomik.redclocktower.command.Whosend;
import io.github.aktomik.redclocktower.utils.brigadier.BrigadierToolbox;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
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
        DataKey.init(this);

        // setup events
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerNameTagEditorListener(), this);

        // startups
        PlayerNameTagEditor.startUpdateTask(this);

        // load brigadier commands
        BrigadierToolbox.loadCommands(this,
            List.of(new Storyteller(), new Setup(), new Vote(), new Tagme(), new Whosend(), new BroadcastCommand())
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
            BloodPlayer bloodPlayer = BloodPlayer.get(player);
            bloodPlayer.disconnect();
        }

        // message
        getLogger().info("Disabled!");
    }
}
