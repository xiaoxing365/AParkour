package me.davidml16.aparkour.managers;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.LeaderboardEntry;
import me.davidml16.aparkour.data.Parkour;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.Position;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import me.filoghost.holographicdisplays.api.hologram.line.HologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.ItemHologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import me.filoghost.holographicdisplays.api.placeholder.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

public class TopHologramManager {

    private HashMap<String, Hologram> holoHeader;
    private HashMap<String, Hologram> holoBody;
    private HashMap<String, TextHologramLine> holoFoot;

    private HashMap<String,Hologram> holoFooter;

    private int timeLeft;
    private int reloadInterval;

    private Main main;

    public TopHologramManager(Main main, int reloadInterval) {
        this.main = main;
        this.reloadInterval = reloadInterval;
        this.holoHeader = new HashMap<String, Hologram>();
        this.holoBody = new HashMap<String, Hologram>();
        this.holoFooter = new HashMap<String, Hologram>();
    }

    public HashMap<String, Hologram> getHoloHeader() {
        return holoHeader;
    }

    public HashMap<String, Hologram> getHoloBody() {
        return holoBody;
    }

    public HashMap<String, TextHologramLine> getHoloFoot() {
        return holoFoot;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setReloadInterval(int reloadInterval) {
        this.reloadInterval = reloadInterval;
    }

    public void restartTimeLeft() {
        this.timeLeft = reloadInterval;
    }

    public void loadTopHolograms() {
        holoBody.clear();
        holoHeader.clear();
        holoFooter.clear();
        if (main.isHologramsEnabled()) {
            for (String parkour : main.getParkourHandler().getParkours().keySet()) {
                loadTopHologram(parkour);
            }
        }
    }

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

    public void loadTopHologram(String id) {
        if (main.isHologramsEnabled()) {
            Parkour parkour = main.getParkourHandler().getParkours().get(id);

            main.getDatabaseHandler().getParkourBestTimes(parkour.getId(), 10).thenAccept(leaderboard -> {
                main.getLeaderboardHandler().addLeaderboard(parkour.getId(), leaderboard);

                Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                    if (parkour.getTopHologram() != null) {
                        Hologram header = api.createHologram(parkour.getTopHologram().clone().add(0.5D, 4.5D, 0.5D));
                        hologramLines.appendText(main.getLanguageHandler()
                                .getMessage("Holograms.Top.Header.Line1").replaceAll("%parkour%", parkour.getName()));
                        hologramLines.appendText(main.getLanguageHandler()
                                .getMessage("Holograms.Top.Header.Line2").replaceAll("%parkour%", parkour.getName()));

                        Hologram body = api.createHologram(parkour.getTopHologram().clone().add(0.5D, 3.75D, 0.5D));

                        Hologram footer = api.createHologram(parkour.getTopHologram().clone().add(0.5D, 1D, 0.5D));
                        hologramLines.appendText(main.getLanguageHandler()
                                .getMessage("Holograms.Top.Footer.Line")
                                .replaceAll("%time%", main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.HologramUpdate"), timeLeft * 1000)));

                        if(leaderboard != null) {
                            int i = 0;
                            for (LeaderboardEntry entry : leaderboard) {
                                String line = main.getLanguageHandler()
                                        .getMessage("Holograms.Top.Body.Line")
                                        .replaceAll("%player%", main.getPlayerDataHandler().getPlayerName((World) body.getPosition(), entry.getName()))
                                        .replaceAll("%position%", Integer.toString(i + 1))
                                        .replaceAll("%time%", main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.ParkourTimer"), entry.getTime()));

                                hologramLines.appendText(line);
                                i++;
                            }
                            for (int j = i; j < 10; j++) {
                                hologramLines.appendText(main.getLanguageHandler()
                                        .getMessage("Holograms.Top.Body.NoTime").replaceAll("%position%", Integer.toString(j + 1)));
                            }
                        } else {
                            for (int i = 0; i < 10; i++) {
                                hologramLines.appendText(main.getLanguageHandler()
                                        .getMessage("Holograms.Top.Body.NoTime").replaceAll("%position%", Integer.toString(i + 1)));
                            }
                        }

                        holoHeader.put(id, header);
                        holoBody.put(id, body);
                        holoFooter.put(id, (Hologram) hologramLines.get(0));
                    }
                }, 20L);
            });
        }
    }

    public void reloadTopHolograms() {
        if (main.isHologramsEnabled()) {
            if (timeLeft <= 0) {
                for (Parkour parkour : main.getParkourHandler().getParkours().values()) {

                    if(parkour.getTopHologram() != null) {
                        if (holoFoot.containsKey(parkour.getId())) {
                            holoFoot.get(parkour.getId()).setText(main.getLanguageHandler().getMessage("Holograms.Top.Footer.Updating"));
                        }
                    }

                    main.getDatabaseHandler().getParkourBestTimes(parkour.getId(), 10).thenAccept(leaderboard -> {
                        main.getLeaderboardHandler().addLeaderboard(parkour.getId(), leaderboard);

                        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                            if(parkour.getTopHologram() != null) {
                                if (holoBody.containsKey(parkour.getId()) && holoFooter.containsKey(parkour.getId())) {
                                    Hologram body = holoBody.get(parkour.getId());
                                    int i = 0;
                                    for (; i < leaderboard.size(); i++) {
                                        ((TextHologramLine) hologramLines.get(i)).setText(main.getLanguageHandler()
                                                .getMessage("Holograms.Top.Body.Line").replaceAll("%position%", Integer.toString(i + 1))
                                                .replaceAll("%player%", main.getPlayerDataHandler().getPlayerName((World) body.getPosition(), leaderboard.get(i).getName()))
                                                .replaceAll("%time%", main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.ParkourTimer"), leaderboard.get(i).getTime())));
                                    }
                                    for (int j = i; j < 10; j++) {
                                        ((TextHologramLine) hologramLines.get(j)).setText(main.getLanguageHandler()
                                                .getMessage("Holograms.Top.Body.NoTime").replaceAll("%position%", Integer.toString(j + 1)));
                                    }
                                }
                            }
                        }, 20L);
                    });
                }

                restartTimeLeft();
            }
            for (String parkour : main.getParkourHandler().getParkours().keySet()) {
                if (holoFoot.containsKey(parkour)) {
                    holoFoot.get(parkour)
                            .setText(main.getLanguageHandler()
                                    .getMessage("Holograms.Top.Footer.Line")
                                    .replaceAll("%time%", main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.HologramUpdate"), timeLeft * 1000)));
                }
            }
            timeLeft--;
        }
    }

    public void removeHologram(String id) {
        if (main.isHologramsEnabled()) {
            if (holoHeader.containsKey(id)) {
                holoHeader.get(id).delete();
                holoHeader.remove(id);
            }

            if (holoBody.containsKey(id)) {
                holoBody.get(id).delete();
                holoBody.remove(id);
            }

            if (holoFooter.containsKey(id)) {
                holoFooter.get(id).delete();
                holoFooter.remove(id);
            }
        }
    }

}
