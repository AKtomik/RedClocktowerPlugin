package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.DataKey;
import io.github.aktomik.redclocktower.utils.PlayerDisplayTagManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BloodPlayer {

	// class
	public final Player player;
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
	public void clearAlive()
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
	public void clearVoteToken()
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
	public void clearVotePull()
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
		clearGame();
		clearAlive();
		clearVotePull();
		clearVoteToken();

		game.getTeam().removePlayer(player);
		revive();
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
	public boolean hasToken()
	{
		return getVoteToken();
	}
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
	}

	public void changeVotePull(boolean value)
	{
		setVotePull(value);
		refreshNameTag();
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
		String prefixString;
		String slotString = "";
		if (isStoryteller())
		{
			prefixString = "<light_purple>❇</light_purple> ";
		} else {
			String lifeString = (isAlive()) ? "<white>" : "<gray>☠";
			String tokenString = getStringToken();
			prefixString = tokenString+lifeString+" ";
			slotString = "<gray>"+Integer.toString(getSlotIndex()+1)+"</gray> ";
		}
		String longeNameString = player.getName();
		String simpleNameString = player.getName();
		String displayName = getDisplayName();
		if (displayName != null && !displayName.isEmpty())
		{
			longeNameString = displayName+" <dark_gray>("+ longeNameString +")";
			simpleNameString = displayName;
		}
		player.playerListName(mini.deserialize(slotString+prefixString+longeNameString));
		PlayerDisplayTagManager.changeDisplay(player, mini.deserialize(prefixString+simpleNameString));
	}

	String getStringToken()
	{
		return hasToken()
		? isAlive()
			? isVoting()
				? "<yellow>✴</yellow>"
				: "<red>✴</red>"
			: isVoting()
				? "<aqua>✴</aqua>"
				: "<blue>✴</blue>"
		: isAlive()
			? isVoting()
				? "<yellow>✳</yellow>"
				: "<gray>✳</gray>"
			: "<black>✳</black>";
	}
}