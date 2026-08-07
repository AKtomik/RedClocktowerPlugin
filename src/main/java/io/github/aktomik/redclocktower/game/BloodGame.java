package io.github.aktomik.redclocktower.game;

import io.github.aktomik.redclocktower.game.town.TownHall;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class BloodGame {
	private static final MiniMessage mini = MiniMessage.miniMessage();

	// definition
	private final TownHall townHall;
	private final SlotCircle circle;
	private final List<BloodPlayer> storytellers = new ArrayList<>();
	private final List<BloodPlayer> spectators = new ArrayList<>();

	private boolean started = false;
	private boolean dead = false;
	private GamePeriod period = GamePeriod.FREE;

	private final World world;// equal to townhall world
	private final Team team;

	private BloodGame(TownHall townHall) {
		this.townHall = townHall;
		this.world = townHall.getWorld();

		this.circle = new SlotCircle(this, townHall);

		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		String teamId = "bloodteam-"+world+"-"+townHall.getTownName();
		Team oldTeam = board.getTeam(teamId);
		if (oldTeam != null) oldTeam.unregister();
		team = board.registerNewTeam(teamId);

		setup();
	}

	private static final Map<Integer, BloodGame> townToGameMap = new HashMap<>();
	private static final Map<World, BloodGame> worldToGameMap = new HashMap<>();

	// sett
	static final NamedTextColor NOMINATE_TEAM_COLOR = NamedTextColor.GOLD;
	static final NamedTextColor PYLORI_TEAM_COLOR = NamedTextColor.RED;
	static final float DEFAULT_VOLUME = .25f;
	static final float VOTE_VOLUME = .25f;
	static final float EVENT_VOLUME = .5f;
	static final Set<Material> SENSITIVE_INFO_ITEM = Set.of(Material.PAPER, Material.WRITABLE_BOOK, Material.WRITTEN_BOOK);

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
		// world
		unsetup();
		team.unregister();
		// players
		circle.forEachSlots(BloodSlot::empty);
		removeAllStorytellers();
		removeAllSpectators();
		// static
		townToGameMap.values().remove(this);
		worldToGameMap.values().remove(this);
		dead = true;
	}

	// getters
	public TownHall getTownHall() {
		return townHall;
	}

	public World getWorld() {
		return world;
	}

	public Team getTeam() {
		return team;
	}

	public SlotCircle getCircle() {
		return circle;
	}

	public MiniMessage getMini() {
		return mini;
	}

	// players participants
	public Stream<Seated> getAllSeated() {
		return circle.getSlotsStream().filter(BloodSlot::isOccupied).map(BloodSlot::getSeated);
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

	public Stream<OfflinePlayer> getAllStorytellers() {
		return storytellers.stream().map(BloodPlayer::getOffPlayer);
	}

	public Stream<Player> getOnlineStorytellers() {
		return getAllStorytellers().map(OfflinePlayer::getPlayer).filter(Objects::nonNull);
	}

	// spectators participants
	public void addSpectator(BloodPlayer bloodPlayer) {
		bloodPlayer.attachSpectating(this);
		spectators.add(bloodPlayer);
	}

	public void removeSpectator(BloodPlayer bloodPlayer) {
		bloodPlayer.detachSpectating();
		spectators.remove(bloodPlayer);
	}

	public void removeAllSpectators() {
		 for (BloodPlayer bloodPlayer : spectators) bloodPlayer.detachSpectating();
		spectators.clear();
	}

	public Stream<OfflinePlayer> getAllSpectators() {
		return spectators.stream().map(BloodPlayer::getOffPlayer);
	}

	public Stream<Player> getOnlineSpectators() {
		return getAllSpectators().map(OfflinePlayer::getPlayer).filter(Objects::nonNull);
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
	private void setup() {
		// game
		circle.lockAll();
		// world
		world.setTime(10000);
		world.setGameRule(GameRules.ADVANCE_TIME, false);
		world.setGameRule(GameRules.KEEP_INVENTORY, true);
		world.setDifficulty(Difficulty.PEACEFUL);
		// team
		team.color(NamedTextColor.AQUA);
		team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
		team.setCanSeeFriendlyInvisibles(true);
	}

	public void resetup() {
		setup();
	}

	private void unsetup() {
		world.setGameRule(GameRules.ADVANCE_TIME, true);
		// we could saved the old world rules to set back but can be confusing
	}

	public void start() {
		// game
		getAllSeated().forEach(seated -> seated.setAlive(true));
		getAllSeated().forEach(seated -> seated.setVotePull(false));
		circle.unlockAll();
		period = GamePeriod.MEET;
		// world
		world.setTime(12000);
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

	// time
	public void switchPeriod(GamePeriod period, CommandSender sender) {
		GameAction.periodEnter.get(period).accept(this, sender);
		this.period = period;
	}

	public GamePeriod getPeriod() {
		return period;
	}
}
