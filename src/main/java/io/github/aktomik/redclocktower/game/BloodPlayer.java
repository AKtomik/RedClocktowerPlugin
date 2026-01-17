package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.DataKey;
import io.github.aktomik.redclocktower.utils.PlayerDisplayTagManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

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
		pdc.set(DataKey.PLAYER_DISPLAY_NAME.key, PersistentDataType.STRING, displayName);
	}
	public void clearDisplayName()
	{
		pdc.remove(DataKey.PLAYER_DISPLAY_NAME.key);
	}
	public String getDisplayName()
	{
		return pdc.get(DataKey.PLAYER_DISPLAY_NAME.key, PersistentDataType.STRING);
	}

	private void setAlive(boolean isAlive)
	{
		pdc.set(DataKey.PLAYER_ALIVE.key, PersistentDataType.BOOLEAN, isAlive);
	}
	private void clearAlive()
	{
		pdc.remove(DataKey.PLAYER_ALIVE.key);
	}
	public boolean getAlive()
	{
		return pdc.getOrDefault(DataKey.PLAYER_ALIVE.key, PersistentDataType.BOOLEAN, true);
	}

	private void setVoteToken(boolean isVoteToken)
	{
		pdc.set(DataKey.PLAYER_VOTE_TOKEN.key, PersistentDataType.BOOLEAN, isVoteToken);
	}
	private void clearVoteToken()
	{
		pdc.remove(DataKey.PLAYER_VOTE_TOKEN.key);
	}
	public boolean getVoteToken()
	{
		return pdc.getOrDefault(DataKey.PLAYER_VOTE_TOKEN.key, PersistentDataType.BOOLEAN, true);
	}

	private void setVotePull(boolean isVotePull)
	{
		pdc.set(DataKey.PLAYER_VOTE_PULL.key, PersistentDataType.BOOLEAN, isVotePull);
	}
	private void clearVotePull()
	{
		pdc.remove(DataKey.PLAYER_VOTE_PULL.key);
	}
	public boolean getVotePull()
	{
		return pdc.getOrDefault(DataKey.PLAYER_VOTE_PULL.key, PersistentDataType.BOOLEAN, false);
	}

	private void setGame(BloodGame game)
	{
		pdc.set(DataKey.PLAYER_GAME_WORLD_NAME.key, PersistentDataType.STRING, game.world.getName());
		pdc.set(DataKey.PLAYER_GAME_ROUND_ID.key, PersistentDataType.STRING, game.getRoundId());
	}
	private void clearGame()
	{
		pdc.remove(DataKey.PLAYER_GAME_WORLD_NAME.key);
		pdc.remove(DataKey.PLAYER_GAME_ROUND_ID.key);
	}
	public BloodGame getGame()
	{
		if (!pdc.has(DataKey.PLAYER_GAME_WORLD_NAME.key)) return null;
		String worldName = pdc.get(DataKey.PLAYER_GAME_WORLD_NAME.key, PersistentDataType.STRING);
		if (worldName == null) return null;
		World world = Bukkit.getWorld(worldName);
		if (world == null) return null;
		BloodGame bloodGame = BloodGame.get(world);
		if (!bloodGame.isPlayerIn(player)) {
			clearGame();
			return null;
		}
		return bloodGame;
	}

	private void setSlotIndex(int index)
	{
		pdc.set(DataKey.PLAYER_GAME_SLOT_INDEX.key, PersistentDataType.INTEGER, index);
	}
	public void clearSlotIndex()
	{
		pdc.remove(DataKey.PLAYER_GAME_SLOT_INDEX.key);
	}
	public int getSlotIndex()
	{
		return pdc.get(DataKey.PLAYER_GAME_SLOT_INDEX.key, PersistentDataType.INTEGER);
	}

	// game link
	void joinGame(BloodGame game, int slotIndex)
	{
		BloodGame lastGame = getGame();
		if (lastGame != null) leaveGame();
		setGame(game);
		setSlotIndex(slotIndex);

		game.getTeam().addPlayer(player);
		revive();
		refreshNameTag();
	}

	// is call by BloodGame to apply the game join player side (step 2/2)
	void quitGame(BloodGame game)
	{
		revive();
		quitLamp();

		clearAlive();
		clearVotePull();
		clearVoteToken();

		clearGame();// after that getGame is null

		game.getTeam().removePlayer(player);
		clearNameTag();

	}

	// is publicly call and will call removePlayer on player game (step 1/3)
	public void leaveGame()
	{
		BloodGame game = getGame();
		if (game == null) return;
		game.removePlayer(this.player);
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
	public boolean isStoryteller()
	{
		return false;
	}
	public boolean isTraveller()
	{
		return false;
	}
	public boolean hasToken() { return getVoteToken(); }
	public boolean isVoting()
	{
		return getVotePull();
	}

	// action
	public void changeAlive(boolean value)
	{
		setAlive(value);
		if (value)
		{
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
		} else {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 9, true, false));
		}
		refreshNameTag();
		refreshLamp();
	}
	public void kill()
	{
		changeAlive(false);
	}
	public void revive()
	{
		changeAlive(true);
	}

	public void changeVoteToken(boolean value)
	{
		setVoteToken(value);
		refreshNameTag();
		refreshLamp();
	}

	public void changeVotePull(boolean value)
	{
		setVotePull(value);
		refreshNameTag();
		refreshLamp();
	}


	public void quitLamp()
	{
		BlockData data = BlockType.WAXED_COPPER_BLOCK.createBlockData();

		BloodGame game = getGame();
		BloodSlot slot = game.getSlot(getSlotIndex());
		Location loc = slot.getPosition(BloodSlotPlace.LAMP);
		game.world.setBlockData(loc, data);
	}

	public void refreshLamp()
	{
		boolean voting = getVotePull();
		boolean alive = getAlive();

		BlockData data = BlockType.WAXED_COPPER_BULB.createBlockData();
		if (voting)
		{
			if (data instanceof Lightable lightable) {
				lightable.setLit(true);
				lightable.copyTo(data);
			}
		}

		BloodGame game = getGame();
		BloodSlot slot = game.getSlot(getSlotIndex());
		Location loc = slot.getPosition(BloodSlotPlace.LAMP);
		game.world.setBlockData(loc, data);
	}

	public void clearNameTag()
	{
		player.playerListName(Component.text(player.getName()));
		PlayerDisplayTagManager.clearDisplay(player);
	}

	public void refreshNameTag()
	{
		if (!isInGame())
		{
			clearNameTag();
			return;
		}
		MiniMessage mini = MiniMessage.miniMessage();

		String deathString = (isAlive()) ? "" : "<gray>☠";
		String spaceOfDeath = (isAlive()) ? "" : " ";
		String tokenColor = getTokenColor();
		String tokenCharacter = getTokenCharacter();
		String slotString =Integer.toString(getSlotIndex()+1);

		String nameString = player.getName();
		String oldNameString = "";
		String displayName = getDisplayName();
		if (displayName != null && !displayName.isEmpty())
		{
			oldNameString = "("+nameString+")";
			nameString = displayName;
		}

		List<TagResolver> resolvers = List.of(
			Placeholder.parsed("prefix_death", deathString),
			Placeholder.parsed("prefix_slot", slotString),
			Placeholder.parsed("death_space", spaceOfDeath),
			Placeholder.parsed("token_color", tokenColor),
			Placeholder.parsed("token_char", tokenCharacter),
			Placeholder.parsed("main_name", nameString),
			Placeholder.parsed("old_name", oldNameString)
		);
		player.playerListName(mini.deserialize("<<token_color>><token_char></<token_color>><gray><prefix_slot></gray> <prefix_death><death_space><main_name> <dark_gray><old_name></dark_gray>",
			TagResolver.resolver(resolvers)
		));
		PlayerDisplayTagManager.changeDisplay(player, mini.deserialize("<<token_color>><token_char></<token_color>><prefix_death> <main_name>",
			TagResolver.resolver(resolvers)
		));
	}

	String getTokenCharacter()
	{
		if (isStoryteller()) return "❇";
		if (isTraveller()) return "✳";
		return "✴";
	}

	String getTokenColor()
	{
		if (isStoryteller()) return "light_purple";
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