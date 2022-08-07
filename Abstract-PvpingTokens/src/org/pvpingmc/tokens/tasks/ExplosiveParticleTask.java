package org.pvpingmc.tokens.tasks;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.pvpingmc.tokens.Main;

import ninja.coelho.dimm.DIMMPlugin;

public class ExplosiveParticleTask {

	private static final long TICK_RATE = 6L;
	private static final int RATE_LIMIT = 1000;

	private final Queue<Runnable> particleQueue;
	private final ReentrantLock queueLock;
	private BukkitTask impl;

	public ExplosiveParticleTask() {
		this.particleQueue = new ConcurrentLinkedQueue();
		this.queueLock = new ReentrantLock();
	}

	public void start() {
		if (this.impl == null) {
			DIMMPlugin dimms = Main.getInstance().getDimmPlugin();
			this.impl = dimms.getBukkit().runAsyncTimer(this::tickWithLock, TICK_RATE);
		}
	}

	public void stop() {
		this.particleQueue.clear();
		this.queueLock.unlock();

		if (this.impl != null) {
			this.impl.cancel();
			this.impl = null;
		}
	}

	public void queue(Location location, Player... playerArr) {
		if (this.particleQueue.size() > RATE_LIMIT) {
			// rate limit?
			return;
		}
		this.particleQueue.offer(() -> {
			for (Player player : playerArr) {
				if (!player.isOnline()) continue;

				Main.getInstance().getBreakHandler().sendExplosionPacket(player, location);
			}
		});
	}

	public void tickWithLock() {
		if (this.queueLock.tryLock()) {
			try {
				tick();
			} finally {
				this.queueLock.unlock();
			}
		}
	}

	public void tick() {
		if (this.particleQueue.isEmpty()) return;

		Runnable toRun = null;
		while ((toRun = this.particleQueue.poll()) != null) toRun.run();
	}
}
