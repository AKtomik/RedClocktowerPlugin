package io.github.aktomik.redclocktower.oldgame;

import io.github.aktomik.redclocktower.RedClocktower;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.function.BiConsumer;

public class OldGameAction {

	private OldGameAction() {}//static method

	static Map<OldGameStepAction, BiConsumer<OldBloodGame, CommandSender>> step = Map.ofEntries(

	Map.entry(OldGameStepAction.SETUP, (game, sender) -> {
		if (game.isReady()) {
			sender.sendRichMessage("<red>game is already setup!");
			return;
		}
		game.world.setTime(12000);
		game.world.setGameRule(GameRules.ADVANCE_TIME, false);
		game.world.setGameRule(GameRules.KEEP_INVENTORY, true);
		game.world.setSpawnLocation(game.getPosition(OldGamePlace.SPAWN));
		game.world.setDifficulty(Difficulty.PEACEFUL);
		game.generateNewId();
		game.setupTeam();
		game.applySlotLimit();
		game.mutateSlots(OldBloodSlot::lock);
		sender.sendRichMessage("<light_purple>setup game <dark_gray><round_id>", Placeholder.parsed("round_id", game.getRoundId()));
		game.setState(OldGameState.WAITING);
	}),

	Map.entry(OldGameStepAction.START, (game, sender) -> {
		if (!game.isReady()) {
			sender.sendRichMessage("<red>game is not setup!");
			return;
		}
		game.setState(OldGameState.INGAME);
		sender.sendRichMessage("<light_purple>starting game <dark_gray><round_id>", Placeholder.parsed("round_id", game.getRoundId()));
		game.broadcast("<red><b>are you ready to bleed?");
	}),

	Map.entry(OldGameStepAction.FINISH, (game, sender) -> {
		if (!game.isStarted()) {
			sender.sendRichMessage("<red>game is not started!");
			return;
		}
		game.setState(OldGameState.ENDED);
		sender.sendRichMessage("<light_purple>ending game <dark_gray><round_id>", Placeholder.parsed("round_id", game.getRoundId()));
		game.broadcast("<red><b>the game is over!");
	}),

	Map.entry(OldGameStepAction.RESET, (game, sender) -> {
		for (OfflinePlayer offlinePlayer : game.getAllPlayersAsOffline())
			game.removePlayer(offlinePlayer);
		game.clearSlotsUuid();
		game.removeNominatedPlayer();
		game.removePyloriPlayer();
		game.deleteTeam();
		game.setState(OldGameState.NOTHING);
		sender.sendRichMessage("<light_purple>reseting game");
	}),

	Map.entry(OldGameStepAction.REPLAY, (game, sender) -> {
		if (!game.isStarted() && !game.isEnded()) {
			sender.sendRichMessage("<red>game is not started nor ended!");
			return;
		}
		game.setState(OldGameState.WAITING);
		sender.sendRichMessage("<light_purple>waiting for a new game with same players and settings <dark_gray><round_id>", Placeholder.parsed("round_id", game.getRoundId()));
	}),

	Map.entry(OldGameStepAction.CLEAR, (game, sender) -> {
		if (game.isReady()) {
			sender.sendRichMessage("<red>game is running!");
			return;
		}
		game.world.setGameRule(GameRules.ADVANCE_TIME, true);
		game.setState(OldGameState.NOTHING);
		sender.sendRichMessage("<light_purple>clearing game");
	})

	);


	static Map<OldGameDebugAction, BiConsumer<OldBloodGame, CommandSender>> debug = Map.ofEntries(

	Map.entry(OldGameDebugAction.CLEAN_PLAYERS, (game, sender) -> {
		game.clearSlotsUuid();// will have to refresh slot limit
		game.clearStorytellerUuid();
		game.removeNominatedPlayer();
		game.removePyloriPlayer();
		sender.sendRichMessage("<light_purple>players of game were brutally cleaned");
		sender.sendRichMessage("<#ff6600>NECESSARY: you have to set back the slot limit.");
		sender.sendRichMessage("<#ff6600>ADVICE: restart server or at least deco/reco all players.");
	}),

	Map.entry(OldGameDebugAction.CLEAN_SLOTS, (game, sender) -> {
	 	game.clearSlotsPdc();
		sender.sendRichMessage("<light_purple>slots of game were brutally cleaned");
		sender.sendRichMessage("<#ff6600>NECESSARY: you have to add and place back all slots.");
	}),

	Map.entry(OldGameDebugAction.CLEAN_TEAM, (game, sender) -> {
		game.deleteTeam();
		sender.sendRichMessage("<light_purple>team was brutally cleaned");
		sender.sendRichMessage("<#ff6600>NECESSARY: you have to run game setup.");
	}),

	Map.entry(OldGameDebugAction.CLEAN_ALL, (game, sender) -> {
		sender.sendRichMessage("<light_purple><b>running all clean actions...");
		game.doDebug(OldGameDebugAction.CLEAN_PLAYERS, sender);
		game.doDebug(OldGameDebugAction.CLEAN_SLOTS, sender);
		game.doDebug(OldGameDebugAction.CLEAN_TEAM, sender);
		sender.sendRichMessage("<light_purple><b>done!");
	})

	);


