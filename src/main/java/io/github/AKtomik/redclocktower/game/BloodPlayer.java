package io.github.AKtomik.redclocktower.game;

import io.github.AKtomik.redclocktower.DataKey;
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
	final public Player player;
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
	private void setAlive(boolean isAlive)
	{
		pdc.set(DataKey.PLAYER_ALIVE.key, PersistentDataType.BOOLEAN, isAlive);
	}
	public boolean getAlive()
	{
		return pdc.getOrDefault(DataKey.PLAYER_ALIVE.key, PersistentDataType.BOOLEAN, true);
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

	// game link
	void joinGame(BloodGame game)
	{
		BloodGame lastGame = getGame();
		if (lastGame != null) leaveGame();
		setGame(game);

		game.getTeam().addPlayer(player);
		revive();
		refreshNameTag();
	}

	// is call by BloodGame to apply the game join player side (step 2/2)
	void quitGame(BloodGame game)
	{
		clearGame();

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
		return true;
	}

	// action
	public void kill()
	{
		setAlive(false);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 9, true));
		refreshNameTag();
	}
	public void revive()
	{
		setAlive(true);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		refreshNameTag();
	}

	public void clearNameTag()
	{
		player.playerListName(Component.text(player.getName()));
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
		if (isStoryteller())
		{
			prefixString = "<light_purple>❇</light_purple> ";
		} else {
			String lifeString = (isAlive()) ? "<white>" : "<gray>☠";
			String tokenString = (hasToken()) ? isAlive() ? "<red>✴</red>" : "<blue>✴</blue>" : isAlive() ? "<gray>✳</gray>" : "<black>✳</black>";
			//String voteString = (isVoting()) ? "<gray>✴</gray>" : "<black>✳<black>";
			prefixString = tokenString+lifeString+" ";
		}
		String nameString = player.getName();
		player.playerListName(mini.deserialize(prefixString+nameString));
	}
}