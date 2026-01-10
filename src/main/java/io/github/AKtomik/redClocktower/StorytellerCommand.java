package io.github.AKtomik.redClocktower;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class StorytellerCommand extends CommandBrigadierBase {

    // register
    public String name() {
        return "storyteller";
    }
    public List<String> aliases() {
        return List.of("blood", "redclocktower");
    }
    public String permission() {
        return "redclocktower.storyteller";
    }
    public Component description() {
        return Component.text("storyteller action for red clocktower");
    }

    // root
    public LiteralArgumentBuilder<CommandSourceStack> root() {
        return base()
        .then(subTime);
    }

    // time subcommand
    Map<BloodDayPeriod, Consumer<World>> dayPeriodsStartAction = Map.ofEntries(
    Map.entry(BloodDayPeriod.MORNING, (world) -> {
        Bukkit.getServer().broadcast(
        Component.text("it's the morning!").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
        );
        Bukkit.getServer().broadcast(
        Component.text("everyone to the townhall").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
        );
        world.setTime(0);
    }),
    Map.entry(BloodDayPeriod.FREE, (world) -> {
        Bukkit.getServer().broadcast(
        Component.text("wonder time").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
        );
        Bukkit.getServer().broadcast(
        Component.text("you are free to go").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
        );
        world.setTime(6000);
    }),
    Map.entry(BloodDayPeriod.MEET, (world) -> {
        Bukkit.getServer().broadcast(
        Component.text("debate time").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
        );
        Bukkit.getServer().broadcast(
        Component.text("everyone to the townhall").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
        );
        world.setTime(12000);
    }),
    Map.entry(BloodDayPeriod.NIGHT, (world) -> {
        Bukkit.getServer().broadcast(
        Component.text("the moon is rising...").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
        );
        Bukkit.getServer().broadcast(
        Component.text("go to your house and sleep well").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
        );
        world.setTime(18000);
    })
    );

    public LiteralArgumentBuilder<CommandSourceStack> subTime = Commands.literal("time")
    .then(Commands.argument("period", new BloodDayPeriodArgument())
    .executes(ctx -> {
        World world;
        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();

        Location location = ctx.getSource().getLocation();
        world = location.getWorld();
        final BloodDayPeriod dayPeriod = ctx.getArgument("period", BloodDayPeriod.class);

        sender.sendMessage(
        Component.text("switching to time ").color(NamedTextColor.LIGHT_PURPLE)
        .append(Component.text(dayPeriod.toString()).color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
        .append(Component.text("").color(NamedTextColor.LIGHT_PURPLE))
        );
        dayPeriodsStartAction.get(dayPeriod).accept(world);
        return Command.SINGLE_SUCCESS;
    }));
}

@NullMarked
enum BloodDayPeriod {
    MORNING,
    FREE,
    MEET,
    NIGHT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

@NullMarked
class BloodDayPeriodArgument implements CustomArgumentType.Converted<BloodDayPeriod, String> {

    private static final DynamicCommandExceptionType ERROR_INVALID_FLAVOR = new DynamicCommandExceptionType(flavor -> {
        return MessageComponentSerializer.message().serialize(Component.text(flavor + " is not a blood day period!"));
    });

    @Override
    public BloodDayPeriod convert(String nativeType) throws CommandSyntaxException {
        try {
            return BloodDayPeriod.valueOf(nativeType.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            throw ERROR_INVALID_FLAVOR.create(nativeType);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (BloodDayPeriod flavor : BloodDayPeriod.values()) {
            String name = flavor.toString();

            // Only suggest if the flavor name matches the user input
            if (name.startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(flavor.toString());
            }
        }

        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}