	// code/period
	static Map<OldGamePeriod, BiConsumer<OldBloodGame, CommandSender>> periodEnter = Map.ofEntries(

	Map.entry(OldGamePeriod.MORNING, (game, sender) -> {
		game.sitTags();
		game.world.setTime(0);
		game.pingSound(Sound.BLOCK_BELL_USE, OldBloodGame.EVENT_VOLUME, .3f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_BELL_USE, OldBloodGame.EVENT_VOLUME, .4f);
			game.broadcast("<white><b>it's the morning!");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_BELL_USE, OldBloodGame.EVENT_VOLUME, .5f);
			game.broadcast("<gray><i>everyone is attended to the townhall");
		}, 40L);
	}),

	Map.entry(OldGamePeriod.FREE, (game, sender) -> {
		game.unsitTags();
		game.world.setTime(6000);
		game.pingSound(Sound.BLOCK_ANVIL_LAND, OldBloodGame.EVENT_VOLUME, 1.7f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_ANVIL_LAND, OldBloodGame.EVENT_VOLUME, 1.7f);
			game.broadcast("<white><b>wonder time");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_ANVIL_LAND, OldBloodGame.EVENT_VOLUME, 1.7f);
			game.broadcast("<gray><i>you are free to go and talk");
		}, 40L);
	}),

	Map.entry(OldGamePeriod.MEET, (game, sender) -> {
		game.sitTags();
		game.world.setTime(12500);
		game.clearVoteStep();
		game.mutateSlots(OldBloodSlot::unlock);
		game.pingSound(Sound.BLOCK_BELL_USE, OldBloodGame.EVENT_VOLUME, .3f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_BELL_USE, OldBloodGame.EVENT_VOLUME, .4f);
			game.broadcast("<white><b>debate time");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_BELL_USE, OldBloodGame.EVENT_VOLUME, .5f);
			game.broadcast("<gray><i>everyone is attended to the townhall");
		}, 40L);
	}),

	Map.entry(OldGamePeriod.NIGHT, (game, sender) -> {
		game.unsitTags();
		game.world.setTime(18000);
		game.removeNominatedPlayer();
		game.removePyloriPlayer();
		game.clearVoteStep();
		game.mutateSlots(OldBloodSlot::lock);
		game.pingSound(Sound.ENTITY_ALLAY_HURT, OldBloodGame.EVENT_VOLUME, .0f);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_WOODEN_DOOR_OPEN, OldBloodGame.EVENT_VOLUME, .9f);
			game.broadcast("<white><b>the moon is rising...");
		}, 20L);
		Bukkit.getScheduler().runTaskLater(RedClocktower.plugin(), () -> {
			game.pingSound(Sound.BLOCK_WOODEN_DOOR_CLOSE, OldBloodGame.EVENT_VOLUME, .9f);
			game.broadcast("<gray><i>go to your house and sleep well");
		}, 40L);
	})

	);

	public static BiConsumer<OldBloodGame, CommandSender> next = (game, sender) -> {

		sender.sendRichMessage("<gray>doing the next logic step...");

		switch (game.getState()) {
			case NOTHING -> {
				game.doStep(OldGameStepAction.SETUP, sender);
			}
			case WAITING -> {
				game.doStep(OldGameStepAction.START, sender);
				game.setTime(OldGamePeriod.MEET);
			}
			case INGAME -> {
				switch (game.getTime()) {
					case MEET -> {
						game.switchTime(OldGamePeriod.NIGHT, sender);
					}
					case NIGHT -> {
						game.switchTime(OldGamePeriod.MORNING, sender);
					}
					case MORNING -> {
						game.switchTime(OldGamePeriod.FREE, sender);
					}
					case FREE -> {
						game.switchTime(OldGamePeriod.MEET, sender);
					}
				}
			}
			case ENDED -> {
				game.doStep(OldGameStepAction.RESET, sender);
			}
		}
	};

}
