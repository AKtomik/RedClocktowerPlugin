package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.DataKey;
import io.github.aktomik.redclocktower.utils.PlayerNameTagEditor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BloodPlayer {

	// class
	private final Player player;
	private final PersistentDataContainer pdc;
	private BloodPlayer(Player player)
	{
		this.player = player;
		this.pdc = player.getPersistentDataContainer();
	}
	public static BloodPlayer get(Player player)
	{
		return new BloodPlayer(player);
	}

	// set & get
	public void setDisplayName(String displayName)
	{
		pdc.set(DataKey.PLAYER_DISPLAY_NAME.key(), PersistentDataType.STRING, displayName);
	}
	public void clearDisplayName()
	{
		pdc.remove(DataKey.PLAYER_DISPLAY_NAME.key());
	}
	public String getDisplayName()
	{
		return pdc.get(DataKey.PLAYER_DISPLAY_NAME.key(), PersistentDataType.STRING);
	}

	private void setAlive(boolean isAlive)
	{
		pdc.set(DataKey.PLAYER_ALIVE.key(), PersistentDataType.BOOLEAN, isAlive);
	}
	private void clearAlive()
	{
		pdc.remove(DataKey.PLAYER_ALIVE.key());
	}
	public boolean getAlive()
	{
		return pdc.getOrDefault(DataKey.PLAYER_ALIVE.key(), PersistentDataType.BOOLEAN, true);
	}

	private void setVoteToken(boolean isVoteToken)
	{
		pdc.set(DataKey.PLAYER_VOTE_TOKEN.key(), PersistentDataType.BOOLEAN, isVoteToken);
	}
	private void clearVoteToken()
	{
		pdc.remove(DataKey.PLAYER_VOTE_TOKEN.key());
	}
	public boolean getVoteToken()
	{
		return pdc.getOrDefault(DataKey.PLAYER_VOTE_TOKEN.key(), PersistentDataType.BOOLEAN, true);
	}

	private void setVotePull(boolean isVotePull)
	{
		pdc.set(DataKey.PLAYER_VOTE_PULL.key(), PersistentDataType.BOOLEAN, isVotePull);
	}
	private void clearVotePull()
	{
		pdc.remove(DataKey.PLAYER_VOTE_PULL.key());
	}
	public boolean getVotePull()
	{
		return pdc.getOrDefault(DataKey.PLAYER_VOTE_PULL.key(), PersistentDataType.BOOLEAN, false);
	}

	private void setGame(BloodGame game)
	{
		pdc.set(DataKey.PLAYER_GAME_WORLD_NAME.key(), PersistentDataType.STRING, game.world.getName());
		pdc.set(DataKey.PLAYER_GAME_ROUND_ID.key(), PersistentDataType.STRING, game.getRoundId());
	}
	private void clearGame()
	{
		pdc.remove(DataKey.PLAYER_GAME_WORLD_NAME.key());
		pdc.remove(DataKey.PLAYER_GAME_ROUND_ID.key());
	}
	public BloodGame getGame()
	{
		if (!pdc.has(DataKey.PLAYER_GAME_WORLD_NAME.key())) return null;
		String worldName = pdc.get(DataKey.PLAYER_GAME_WORLD_NAME.key(), PersistentDataType.STRING);
		if (worldName == null) return null;
		World world = Bukkit.getWorld(worldName);
		if (world == null) return null;
		BloodGame bloodGame = BloodGame.get(world);
		if (!isSpectator() && !bloodGame.isPlayerIn(player)) {
			clearGame();
			return null;
		}
		return bloodGame;
	}

	private void setSlotIndex(int index)
	{
		pdc.set(DataKey.PLAYER_GAME_SLOT_INDEX.key(), PersistentDataType.INTEGER, index);
	}
	public void clearSlotIndex()
	{
		pdc.remove(DataKey.PLAYER_GAME_SLOT_INDEX.key());
	}
	public int getSlotIndex()
	{
		return pdc.get(DataKey.PLAYER_GAME_SLOT_INDEX.key(), PersistentDataType.INTEGER);
	}

	private void setTraveller(boolean isTraveller)
	{
		pdc.set(DataKey.PLAYER_TRAVELLER.key(), PersistentDataType.BOOLEAN, isTraveller);
	}
	private void clearTraveller()
	{
		pdc.remove(DataKey.PLAYER_TRAVELLER.key());
	}
	public boolean getTraveller()
	{
		return pdc.getOrDefault(DataKey.PLAYER_TRAVELLER.key(), PersistentDataType.BOOLEAN, false);
	}

	private void setSpectator(boolean isSpectator)
	{
		pdc.set(DataKey.PLAYER_GAME_SPECTATOR.key(), PersistentDataType.BOOLEAN, isSpectator);
	}
	private void clearSpectator()
	{
		pdc.remove(DataKey.PLAYER_GAME_SPECTATOR.key());
	}
	public boolean getSpectator()
	{
		return pdc.getOrDefault(DataKey.PLAYER_GAME_SPECTATOR.key(), PersistentDataType.BOOLEAN, false);
	}

	private void setStoryteller(boolean isStoryteller)
	{
		pdc.set(DataKey.PLAYER_GAME_STORYTELLER.key(), PersistentDataType.BOOLEAN, isStoryteller);
	}
	private void clearStoryteller()
	{
		pdc.remove(DataKey.PLAYER_GAME_STORYTELLER.key());
	}
	public boolean getStoryteller()
	{
		return pdc.getOrDefault(DataKey.PLAYER_GAME_STORYTELLER.key(), PersistentDataType.BOOLEAN, false);
	}

	// game link

	void joinGame(BloodGame game, int slotIndex)
	{
		BloodGame lastGame = getGame();
		if (lastGame != null) leaveGame();
		setGame(game);
		setSlotIndex(slotIndex);

		game.getTeam().addPlayer(player);
		player.setGameMode(GameMode.ADVENTURE);
		revive();
		refreshNameTag();
	}

	// join without a slot index for spectators and storyteller
	void joinGame(BloodGame game, boolean storyteller)
	{
		BloodGame lastGame = getGame();
		if (lastGame != null) leaveGame();
		setGame(game);
		setSpectator(true);
		setStoryteller(storyteller);

		game.getTeam().addPlayer(player);
		refreshNameTag();
	}

	// is call by BloodGame to apply the game join player side (step 2/2)
	void quitGameFinalStep()
	{
		revive();
		quitSlotLamp();

		clearAlive();
		clearVotePull();
		clearVoteToken();
		clearTraveller();
		clearSpectator();
		clearStoryteller();

		clearSlotIndex();
		clearGame();// after that getGame is null

		clearNameTag();
	}

	// is publicly call and will call removePlayer on player game (step 1/3)
	public void leaveGame()
	{
		BloodGame game = getGame();
		if (game == null) return;
		game.removePlayer(this.player);
	}

	// when player disconnect of the server (can be called twice on restart)
	public void disconnect()
	{
		leaveGame();
	}

	// if
	boolean isInGame()
	{
		return getGame() != null;
	}

	public boolean isAlive()
	{
		return getAlive();
	}
	public boolean isSpectator()
	{
		return getSpectator();
	}
	public boolean isStoryteller()
	{
		return getStoryteller();
	}
	public boolean isTraveller()
	{
		return getTraveller();
	}
	public boolean hasToken() { return getVoteToken(); }
	public boolean isVoting()
	{
		return getVotePull();
	}
	public boolean hasVote() { return (isAlive() || hasToken()); }
	public boolean isSlotVoteLock() { return getGame().getSlots().get(getSlotIndex()).getLock(); }

	// action
	public void changeAlive(boolean value)
	{
		setAlive(value);
		if (value)
		{
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
		} else {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 9, true, false, false));
		}
		if (!hasVote()) setVotePull(false);
		refreshNameTag();
		refreshSlotLamp();
	}
	public void kill()
	{
		changeAlive(false);
	}
	public void revive()
	{
		changeAlive(true);
	}

	public void changeTraveller(boolean value)
	{
		setTraveller(value);
		refreshNameTag();
		refreshSlotLamp();
	}

	public void changeVoteToken(boolean value)
	{
		setVoteToken(value);
		if (!hasVote()) setVotePull(false);
		refreshNameTag();
		refreshSlotLamp();
	}

	public void changeVotePull(boolean value)
	{
		setVotePull(value);
		refreshNameTag();
		refreshSlotLamp();
	}

	public int getVote()
	{
		return (isVoting())
			? 1// we will be able to change vote power here
			: 0;
	}

	// refresh

	public void quitSlotLamp()
	{
		if (isSpectator()) return;
		BloodGame game = getGame();
		BloodSlot slot = game.getSlot(getSlotIndex());
		slot.refreshLamp(null);
	}

	public void refreshSlotLamp()
	{
		if (isSpectator()) return;
		BloodGame game = getGame();
		BloodSlot slot = game.getSlot(getSlotIndex());
		slot.refreshLamp(this);
	}

	public void clearNameTag()
	{
		player.playerListName(Component.text(player.getName()));
		PlayerNameTagEditor.clearDisplay(player);
	}

	public void refreshNameTag()
	{
		if (!isInGame())
		{
			clearNameTag();
			return;
		}
		MiniMessage mini = MiniMessage.miniMessage();

		String tokenColor = getTokenColor();
		String tokenCharacter = getTokenCharacter();

		String deathString = "";
		String spaceOfDeath = "";
		String slotString = "";

		if (!isSpectator())
		{
			deathString = (isAlive()) ? "" : "<gray>☠";
			spaceOfDeath = (isAlive()) ? "" : " ";
			slotString =Integer.toString(getSlotIndex()+1);
		}


		String nameString = player.getName();
		String oldNameString = "";
		String displayName = getDisplayName();
		if (displayName != null && !displayName.isEmpty())
		{
			oldNameString = "("+nameString+")";
			nameString = displayName;
		}

		TagResolver[] resolvers = new TagResolver[]{
			Placeholder.parsed("prefix_death", deathString),
			Placeholder.parsed("prefix_slot", slotString),
			Placeholder.parsed("death_space", spaceOfDeath),
			Placeholder.parsed("token_color", tokenColor),
			Placeholder.parsed("token_char", tokenCharacter),
			Placeholder.parsed("main_name", nameString),
			Placeholder.parsed("old_name", oldNameString)
		};
		player.playerListName(mini.deserialize(
		"<<token_color>><token_char></<token_color>><gray><prefix_slot></gray> <prefix_death><death_space><main_name> <dark_gray><old_name></dark_gray>",
			resolvers
		));
		PlayerNameTagEditor.changeDisplay(player, mini.deserialize(
		"<<token_color>><token_char></<token_color>><prefix_death> <main_name>",
			resolvers
		));
	}

	String getName()
	{
		String displayName = getDisplayName();
		if (displayName != null && !displayName.isEmpty()) return displayName;
		return player.getName();
	}

	String getTokenCharacter()
	{
		if (isStoryteller()) return "❇";
		if (isSpectator()) return "♟";
		if (isTraveller()) return "✳";
		return "✴";
	}

	String getTokenColor()
	{
		if (isStoryteller()) return "light_purple";
		if (isSpectator()) return "gray";
		return hasToken()
			? isAlive()
				? isVoting()
					? "yellow"
					: "red"
				: isVoting()
					? "aqua"
					: "blue"
			: isAlive()
				? isVoting()
					? "gold"
					: "gray"
				: "black";
	}
}