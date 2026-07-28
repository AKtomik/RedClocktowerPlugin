package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.game.town.TownHall;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class BloodGame {
	private static final MiniMessage mini = MiniMessage.miniMessage();

	// definition
	private final TownHall townHall;
	private final BloodSlot[] slots;
	private final ArrayList<BloodPlayer> storytellers = new ArrayList<>();
	private final ArrayList<BloodPlayer> spectators = new ArrayList<>();
	private boolean started = false;
	private boolean dead = false;
	private BloodGame(TownHall townHall) {
		this.townHall = townHall;
		slots = townHall.getAllChairs().map(BloodSlot::new).toArray(BloodSlot[]::new);
	}

	private static final Map<Integer, BloodGame> townToGameMap = new HashMap<>();
	private static final Map<World, BloodGame> worldToGameMap = new HashMap<>();

	// sett
	static final NamedTextColor NOMINATE_TEAM_COLOR = NamedTextColor.GOLD;
	static final NamedTextColor PYLORI_TEAM_COLOR = NamedTextColor.RED;
	static final float DEFAULT_VOLUME = .25f;
	static final float VOTE_VOLUME = .25f;
	static final float EVENT_VOLUME = .5f;

	// manage
	@Nullable
	public static BloodGame get(TownHall townHall) {
		return townToGameMap.get(townHall.getHash());
	}

	@Nullable
	public static BloodGame get(World world) {
		return worldToGameMap.get(world);
	}

	public static BloodGame create(TownHall townHall) {
		BloodGame game = new BloodGame(townHall);
		townToGameMap.put(townHall.getHash(), game);
		worldToGameMap.put(townHall.getWorld(), game);
		return game;
	}

	// this will remove the game from statics references so it is not accessible anymore
	// remove it from others references too so it can be garbage collected
	public void kill() {
		// reset
		for (BloodSlot slot : slots) slot.empty();
		removeAllStorytellers();
		removeAllSpectators();
		// static
		townToGameMap.values().remove(this);
		worldToGameMap.values().remove(this);
		dead = true;
	}

	// townhall
	public TownHall getTownHall() {
		return townHall;
	}

	// slot
	public Integer getSlotCount()
	{
		return slots.length;
	}

	public boolean isValidSlot(int index)
	{
		return index >= 0 && index < slots.length;
	}

	public BloodSlot getSlot(int index)
	{
		return slots[index];
	}

	// players participants
	public Stream<Seated> getAllSeated() {
		return Arrays.stream(slots).filter(BloodSlot::isOccupied).map(BloodSlot::getSeated);
	}

	public Stream<SeatedPlayer> getAllSeatedPlayers() {
		return getAllSeated().filter(SeatedPlayer.class::isInstance).map(SeatedPlayer.class::cast);
	}

	public Stream<OfflinePlayer> getOfflinePlayers() {
		return getAllSeatedPlayers().map(SeatedPlayer::getOffPlayer);
	}

	public Stream<Player> getOnlinePlayers() {
		return getOfflinePlayers().map(OfflinePlayer::getPlayer).filter(Objects::nonNull);
	}

	// storytellers participants
	public void addStoryteller(BloodPlayer bloodPlayer) {
		bloodPlayer.attachStorytelling(this);
		storytellers.add(bloodPlayer);
	}

	public void removeStoryteller(BloodPlayer bloodPlayer) {
		bloodPlayer.detachStorytelling();
		storytellers.remove(bloodPlayer);
	}

	public void removeAllStorytellers() {
		for (BloodPlayer bloodPlayer : storytellers) bloodPlayer.detachStorytelling();
		storytellers.clear();
	}

	public Stream<Player> getOnlineStorytellers() {
		return storytellers.stream().map(BloodPlayer::getOffPlayer).map(OfflinePlayer::getPlayer).filter(Objects::nonNull);
	}

	// spectators participants
	public void addSpectator(BloodPlayer bloodPlayer) {
		spectators.add(bloodPlayer);
	}

	public void removeSpectator(BloodPlayer bloodPlayer) {
		spectators.remove(bloodPlayer);
	}

	public void removeAllSpectators() {
		// for (BloodPlayer bloodPlayer : spectators) bloodPlayer.();
		spectators.clear();
	}

	public Stream<Player> getOnlineSpectators() {
		return spectators.stream().map(BloodPlayer::getOffPlayer).map(OfflinePlayer::getPlayer).filter(Objects::nonNull);
	}

	// all participants
	public Stream<Player> getAllOnline() {
		return Stream.concat(Stream.concat(getOnlinePlayers(), getOnlineStorytellers()), getOnlineSpectators());
	}

	// text utils
	public void sendStorytellers(Component message) {
		for (Player player : getOnlineStorytellers().toList())
			player.sendMessage(message);
	}

	void sendAll(Component message) {
		for (Player player : getAllOnline().toList())
			player.sendMessage(message);
	}

	public void broadcast(String richString) {
		sendAll(mini.deserialize(richString));
	}

	public void broadcast(String richString, final TagResolver... tagResolvers) {
		sendAll(mini.deserialize(richString, tagResolvers));
	}

	// sound utils
	public void pingSound(Sound sound, float volume, float pitch)
	{
		for (Player player : townHall.getWorld().getPlayers()) {
			Location loc = Objects.requireNonNull(player.getLocation());
			player.playSound(loc, sound, SoundCategory.MASTER, volume, pitch);
		}
	}

	public void pingSound(Sound sound, float pitch)
	{
		pingSound(sound, DEFAULT_VOLUME, pitch);
	}

	public void pingSound(Sound sound)
	{
		pingSound(sound,1f);
	}


	// state
	public void start() {
		// action
		getAllSeated().forEach(seated -> seated.setAlive(true));
		// message
		broadcast("<red><b>are you ready to bleed?");
		pingSound(Sound.ENTITY_ARROW_HIT_PLAYER, 2f);
		// state
		this.started = true;
	}

	public void finish(GameTeam winTeam) {
		// message
		switch (winTeam) {
			case GOOD -> {
				broadcast("<aqua><b>good win!");
				pingSound(Sound.ENTITY_ALLAY_HURT, 2f);
			}
			case BAD -> {
				broadcast("<red><b>bad win!");
				pingSound(Sound.ENTITY_ALLAY_HURT, 0f);
			}
			case NEUTRAL -> {
				broadcast("<gray><b>no one won");
				pingSound(Sound.ENTITY_ALLAY_HURT, 1f);
			}
		}
		// state
		this.started = false;
	}

	public boolean isStarted() {
		return started;
	}

	public boolean isDead() {
		return dead;
	}

	public MiniMessage getMini() {
		return mini;
	}
}
