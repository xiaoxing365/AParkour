package me.davidml16.aparkour.utils;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.managers.ColorManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ParkourItems {

    private ItemStack returnItem;
    private ItemStack checkpointItem;

    private Main main;
    public ParkourItems(Main main) {
        this.main = main;
    }

    public void loadReturnItem() {
        String id = main.getConfig().getString("Items.Restart.ID");
        String name = ColorManager.translate(main.getConfig().getString("Items.Restart.Name"));
        String lore = ColorManager.translate(main.getConfig().getString("Items.Restart.Lore"));
        returnItem = new ItemBuilder(Material.matchMaterial(id), 1).setName(name).setLore(lore).toItemStack();
    }

    public void loadCheckpointItem() {
        String id = main.getConfig().getString("Items.Checkpoint.ID");
        String name = ColorManager.translate(main.getConfig().getString("Items.Checkpoint.Name"));
        String lore = ColorManager.translate(main.getConfig().getString("Items.Checkpoint.Lore"));
        checkpointItem = new ItemBuilder(Material.getMaterial(id), 1).setName(name).setLore(lore).toItemStack();
    }

    public ItemStack getRestartItem() {
        return returnItem;
    }

    public ItemStack getCheckpointItem() {
        return checkpointItem;
    }

}
