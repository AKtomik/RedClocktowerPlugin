package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.game.town.TownHall;
import io.github.aktomik.redclocktower.utils.renametag.PlayerRenameTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BloodPlayer {

	private final UUID uuid;
	private SeatedPlayer seated;
	private BloodGame storytelling;
	private BloodGame spectating;
	private String name;
	private BloodPlayer(OfflinePlayer offlinePlayer) {
		this.uuid = offlinePlayer.getUniqueId();
		this.name = offlinePlayer.getName();
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

	public @Nullable Player getOnlinePlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(uuid);
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
		refreshNameTag();
	}

	void detachSeat() {// only one call at SeatedPlayer
		if (seated == null) return;
		onSeatLeaved();
		seated = null;
		refreshNameTag();
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
		storytelling.getTeam().addPlayer(getOfflinePlayer());
		refreshRelatedEffects();
		refreshNameTag();
	}

	void detachStorytelling() {// only one call at BloodGame
		if (storytelling == null) return;
		// needed for invisibility view
		storytelling.getTeam().removePlayer(getOfflinePlayer());
		// then clear the pointer
		storytelling = null;
		clearRelatedEffects();
		refreshNameTag();
	}

	// to avoid player being on two spectating simultaneously
	// is used by BloodGame and should not be used elsewhere

	void attachSpectating(BloodGame game) {// only one call at BloodGame
		// do not call detachStorytelling here to avoid circular call
		if (spectating != null) spectating.removeSpectator(this);
		if (storytelling != null) storytelling.removeStoryteller(this);
		this.spectating = game;
		// needed for invisibility view
		spectating.getTeam().addPlayer(getOfflinePlayer());
		refreshRelatedEffects();
		refreshNameTag();
	}

	void detachSpectating() {// only one call at BloodGame
		if (spectating == null) return;
		// needed for invisibility view
		spectating.getTeam().removePlayer(getOfflinePlayer());
		// then clear the pointer
		spectating = null;
		clearRelatedEffects();
		refreshNameTag();
	}

	// NAME

	public void setName(String newName) {
		name = newName;
		refreshNameTag();
	}

	void onSeatLabelRefresh() {
		name = seated.getName();// sync name from seat to blood
		refreshNameTag(false);
	}

	public String getName() {
		return name;
	}

//	void loadName() {
//		return;
//	}
//
//	void saveName() {
//		return;
//	}

	void refreshNameTag() {
		refreshNameTag(true);
	}

	private void refreshNameTag(boolean seatedCascadeCall) {
		if (seatedCascadeCall && seated != null) {
			// sync name from blood to seat (and refresh seat)
			seated.setName(name);
		}

		Player player = getOnlinePlayer();
		if (player == null) return;

		Component tabName = Component.text(name).color(NamedTextColor.WHITE);
		Component headName = Component.text(name).color(NamedTextColor.WHITE);
		Color headBackColor = null;// default

		Component tabPrefixToken = Component.empty();
		int sortNumber = 0;

		// playing
		if (seated != null)
		{
			int slotIndex = seated.getSlot().getIndex();

			if (seated.getSlot().getLabelVisibility()) {
				//hide the name tag when chair label is on
				headName = Component.text(" ");
				headBackColor = Color.fromARGB(0, 0, 0, 0);
			}
			else {
				// set to the game name
				headName = seated.getTag();
			}
			tabName = tabName.color(seated.getTagColor());

			tabPrefixToken = tabPrefixToken.append(seated.getPrefixDigit());
			sortNumber = 990 - slotIndex;// I don't think we will ever have 990 players in a game
		}

		// looking
		//✳✴❇♟
		if (storytelling != null)
		{
			tabPrefixToken = tabPrefixToken.append(Component.text("✴").color(NamedTextColor.LIGHT_PURPLE));
			sortNumber = 999;
		}
		//🞉🞊👁
		if (spectating != null)
		{
			tabPrefixToken = tabPrefixToken.append(Component.text("\uD83D\uDF8A").color(NamedTextColor.GRAY));
			sortNumber = 1;
		}

		// build
		if (!Objects.equals(getName(), player.getName()))
			tabName = tabName
				.append(Component.text(" "))
				.append(Component.text(player.getName()).color(NamedTextColor.DARK_GRAY).shadowColor(ShadowColor.shadowColor(0)));
		if (tabPrefixToken != Component.empty())
			tabName = tabPrefixToken
				.append(Component.text(" "))
				.append(tabName);

		// edit
		PlayerRenameTag.changeDisplay(player, headName);
		if (headBackColor == null) PlayerRenameTag.resetDisplayBackColor(player);
		else PlayerRenameTag.setDisplayBackColor(player, headBackColor);
		player.playerListName(tabName);
		player.setPlayerListOrder(sortNumber);
	}

	// STATE & EFFECTS

	// events
	void onSeatJoined() {
		// called by attachSeat
		if (seated == null) return;
		refreshMemberEffects();
//		seated.getSlot().getGame().getTeam().addPlayer(getOfflinePlayer());
	}

	void onSeatLeaved() {
		// called by detachSeat
		if (seated == null) return;
		clearMemberEffects();
//		seated.getSlot().getGame().getTeam().removePlayer(getOfflinePlayer());
	}

	void onServerJoined() {
		// called by onJoin
		refreshNameTag();
		if (seated == null) return;
		refreshMemberEffects();
	}

	void onServerLeaved() {
		// called by onQuit
		clearMemberEffects();
		//clearNameTag();// already in PlayerNameTagEditorListener
	}

	// global effects
	void refreshMemberEffects() {
		if (seated == null) throw new RuntimeException("seated is null");
		if (getOnlinePlayer() == null) throw new RuntimeException("player is offline");
		refreshAliveEffect(seated.getAlive());
		refreshGlowerEffect(seated.isGlowing());
		refreshXpLevel(seated.getSlot().getIndex());
		refreshMemberEffects();
	}

	void clearMemberEffects() {
		refreshAliveEffect(true);
		refreshGlowerEffect(false);
		refreshXpLevel(0);
		clearRelatedEffects();
	}

	void refreshRelatedEffects() {
		BloodGame game = getRelatedGame();
		if (game == null) throw new RuntimeException("related game is null");
		if (getOnlinePlayer() == null) throw new RuntimeException("player is offline");
		setGameScoreboard(game.getScoreboard());
	}

	void clearRelatedEffects() {
		setGameScoreboard(null);
	}

	// state effects
	protected void refreshAliveEffect(boolean isAlive) {
		Player player = getOnlinePlayer();
		if (player == null) return;
		if (isAlive)
		{
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
		} else {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 9, true, false, false));
		}
	}

	protected void refreshGlowerEffect(boolean isGlower) {
		Player player = getOnlinePlayer();
		if (player == null) return;
		if (isGlower)
		{
			player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, -1, 9, true, false, false));
		} else {
			player.removePotionEffect(PotionEffectType.GLOWING);
		}
	}

	protected void refreshXpLevel(int level) {
		Player player = getOnlinePlayer();
		if (player == null) return;
		player.setExp(0);
		player.setLevel(level);
	}

	protected void setGameScoreboard(Scoreboard scoreboard) {
		Player player = getOnlinePlayer();
		if (player == null) return;
		player.setScoreboard((scoreboard == null) ? Bukkit.getScoreboardManager().getMainScoreboard() : scoreboard);
	}
}
