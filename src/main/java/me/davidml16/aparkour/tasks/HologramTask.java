package me.davidml16.aparkour.tasks;

import me.davidml16.aparkour.Main;
import org.bukkit.Bukkit;

public class HologramTask {
	
	private int id;

	private Main main = new Main();

	class Task implements Runnable {
		@Override
		public void run() {
			main.getTopHologramManager().reloadTopHolograms();
		}
	}
	
	public int getId() { return id; }

	@SuppressWarnings("deprecation")
	public void start() {
		id = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(main, new Task(), 20L, 20);
	}
	
	public void stop() {
		Bukkit.getServer().getScheduler().cancelTask(id);
	}
	
}
