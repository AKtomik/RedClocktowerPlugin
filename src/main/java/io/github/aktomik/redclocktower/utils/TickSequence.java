package io.github.aktomik.redclocktower.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class TickSequence {
	private final Plugin plugin;
	private final BooleanSupplier cancelled;
	private final List<SequenceStep> steps = new ArrayList<>();

	public TickSequence(Plugin plugin, BooleanSupplier cancelled) {
		this.plugin = plugin;
		this.cancelled = cancelled;
	}

	public TickSequence then(long delayTicks, Runnable action) {
		steps.add(new SequenceStep(delayTicks, action));
		return this;
	}

	public void run() {
		runFrom(0);
	}

	private void runFrom(int index) {
		if (index >= steps.size()) return;
		if (cancelled.getAsBoolean()) return;
		SequenceStep step = steps.get(index);
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			if (cancelled.getAsBoolean()) return;
			step.action().run();
			runFrom(index + 1);
		}, step.delayTicks());
	}

	private record SequenceStep(long delayTicks, Runnable action) {}
}
