package io.github.AKtomik.redclocktower.game;

import io.github.AKtomik.redclocktower.DataKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

	// game link
	void joinGame(BloodGame game)
	{
		game.getTeam().addPlayer(player);
		refreshNameTag();
	}

	void quitGame(BloodGame game)
	{
		game.getTeam().removePlayer(player);
		clearNameTag();
	}

	// if
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
		player.playerListName();
	}

	public void refreshNameTag()
	{
//		MiniMessage mini = MiniMessage.miniMessage();
		String prefixString;
		if (isStoryteller())
		{
			prefixString = "<light_purple>❇</light_purple> ";
		} else {
			String lifeString = (isAlive()) ? "<white>" : "<gray>☠";
			String tokenString = (hasToken()) ? "<red>✴</red>" : "<black>✳<black>";
			//String voteString = (isVoting()) ? "<gray>✴</gray>" : "<black>✳<black>";
			prefixString = lifeString+tokenString+" ";
		}
		String nameString = player.getName();
		player.playerListName(Component.text(prefixString+nameString));
	}
}
