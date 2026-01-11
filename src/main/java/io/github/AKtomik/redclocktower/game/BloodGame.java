package io.github.AKtomik.redclocktower.game;

import io.github.AKtomik.redclocktower.DataKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
	private BloodGame(World world)
	{
		this.world = world;
		this.pdc = world.getPersistentDataContainer();
	}
	public static BloodGame WorldGame(World world)
	{
		return new BloodGame(world);
	}

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
		int ordinal = pdc.getOrDefault(DataKey.GAME_PERIOD.key, PersistentDataType.INTEGER, BloodGameState.NOTHING.ordinal());
		return BloodGamePeriod.values()[ordinal];
	}

	// if
	public boolean isPlaying()
	{
		return getState() == BloodGameState.INGAME;
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

	public void broadcast(Component text)
	{
		Bukkit.getServer().broadcast(text);
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
		game.broadcast(
		Component.text("are you ready to bleed?").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
		);
	}),
	Map.entry(BloodGameAction.FINISH, (game) -> {
		game.doAction(BloodGameAction.RESET);
		game.setState(BloodGameState.ENDED);
		game.broadcast(
		Component.text("the game is over!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
		);
	}),
	Map.entry(BloodGameAction.CLEAN, (game) -> {
		game.doAction(BloodGameAction.RESET);
		game.doAction(BloodGameAction.SETOUT);
		game.setState(BloodGameState.NOTHING);
	}));

	// code/period
	static Map<BloodGamePeriod, Consumer<BloodGame>> gamePeriodEnter = Map.ofEntries(
	Map.entry(BloodGamePeriod.MORNING, (game) -> {
		Bukkit.getServer().broadcast(
		Component.text("it's the morning!").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
		);
		Bukkit.getServer().broadcast(
		Component.text("everyone to the townhall").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
		);
		game.world.setTime(0);
	}),
	Map.entry(BloodGamePeriod.FREE, (game) -> {
		Bukkit.getServer().broadcast(
		Component.text("wonder time").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
		);
		Bukkit.getServer().broadcast(
		Component.text("you are free to go").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
		);
		game.world.setTime(6000);
	}),
	Map.entry(BloodGamePeriod.MEET, (game) -> {
		Bukkit.getServer().broadcast(
		Component.text("debate time").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
		);
		Bukkit.getServer().broadcast(
		Component.text("everyone to the townhall").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
		);
		game.world.setTime(12000);
	}),
	Map.entry(BloodGamePeriod.NIGHT, (game) -> {
		Bukkit.getServer().broadcast(
		Component.text("the moon is rising...").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
		);
		Bukkit.getServer().broadcast(
		Component.text("go to your house and sleep well").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)
		);
		game.world.setTime(18000);
	})
	);
}