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

		this.circle = new SlotCircle(this);

		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		String teamId = townHall.getStringId("bloodteam");
		Team oldTeam = board.getTeam(teamId);
		if (oldTeam != null) oldTeam.unregister();
		team = board.registerNewTeam(teamId);

		setup();
	}

	private static final Map<TownHall.TownKey, BloodGame> townToGameMap = new HashMap<>();
	private static final Map<World, BloodGame> worldToGameMap = new HashMap<>();

	// sett
	static final float DEFAULT_VOLUME = .25f;
	static final float VOTE_VOLUME = .25f;
	public static final float EVENT_VOLUME = .5f;
	static final Set<Material> SENSITIVE_INFO_ITEM = Set.of(Material.PAPER, Material.WRITABLE_BOOK, Material.WRITTEN_BOOK);
	static final float CHAIR_LABEL_HEIGHT = 3f;

	static final NamedTextColor NOMINATE_GLOW_COLOR = NamedTextColor.GOLD;
	static final NamedTextColor SENTENCED_GLOW_COLOR = NamedTextColor.RED;
	static final boolean IS_ONLY_ONE_GLOW = true;
	static final boolean VOTE_BROADCAST_VOTERS = true;

	// manage
	@Nullable
	public static BloodGame get(TownHall townHall) {
		return townToGameMap.get(townHall.getKey());
	}

	@Nullable
	public static BloodGame get(World world) {
		return worldToGameMap.get(world);
	}

	public static BloodGame create(TownHall townHall) {
		BloodGame game = new BloodGame(townHall);
		townToGameMap.put(townHall.getKey(), game);
		worldToGameMap.put(townHall.getWorld(), game);
		return game;
	}

	// this will remove the game from statics references so it is not accessible anymore
	// remove it from others references too so it can be garbage collected
	public void kill() {
		// players
		circle.forEachSlots(BloodSlot::empty);
		removeAllStorytellers();
		removeAllSpectators();
		// world
		unsetup();
		team.unregister();
		// static
		townToGameMap.values().remove(this);
		worldToGameMap.values().remove(this);
		dead = true;
	}

	public static void killAll() {
		worldToGameMap.values().stream().toList().forEach(BloodGame::kill);
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

	public boolean isStarted() {
		return started;
	}

	public boolean isDead() {
		return dead;
	}

	public GamePeriod getPeriod() {
		return period;
	}

	// players participants
	// moved to SlotCircle

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
		return storytellers.stream().map(BloodPlayer::getOfflinePlayer);
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
		return spectators.stream().map(BloodPlayer::getOfflinePlayer);
	}

	public Stream<Player> getOnlineSpectators() {
		return getAllSpectators().map(OfflinePlayer::getPlayer).filter(Objects::nonNull);
	}

	// all participants
	public Stream<Player> getAllOnline() {
		return Stream.concat(Stream.concat(circle.getOnlinePlayers(), getOnlineStorytellers()), getOnlineSpectators());
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
		circle.hiddeLabels();
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
		circle.getAllSeated().forEach(seated -> seated.setAlive(true));
		circle.getAllSeated().forEach(seated -> seated.setVotePull(false));
		circle.unlockAll();
		circle.showLabels();
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

	// time
	public void switchPeriod(GamePeriod period, CommandSender sender) {
		GameAction.periodEnter.get(period).accept(this, sender);
		this.period = period;
	}
}
