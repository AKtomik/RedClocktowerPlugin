package io.github.AKtomik.redclocktower.game;

import io.github.AKtomik.redclocktower.DataKey;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.function.Consumer;

public class BloodGame {

	// class
	final public World world;
	private final PersistentDataContainer pdc;
	private String roundId;
	private BloodGame(World world)
	{
		this.world = world;
		this.pdc = world.getPersistentDataContainer();
	}
	public static BloodGame WorldGame(World world)
	{
		return new BloodGame(world);
	}
	public static MiniMessage mini = MiniMessage.miniMessage();

	// get & set
	public void setState(BloodGameState gameState)
	{
		pdc.set(DataKey.GAME_STATE.key, PersistentDataType.INTEGER, gameState.ordinal());
	}
	public BloodGameState getState()
	{
		int ordinal = pdc.getOrDefault(DataKey.GAME_STATE.key, PersistentDataType.INTEGER, BloodGameState.NOTHING.ordinal());
		return BloodGameState.values()[ordinal];
	}

	private void setTime(BloodGamePeriod gameState)
	{
		pdc.set(DataKey.GAME_PERIOD.key, PersistentDataType.INTEGER, gameState.ordinal());
	}
	public BloodGamePeriod getTime()
	{
		int ordinal = pdc.getOrDefault(DataKey.GAME_PERIOD.key, PersistentDataType.INTEGER, BloodGamePeriod.FREE.ordinal());
		return BloodGamePeriod.values()[ordinal];
	}

	private void setRoundCount(int count)
	{
		pdc.set(DataKey.GAME_ROUND_COUNT.key, PersistentDataType.INTEGER, count);
	}
	public int getRoundCount()
	{
		return pdc.getOrDefault(DataKey.GAME_ROUND_COUNT.key, PersistentDataType.INTEGER, 0);
	}

	private void setRoundId(String newRondId)
	{
		roundId = newRondId;
	}
	private String getRoundId()
	{
		return roundId;
	}

	// if
	public boolean isPlaying()
	{
		return getState() == BloodGameState.INGAME;
	}

	// actions
	private void generateNewId()
	{
		int incrementedRoundCount = getRoundCount() + 1;
		setRoundCount(incrementedRoundCount);
		roundId =  "%s:%s".formatted(world.getName(), Integer.toString(incrementedRoundCount));
	}

	// runs
	public void doAction(BloodGameAction action)
	{
		gameAction.get(action).accept(this);
	}
	public void switchTime(BloodGamePeriod period)
	{
		gamePeriodEnter.get(period).accept(this);
		setTime(period);
	}

	public void broadcast(String richString)
	{
		Bukkit.getServer().broadcast(mini.deserialize(richString));
	}

	// code/action
	static Map<BloodGameAction, Consumer<BloodGame>> gameAction = Map.ofEntries(
	Map.entry(BloodGameAction.RESET, (game) -> {
		game.world.setTime(12000);
	}),
	Map.entry(BloodGameAction.SETUP, (game) -> {
		game.world.setGameRule(GameRules.ADVANCE_TIME, false);
	}),
	Map.entry(BloodGameAction.SETOUT, (game) -> {
		game.world.setGameRule(GameRules.ADVANCE_TIME, true);
	}),
	Map.entry(BloodGameAction.START, (game) -> {
		game.doAction(BloodGameAction.RESET);
		game.doAction(BloodGameAction.SETUP);
		game.setState(BloodGameState.INGAME);
		game.generateNewId();
		game.broadcast("<red><b>are you ready to bleed?");
	}),
	Map.entry(BloodGameAction.FINISH, (game) -> {
		game.doAction(BloodGameAction.RESET);
		game.setState(BloodGameState.ENDED);
		game.broadcast("<red><b>the game is over!");
	}),
	Map.entry(BloodGameAction.CLEAN, (game) -> {
		game.doAction(BloodGameAction.RESET);
		game.doAction(BloodGameAction.SETOUT);
		game.setState(BloodGameState.NOTHING);
	}));

	// code/period
	static Map<BloodGamePeriod, Consumer<BloodGame>> gamePeriodEnter = Map.ofEntries(
	Map.entry(BloodGamePeriod.MORNING, (game) -> {
		game.broadcast("<white><b>it's the morning!");
		game.broadcast("<gray><i>it's the morning!");
		game.world.setTime(0);
	}),
	Map.entry(BloodGamePeriod.FREE, (game) -> {
		game.broadcast("<white><b>wonder time");
		game.broadcast("<gray><i>you are free to go and talk");
		game.world.setTime(6000);
	}),
	Map.entry(BloodGamePeriod.MEET, (game) -> {
		game.broadcast("<white><b>debate time");
		game.broadcast("<gray><i>everyone is attended to the townhall");
		game.world.setTime(12000);
	}),
	Map.entry(BloodGamePeriod.NIGHT, (game) -> {
		game.broadcast("<white><b>the moon is rising...");
		game.broadcast("<gray><i>go to your house and sleep well");
		game.world.setTime(18000);
	})
	);
}