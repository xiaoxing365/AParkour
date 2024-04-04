package me.davidml16.aparkour.managers;

import me.davidml16.aparkour.APIImplementer.HDAPIImplementer;
import me.davidml16.aparkour.APIImplementer.HDImplementer;
import me.davidml16.aparkour.APIImplementer.HDLineImplementer;
import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.Position;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import me.filoghost.holographicdisplays.api.hologram.line.HologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.ItemHologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import me.filoghost.holographicdisplays.api.placeholder.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static me.filoghost.holographicdisplays.api.hologram.VisibilitySettings.Visibility.HIDDEN;

public class StatsHologramManager {

	Main main = (Main) Main.getProvidingPlugin(Main.class);

	public void loadStatsHolograms(Player p) {
		if (main.isHologramsEnabled()) {
			for (String parkour : main.getParkourHandler().getParkours().keySet()) {
				loadStatsHologram(p, parkour);
			}
		}
	}

	HologramLines hologramLines = new HDLineImplementer();

	HolographicDisplaysAPI api = new HDAPIImplementer();
	Hologram HD = new HDImplementer();

	public void loadStatsHologram(Player p, String id) {
		if (main.isHologramsEnabled()) {
			Parkour parkour = main.getParkourHandler().getParkours().get(id);
			if(Parkour.getStatsHologram() != null) {
				List<String> lines = new ArrayList<>();
				lines.add(main.getLanguageHandler().getMessage("Holograms.Stats.Line1"));
				lines.add(main.getLanguageHandler().getMessage("Holograms.Stats.Line2"));

				for(int i = 0; i < lines.size(); i++) {
					lines.set(i, lines.get(i).replaceAll("%player%", main.getPlayerDataHandler().getPlayerName(p.getWorld(), p.getName()))
							.replaceAll("%time%", ColorManager.translate(main.getLanguageHandler().getMessage("Times.Loading")))
							.replaceAll("%parkour%", parkour.getName()));
				}


				HD = api.createHologram(parkour.getStatsHologram().clone().add(0.5D, 2.0D, 0.5D));

				VisibilitySettings visibilitySettings = HD.getVisibilitySettings();

				visibilitySettings.isVisibleTo(p);
				visibilitySettings.setGlobalVisibility(HIDDEN);


				hologramLines.insertText(0, lines.get(0));
				hologramLines.insertText(1, lines.get(1));

				main.getPlayerDataHandler().getData(p).getHolograms().put(parkour.getId(), HD);
			}
		}
	}
	
	public void reloadStatsHolograms(Player p) {
		if (main.isHologramsEnabled()) {
			for (String parkour : main.getParkourHandler().getParkours().keySet()) {
				reloadStatsHologram(p, parkour);
			}
		}
	}

	public void reloadStatsHologram(Player p, String id) {
		if (main.isHologramsEnabled()) {
			Parkour parkour = main.getParkourHandler().getParkours().get(id);

			if(!haveParkourData(p, id)) return;

			if(main.getPlayerDataHandler().getData(p).getHolograms().containsKey(parkour.getId())) {
				Hologram hologram = main.getPlayerDataHandler().getData(p).getHolograms().get(parkour.getId());

				long bestTime = main.getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId());

				List<String> lines = getLines(parkour, p, bestTime);

				hologramLines.insertText(0,lines.get(0));
				hologramLines.insertText(1,lines.get(1));
			}
		}
	}

	public boolean haveParkourData(Player p, String id) {
		Parkour parkour = main.getParkourHandler().getParkours().get(id);

		if(p == null || !p.isOnline()) return false;
		if(parkour == null) return false;
		if(!main.getPlayerDataHandler().getData(p).getBestTimes().containsKey(parkour.getId())) return false;

		return true;
	}
	
	public List<String> getLines(Parkour parkour, Player p, long bestTime) {
		List<String> lines = new ArrayList<>();
		String NoBestTime = main.getLanguageHandler().getMessage("Times.NoBestTime");
		lines.add(main.getLanguageHandler().getMessage("Holograms.Stats.Line1"));
		lines.add(main.getLanguageHandler().getMessage("Holograms.Stats.Line2"));
		
		if (bestTime != 0) {
			for(int i = 0; i < lines.size(); i++) {
				lines.set(i, lines.get(i).replaceAll("%player%", main.getPlayerDataHandler().getPlayerName(p.getWorld(), p.getName()))
						.replaceAll("%time%", main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.ParkourTimer"),bestTime))
						.replaceAll("%parkour%", parkour.getName()));
			}
		} else {
			for(int i = 0; i < lines.size(); i++) {
				lines.set(i, lines.get(i).replaceAll("%player%", main.getPlayerDataHandler().getPlayerName(p.getWorld(), p.getName()))
						.replaceAll("%time%", NoBestTime)
						.replaceAll("%parkour%", parkour.getName()));
			}
		}

		return lines;
	}
	
	public void removeStatsHolograms(Player p) {
		if (main.isHologramsEnabled()) {
			for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
				if(parkour.getStatsHologram() != null) {
					removeStatsHologram(p, parkour.getId());
				}
			}
			main.getPlayerDataHandler().getData(p).getHolograms().clear();
		}
	}
	
	public void removeStatsHologram(Player p, String id) {
		if (main.isHologramsEnabled()) {
			if(main.getPlayerDataHandler().getData(p).getHolograms().containsKey(id)) {
				main.getPlayerDataHandler().getData(p).getHolograms().get(id).delete();
				main.getPlayerDataHandler().getData(p).getHolograms().remove(id);
			}
		}
	}

	public void reloadStatsHolograms() {
		if (main.isHologramsEnabled()) {
			HolographicDisplaysAPI.get(main).getHolograms().forEach(
					hologram -> {
						HD.delete();
					}
			);
		}
	}

}
