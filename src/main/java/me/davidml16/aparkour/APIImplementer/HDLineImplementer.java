package me.davidml16.aparkour.APIImplementer;

import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import me.filoghost.holographicdisplays.api.hologram.line.HologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.ItemHologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HDLineImplementer implements HologramLines {
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
}
