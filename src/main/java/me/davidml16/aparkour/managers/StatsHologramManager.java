package me.davidml16.aparkour.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.davidml16.aparkour.data.Parkour;
import me.filoghost.holographicdisplays.api.Position;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import me.filoghost.holographicdisplays.api.hologram.line.HologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.ItemHologramLine;
import me.filoghost.holographicdisplays.api.placeholder.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;

import me.davidml16.aparkour.Main;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	HologramLines hologramLines = new HologramLines() {
		@Override
		public @NotNull TextHologramLine appendText(@Nullable String s) {
			return null;
		}

		@Override
		public @NotNull ItemHologramLine appendItem(@Nullable ItemStack itemStack) {
			return null;
		}

		@Override
		public @NotNull TextHologramLine insertText(int i, @Nullable String s) {
			return null;
		}

		@Override
		public @NotNull ItemHologramLine insertItem(int i, @NotNull ItemStack itemStack) {
			return null;
		}

		@Override
		public @NotNull HologramLine get(int i) {
			return null;
		}

		@Override
		public void remove(int i) {

		}

		@Override
		public boolean remove(@NotNull HologramLine hologramLine) {
			return false;
		}

		@Override
		public void clear() {

		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public double getHeight() {
			return 0;
		}
	};

	HolographicDisplaysAPI api = new HolographicDisplaysAPI() {
		@Override
		public @NotNull Hologram createHologram(@NotNull Location location) {
			return null;
		}

		@Override
		public @NotNull Hologram createHologram(@NotNull Position position) {
			return null;
		}

		@Override
		public @NotNull Collection<Hologram> getHolograms() {
			return null;
		}

		@Override
		public void deleteHolograms() {

		}

		@Override
		public void registerGlobalPlaceholder(@NotNull String s, int i, @NotNull GlobalPlaceholderReplaceFunction globalPlaceholderReplaceFunction) {

		}

		@Override
		public void registerGlobalPlaceholder(@NotNull String s, @NotNull GlobalPlaceholder globalPlaceholder) {

		}

		@Override
		public void registerGlobalPlaceholderFactory(@NotNull String s, @NotNull GlobalPlaceholderFactory globalPlaceholderFactory) {

		}

		@Override
		public void registerIndividualPlaceholder(@NotNull String s, int i, @NotNull IndividualPlaceholderReplaceFunction individualPlaceholderReplaceFunction) {

		}

		@Override
		public void registerIndividualPlaceholder(@NotNull String s, @NotNull IndividualPlaceholder individualPlaceholder) {

		}

		@Override
		public void registerIndividualPlaceholderFactory(@NotNull String s, @NotNull IndividualPlaceholderFactory individualPlaceholderFactory) {

		}

		@Override
		public boolean isRegisteredPlaceholder(@NotNull String s) {
			return false;
		}

		@Override
		public @NotNull Collection<String> getRegisteredPlaceholders() {
			return null;
		}

		@Override
		public void unregisterPlaceholder(@NotNull String s) {

		}

		@Override
		public void unregisterPlaceholders() {

		}
	};

	public void loadStatsHologram(Player p, String id) {
		if (main.isHologramsEnabled()) {
			Parkour parkour = main.getParkourHandler().getParkours().get(id);
			if(parkour.getStatsHologram() != null) {
				List<String> lines = new ArrayList<>();
				lines.add(main.getLanguageHandler().getMessage("Holograms.Stats.Line1"));
				lines.add(main.getLanguageHandler().getMessage("Holograms.Stats.Line2"));

				for(int i = 0; i < lines.size(); i++) {
					lines.set(i, lines.get(i).replaceAll("%player%", main.getPlayerDataHandler().getPlayerName(p.getWorld(), p.getName()))
							.replaceAll("%time%", ColorManager.translate(main.getLanguageHandler().getMessage("Times.Loading")))
							.replaceAll("%parkour%", parkour.getName()));
				}


				Hologram hologram = api.createHologram(parkour.getStatsHologram().clone().add(0.5D, 2.0D, 0.5D));

				VisibilitySettings visibilitySettings = hologram.getVisibilitySettings();

				visibilitySettings.isVisibleTo(p);
				visibilitySettings.setGlobalVisibility(HIDDEN);


				hologramLines.insertText(0, lines.get(0));
				hologramLines.insertText(1, lines.get(1));

				main.getPlayerDataHandler().getData(p).getHolograms().put(parkour.getId(), hologram);
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
		List<String> lines = new ArrayList<String>();
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
						hologram.delete();
					}
			);
		}
	}

}
