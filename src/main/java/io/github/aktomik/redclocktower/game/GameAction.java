package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.RedClocktower;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.function.BiConsumer;

public class GameAction {

	private GameAction() {}//static method

	static Map<BloodGameStepAction, BiConsumer<BloodGame, CommandSender>> step = Map.ofEntries(

	Map.entry(BloodGameStepAction.SETUP, (game, sender) -> {
		if (game.isReady()) {
			sender.sendRichMessage("<red>game is already setup!");
			return;
		}
		game.world.setTime(12000);
		game.world.setGameRule(GameRules.ADVANCE_TIME, false);
		game.world.setDifficulty(Difficulty.PEACEFUL);
		game.generateNewId();
		game.setupTeam();
		game.applySlotLimit();
		game.mutateSlots(BloodSlot::lock);
		sender.sendRichMessage("<light_purple>setup game <dark_gray><round_id>", Placeholder.parsed("round_id", game.getRoundId()));
		game.setState(BloodGameState.WAITING);
	}),

	Map.entry(BloodGameStepAction.START, (game, sender) -> {
		if (!game.isReady()) {
			sender.sendRichMessage("<red>game is not setup!");
			return;
		}
		game.setState(BloodGameState.INGAME);
		sender.sendRichMessage("<light_purple>starting game <dark_gray><round_id>", Placeholder.parsed("round_id", game.getRoundId()));
		game.broadcast("<red><b>are you ready to bleed?");
	}),

	Map.entry(BloodGameStepAction.FINISH, (game, sender) -> {
		if (!game.isStarted()) {
			sender.sendRichMessage("<red>game is not started!");
			return;
		}
		game.setState(BloodGameState.ENDED);
		sender.sendRichMessage("<light_purple>ending game <dark_gray><round_id>", Placeholder.parsed("round_id", game.getRoundId()));
		game.broadcast("<red><b>the game is over!");
	}),

	Map.entry(BloodGameStepAction.RESET, (game, sender) -> {
		for (OfflinePlayer offlinePlayer : game.getAllPlayersAsOffline())
			game.removePlayer(offlinePlayer);
		game.clearSlotsUuid();
		game.removeNominatedPlayer();
		game.removePyloriPlayer();
		game.deleteTeam();
		game.setState(BloodGameState.NOTHING);
		sender.sendRichMessage("<light_purple>reseting game");
	}),

	Map.entry(BloodGameStepAction.REPLAY, (game, sender) -> {
		if (!game.isStarted() && !game.isEnded()) {
			sender.sendRichMessage("<red>game is not started nor ended!");
			return;
		}
		game.setState(BloodGameState.WAITING);
		sender.sendRichMessage("<light_purple>waiting for a new game with same players and settings <dark_gray><round_id>", Placeholder.parsed("round_id", game.getRoundId()));
	}),

	Map.entry(BloodGameStepAction.CLEAR, (game, sender) -> {
		if (game.isReady()) {
			sender.sendRichMessage("<red>game is running!");
			return;
		}
		game.world.setGameRule(GameRules.ADVANCE_TIME, true);
		game.setState(BloodGameState.NOTHING);
		sender.sendRichMessage("<light_purple>clearing game");
	})

	);


	static Map<BloodGameDebugAction, BiConsumer<BloodGame, CommandSender>> debug = Map.ofEntries(

	Map.entry(BloodGameDebugAction.CLEAN_PLAYERS, (game, sender) -> {
		game.clearSlotsUuid();// will have to refresh slot limit
		game.clearStorytellerUuid();
		game.removeNominatedPlayer();
		game.removePyloriPlayer();
		sender.sendRichMessage("<light_purple>players of game were brutally cleaned");
		sender.sendRichMessage("<#ff6600>NECESSARY: you have to set back the slot limit.");
		sender.sendRichMessage("<#ff6600>ADVICE: restart server or at least deco/reco all players.");
	}),

	Map.entry(BloodGameDebugAction.CLEAN_SLOTS, (game, sender) -> {
	 	game.clearSlotsPdc();
		sender.sendRichMessage("<light_purple>slots of game were brutally cleaned");
		sender.sendRichMessage("<#ff6600>NECESSARY: you have to add and place back all slots.");
	}),

	Map.entry(BloodGameDebugAction.CLEAN_TEAM, (game, sender) -> {
		game.deleteTeam();
		sender.sendRichMessage("<light_purple>team was brutally cleaned");
		sender.sendRichMessage("<#ff6600>NECESSARY: you have to run game setup.");
	}),

	Map.entry(BloodGameDebugAction.CLEAN_ALL, (game, sender) -> {
		sender.sendRichMessage("<light_purple><b>running all clean actions...");
		game.doDebug(BloodGameDebugAction.CLEAN_PLAYERS, sender);
		game.doDebug(BloodGameDebugAction.CLEAN_SLOTS, sender);
		game.doDebug(BloodGameDebugAction.CLEAN_TEAM, sender);
		sender.sendRichMessage("<light_purple><b>done!");
	})

	);


	// code/period
	static Map<BloodGamePeriod, BiConsumer<BloodGame, CommandSender>> periodEnter = Map.ofEntries(

	Map.entry(BloodGamePeriod.MORNING, (game, sender) -> {
		game.world.setTime(0);
		game.pingSound(Sound.BLOCK_BELL_USE, BloodGame.EVENT_VOLUME, 0.3f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_BELL_USE, BloodGame.EVENT_VOLUME, 0.4f);
			game.broadcast("<white><b>it's the morning!");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_BELL_USE, BloodGame.EVENT_VOLUME, 0.5f);
			game.broadcast("<gray><i>everyone is attended to the townhall");
		}, 40L);
	}),

	Map.entry(BloodGamePeriod.FREE, (game, sender) -> {
		game.world.setTime(6000);
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

	Map.entry(BloodGamePeriod.MEET, (game, sender) -> {
		game.world.setTime(12500);
		game.mutateSlots(BloodSlot::unlock);
		game.pingSound(Sound.BLOCK_BELL_USE, BloodGame.EVENT_VOLUME, 0.3f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_BELL_USE, BloodGame.EVENT_VOLUME, 0.4f);
			game.broadcast("<white><b>debate time");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_BELL_USE, BloodGame.EVENT_VOLUME, 0.5f);
			game.broadcast("<gray><i>everyone is attended to the townhall");
		}, 40L);
	}),

	Map.entry(BloodGamePeriod.NIGHT, (game, sender) -> {
		game.removeNominatedPlayer();
		game.removePyloriPlayer();
		game.world.setTime(18000);
		game.mutateSlots(BloodSlot::lock);
		game.pingSound(Sound.ENTITY_ALLAY_HURT, BloodGame.EVENT_VOLUME, 0f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_WOODEN_DOOR_OPEN, BloodGame.EVENT_VOLUME, 0.9f);
			game.broadcast("<white><b>the moon is rising...");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_WOODEN_DOOR_CLOSE, BloodGame.EVENT_VOLUME, 0.9f);
			game.broadcast("<gray><i>go to your house and sleep well");
		}, 40L);
	})

	);

}
