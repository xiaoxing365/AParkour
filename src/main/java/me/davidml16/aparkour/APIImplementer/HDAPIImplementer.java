package me.davidml16.aparkour.APIImplementer;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.Position;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.placeholder.*;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class HDAPIImplementer implements HolographicDisplaysAPI {
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
}
