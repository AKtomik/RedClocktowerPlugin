package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.game.town.TownHall;
import io.github.aktomik.redclocktower.utils.PlayerNameTagEditor;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BloodPlayer {

	private final OfflinePlayer offPlayer;
	private SeatedPlayer seated;
	private BloodGame storytelling;
	private BloodGame spectating;
	@Nullable private String customName;
	private BloodPlayer(OfflinePlayer offlinePlayer) {
		this.offPlayer = offlinePlayer;
		refreshNameTag();
	}

	// avoid creating more than one BloodPlayer object by player (fixes)
	private static final Map<OfflinePlayer, BloodPlayer> bloodPlayerCreatedObjects = new HashMap<>();

	@NullMarked
	public static BloodPlayer get(OfflinePlayer offlinePlayer) {
		// find
		BloodPlayer playerFound = bloodPlayerCreatedObjects.get(offlinePlayer);
		if (playerFound != null) return playerFound;

		// create
		BloodPlayer playerCreated = new BloodPlayer(offlinePlayer);
		bloodPlayerCreatedObjects.put(offlinePlayer, playerCreated);
		return playerCreated;
	}

	// ACCESS

	public OfflinePlayer getOffPlayer() {
		return offPlayer;
	}

	@Nullable
	public SeatedPlayer getSeated() {
		return seated;
	}

	@Nullable
	public BloodGame getSeatedGame() {
		if (seated == null) return null;
		return seated.getSlot().getGame();
	}

	@Nullable
	public TownHall getSeatedTownHall() {
		if (seated == null) return null;
		return seated.getSlot().getGame().getTownHall();
	}

	@Nullable
	public BloodGame getStorytellingGame() {
		return storytelling;
	}

	@Nullable
	public BloodGame getSpectatingGame() {
		return spectating;
	}

	@Nullable
	public BloodGame getRelatedGame() {
		if (seated != null) return getSeatedGame();
		if (storytelling != null) return storytelling;
		if (spectating != null) return spectating;
		return null;
	}

	// LINKS

	// to avoid player being on two seats simultaneously
	// is used by SeatedPlayer and should not be used elsewhere

	void attachSeat(SeatedPlayer seated) {// only one call at SeatedPlayer
		// do not call detachSeat here to avoid circular call
		if (this.seated != null) this.seated.getSlot().empty();
		this.seated = seated;
		onSeatJoined();
	}

	void detachSeat() {// only one call at SeatedPlayer
		if (seated == null) return;
		onSeatLeaved();
		seated = null;
	}

	// to avoid player being on two storytelling simultaneously
	// is used by BloodGame and should not be used elsewhere
	// it is technically tolerated to be storytelling and playing a game

	void attachStorytelling(BloodGame game) {// only one call at BloodGame
		// do not call detachStorytelling here to avoid circular call
		if (storytelling != null) storytelling.removeStoryteller(this);
		if (spectating != null) spectating.removeSpectator(this);
		this.storytelling = game;
		// needed for invisibility view
		storytelling.getTeam().addPlayer(offPlayer);
	}

	void detachStorytelling() {// only one call at BloodGame
		if (storytelling == null) return;
		// needed for invisibility view
		storytelling.getTeam().removePlayer(offPlayer);
		// then clear the pointer
		storytelling = null;
	}

	// to avoid player being on two spectating simultaneously
	// is used by BloodGame and should not be used elsewhere

	void attachSpectating(BloodGame game) {// only one call at BloodGame
		// do not call detachStorytelling here to avoid circular call
		if (spectating != null) spectating.removeSpectator(this);
		if (storytelling != null) storytelling.removeStoryteller(this);
		this.spectating = game;
		// needed for invisibility view
		spectating.getTeam().addPlayer(offPlayer);
	}

	void detachSpectating() {// only one call at BloodGame
		if (spectating == null) return;
		// needed for invisibility view
		spectating.getTeam().removePlayer(offPlayer);
		// then clear the pointer
		spectating = null;
	}

	// NAME

	public String displayName() {
		return (customName != null) ? customName : offPlayer.getName();
	}

	public void setCustomName(String newName) {
		customName = newName;
		refreshNameTag();
	}

	@Nullable
	public String getCustomName() {
		return customName;
	}

//	void loadDisplayName() {
//		return;
//	}
//
//	void saveDisplayName() {
//		return;
//	}

	void refreshNameTag() {
		if (seated != null) seated.setName(displayName());

		if (!(offPlayer instanceof Player player)) return;
		player.playerListName(Component.text(displayName()));
		if (seated == null)
			PlayerNameTagEditor.clearDisplay(player);
		else
			PlayerNameTagEditor.changeDisplay(player, Component.text(displayName()));
	}

	void clearNameTag() {
		if (!(offPlayer instanceof Player player)) return;
		PlayerNameTagEditor.clearDisplay(player);
		player.playerListName(Component.text(player.getName()));
	}

	// STATE & EFFECTS

	// events
	void onSeatJoined() {
		// called by attachSeat
		if (seated == null) return;
		refreshAllEffects();
		refreshNameTag();
		seated.getSlot().getGame().getTeam().addPlayer(offPlayer);
	}

	void onSeatLeaved() {
		// called by detachSeat
		if (seated == null) return;
		clearAllEffects();
		refreshNameTag();
		seated.getSlot().getGame().getTeam().removePlayer(offPlayer);
	}

	void onServerJoined() {
		// called by onJoin
		refreshNameTag();
		if (seated == null) return;
		refreshAllEffects();
	}

	void onServerLeaved() {
		// called by onQuit
		clearNameTag();
		clearAllEffects();
	}

	// global effects
	void refreshAllEffects() {
		refreshAliveEffect(seated.getAlive());
	}

	void clearAllEffects() {
		clearAliveEffect();
	}

	// state effects
	protected void clearAliveEffect() {
		if (!(offPlayer instanceof Player player)) return;
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
	}

	protected void refreshAliveEffect(boolean alive) {
		if (!(offPlayer instanceof Player player)) return;
		if (alive)
		{
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
		} else {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 9, true, false, false));
		}
	}
}
