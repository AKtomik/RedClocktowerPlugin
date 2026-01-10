package io.github.AKtomik.redClocktower;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static org.bukkit.Bukkit.getWorlds;

public class StorytellerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {

        // permission
        assert command.getPermission() != null;
        if (!sender.hasPermission(command.getPermission()))
        {
            sender.sendMessage(Component.text("you don't have the permission to storytell!").color(NamedTextColor.RED));
            return false;
        }

        // error
        if (args.length == 0)
        {
            sender.sendMessage(Component.text("give a first argument!").color(NamedTextColor.RED));
            return true;
        }

        // subcommands
        switch (args[0])
        {
            case "time":
            {
                return subCommandTime(sender, command, Arrays.stream(args, 1, args.length).toArray(String[]::new));
            }
        }

        // error
        sender.sendMessage(
                Component.text("the argument ").color(NamedTextColor.RED)
                        .append(Component.text(args[0]).color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                        .append(Component.text(" does not exist!").color(NamedTextColor.RED))
        );
        return true;
    }

    Map<String, Consumer<World>> timeIdToAction = Map.ofEntries(
            Map.entry("morning", (world) -> {
                Bukkit.getServer().broadcast(
                    Component.text("it's the morning!").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
                );
                Bukkit.getServer().broadcast(
                    Component.text("everyone to the townhall").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
                );
                world.setTime(0);
            }),
            Map.entry("free", (world) -> {
                Bukkit.getServer().broadcast(
                    Component.text("wonder time").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
                );
                Bukkit.getServer().broadcast(
                    Component.text("you are free to go").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
                );
                world.setTime(6000);
            }),
            Map.entry("talk", (world) -> {
                Bukkit.getServer().broadcast(
                        Component.text("debate time").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
                );
                Bukkit.getServer().broadcast(
                        Component.text("everyone to the townhall").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
                );
                world.setTime(12000);
            }),
            Map.entry("night", (world) -> {
                Bukkit.getServer().broadcast(
                        Component.text("the moon is rising...").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
                );
                Bukkit.getServer().broadcast(
                        Component.text("go to your house and sleep well").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
                );
                world.setTime(18000);
            })
    );

    boolean subCommandTime(@NonNull CommandSender sender, @NonNull Command command, String @NonNull [] args)
    {
        if (args.length == 0)
        {
            sender.sendMessage(Component.text("give a time to switch!").color(NamedTextColor.RED));
            return true;
        }

        if (!timeIdToAction.containsKey(args[0]))
        {
            sender.sendMessage(
                    Component.text("the time ").color(NamedTextColor.RED)
                            .append(Component.text(args[0]).color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                            .append(Component.text(" does not exist!").color(NamedTextColor.RED))
            );
            return true;
        }

        World world;
        if (sender instanceof Player)
            world = ((Player) sender).getWorld();
        else if (sender instanceof CommandBlock)
            world = ((CommandBlock) sender).getWorld();
        else
            world = getWorlds().getFirst();

        sender.sendMessage(
                Component.text("switching to time ").color(NamedTextColor.LIGHT_PURPLE)
                        .append(Component.text(args[0]).color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("").color(NamedTextColor.LIGHT_PURPLE))
        );
        timeIdToAction.get(args[0]).accept(world);
        return  true;
    }
}
