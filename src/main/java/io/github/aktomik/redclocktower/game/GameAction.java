package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.RedClocktower;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.function.BiConsumer;

public class GameAction {

	private GameAction() {}//static class

	// code/period
	static final Map<GamePeriod, BiConsumer<BloodGame, CommandSender>> periodEnter = Map.ofEntries(

	Map.entry(GamePeriod.MORNING, (game, sender) -> {
		game.getWorld().setTime(0);
		game.getCircle().showLabels();
		game.pingSound(Sound.BLOCK_BELL_USE, BloodGame.EVENT_VOLUME, .3f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_BELL_USE, BloodGame.EVENT_VOLUME, .4f);
			game.broadcast("<white><b>it's the morning!");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_BELL_USE, BloodGame.EVENT_VOLUME, .5f);
			game.broadcast("<gray><i>everyone is attended to the townhall");
		}, 40L);
	}),

	Map.entry(GamePeriod.FREE, (game, sender) -> {
		game.getWorld().setTime(6000);
		game.getCircle().hiddeLabels();
		game.pingSound(Sound.BLOCK_ANVIL_LAND, BloodGame.EVENT_VOLUME, 1.7f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_ANVIL_LAND, BloodGame.EVENT_VOLUME, 1.7f);
			game.broadcast("<white><b>wonder time");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_ANVIL_LAND, BloodGame.EVENT_VOLUME, 1.7f);
			game.broadcast("<gray><i>you are free to go and talk");
		}, 40L);
	}),

	Map.entry(GamePeriod.MEET, (game, sender) -> {
		game.getWorld().setTime(12500);
		game.getCircle().startVoteSession();
		game.getCircle().showLabels();
		game.getCircle().unlockAll();
		// game.clearVoteStep();
		game.pingSound(Sound.BLOCK_BELL_USE, BloodGame.EVENT_VOLUME, .3f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_BELL_USE, BloodGame.EVENT_VOLUME, .4f);
			game.broadcast("<white><b>debate time");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_BELL_USE, BloodGame.EVENT_VOLUME, .5f);
			game.broadcast("<gray><i>everyone is attended to the townhall");
		}, 40L);
	}),

	Map.entry(GamePeriod.NIGHT, (game, sender) -> {
		game.getWorld().setTime(18000);
		game.getCircle().endVoteSession();
		//game.removeNominatedPlayer();
		//game.removePyloriPlayer();
		//game.clearVoteStep();
		game.getCircle().lockAll();
		game.getCircle().hiddeLabels();
		game.pingSound(Sound.ENTITY_ALLAY_HURT, BloodGame.EVENT_VOLUME, .0f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_WOODEN_DOOR_OPEN, BloodGame.EVENT_VOLUME, .9f);
			game.broadcast("<white><b>the moon is rising...");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_WOODEN_DOOR_CLOSE, BloodGame.EVENT_VOLUME, .9f);
			game.broadcast("<gray><i>go to your house and sleep well");
		}, 40L);
	})
	);

	public static final BiConsumer<BloodGame, CommandSender> next = (game, sender) -> {

		if (!game.isStarted())
		{
			sender.sendRichMessage("<red>the game is not started!");
			return;
		}

		sender.sendRichMessage("<dark_purple>doing the next logic step...");
		switch (game.getPeriod()) {
			case MEET -> {
				game.switchPeriod(GamePeriod.NIGHT, sender);
			}
			case NIGHT -> {
				game.switchPeriod(GamePeriod.MORNING, sender);
			}
			case MORNING -> {
				game.switchPeriod(GamePeriod.FREE, sender);
			}
			case FREE -> {
				game.switchPeriod(GamePeriod.MEET, sender);
			}
		}
	};

}
