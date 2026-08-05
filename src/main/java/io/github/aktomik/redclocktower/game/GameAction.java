package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.RedClocktower;
import io.github.aktomik.redclocktower.oldgame.*;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.function.BiConsumer;

public class GameAction {

	private GameAction() {}//static class

	// code/period
	static Map<GamePeriod, BiConsumer<BloodGame, CommandSender>> periodEnter = Map.ofEntries(

	Map.entry(GamePeriod.MORNING, (game, sender) -> {
		// game.sitTags();
		game.getWorld().setTime(0);
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
		// game.unsitTags();
		game.getWorld().setTime(6000);
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
		// game.sitTags();
		game.getWorld().setTime(12500);
		// game.clearVoteStep();
		game.getCircle().forEachSlots(BloodSlot::unlock);
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
		//game.unsitTags();
		game.getWorld().setTime(18000);
		//game.removeNominatedPlayer();
		//game.removePyloriPlayer();
		//game.clearVoteStep();
		game.getCircle().forEachSlots(BloodSlot::lock);
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

//	public static BiConsumer<BloodGame, CommandSender> next = (game, sender) -> {
//
//		sender.sendRichMessage("<gray>doing the next logic step...");
//
//		switch (game.getState()) {
//			case NOTHING -> {
//				game.doStep(OldGameStepAction.SETUP, sender);
//			}
//			case WAITING -> {
//				game.doStep(OldGameStepAction.START, sender);
//				game.setTime(GamePeriod.MEET);
//			}
//			case INGAME -> {
//				switch (game.getTime()) {
//					case MEET -> {
//						game.switchTime(GamePeriod.NIGHT, sender);
//					}
//					case NIGHT -> {
//						game.switchTime(GamePeriod.MORNING, sender);
//					}
//					case MORNING -> {
//						game.switchTime(GamePeriod.FREE, sender);
//					}
//					case FREE -> {
//						game.switchTime(GamePeriod.MEET, sender);
//					}
//				}
//			}
//			case ENDED -> {
//				game.doStep(OldGameStepAction.RESET, sender);
//			}
//		}
//	};

}
