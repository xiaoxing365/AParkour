package me.davidml16.aparkour.managers;

import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class PlateManager {

    public void loadPlates(Parkour parkour) {
        Block start = parkour.getStart().getLocation().getWorld().getBlockAt(parkour.getStart().getLocation());
        try {
            if (start.getType() != Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                start.setType(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
            }
        } catch (NullPointerException ignored) {}

        try {
            Block end = parkour.getEnd().getLocation().getWorld().getBlockAt(parkour.getEnd().getLocation());
            if(end.getType() != Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                end.setType(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
            }
        } catch (NullPointerException ignored) {}

        for(Location checkpoint : parkour.getCheckpointLocations()) {
            Block cp = checkpoint.getWorld().getBlockAt(checkpoint);
            try {
                if(cp.getType() != Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                    cp.setType(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
                }
            } catch (NullPointerException ignored) {}
        }
    }

}
