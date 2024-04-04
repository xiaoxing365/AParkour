package me.davidml16.aparkour.APIImplementer;

import me.filoghost.holographicdisplays.api.Position;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import me.filoghost.holographicdisplays.api.hologram.PlaceholderSetting;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class HDImplementer implements Hologram {
    @Override
    public @NotNull HologramLines getLines() {
        return null;
    }

    @Override
    public @NotNull VisibilitySettings getVisibilitySettings() {
        return null;
    }

    @Override
    public @NotNull Position getPosition() {
        return null;
    }

    @Override
    public void setPosition(@NotNull Position position) {

    }

    @Override
    public void setPosition(@NotNull String s, double v, double v1, double v2) {

    }

    @Override
    public void setPosition(@NotNull World world, double v, double v1, double v2) {

    }

    @Override
    public void setPosition(@NotNull Location location) {

    }

    @Override
    public @NotNull PlaceholderSetting getPlaceholderSetting() {
        return null;
    }

    @Override
    public void setPlaceholderSetting(@NotNull PlaceholderSetting placeholderSetting) {

    }

    @Override
    public void delete() {

    }

    @Override
    public boolean isDeleted() {
        return false;
    }
}
