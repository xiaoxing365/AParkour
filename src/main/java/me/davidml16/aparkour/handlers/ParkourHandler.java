package me.davidml16.aparkour.handlers;

import me.davidml16.aparkour.APIImplementer.HDAPIImplementer;
import me.davidml16.aparkour.APIImplementer.HDImplementer;
import me.davidml16.aparkour.APIImplementer.HDLineImplementer;
import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Plate;
import me.davidml16.aparkour.data.Reward;
import me.davidml16.aparkour.data.WalkableBlock;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.ItemBuilder;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.Position;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import me.filoghost.holographicdisplays.api.hologram.line.HologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.ItemHologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import me.filoghost.holographicdisplays.api.placeholder.*;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ParkourHandler {

	private HashMap<String, Parkour> parkours;
	private HashMap<String, File> parkourFiles;
	private HashMap<String, YamlConfiguration> parkourConfigs;

	private GameMode parkourGamemode;

	private Main main;

	public ParkourHandler(Main main) {
		this.main = main;
		this.parkours = new HashMap<>();
		this.parkourFiles = new HashMap<>();
		this.parkourConfigs = new HashMap<>();
		this.parkourGamemode = GameMode.valueOf(this.main.getConfig().getString("ParkourGamemode"));
	}

	public HashMap<String, Parkour> getParkours() {
		return parkours;
	}

	public HashMap<String, File> getParkourFiles() {
		return parkourFiles;
	}

	public HashMap<String, YamlConfiguration> getParkourConfigs() {
		return parkourConfigs;
	}

	public GameMode getParkourGamemode() {
		return parkourGamemode;
	}

	public void setParkourGamemode(GameMode parkourGamemode) {
		this.parkourGamemode = parkourGamemode;
	}

	public boolean createParkour(String id) {
		File file = new File(main.getDataFolder(), "parkours/" + id + ".yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
				parkourFiles.put(id, file);
				parkourConfigs.put(id, YamlConfiguration.loadConfiguration(file));
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean removeParkour(String id) {
		if(parkourFiles.containsKey(id) && parkourConfigs.containsKey(id)) {
			File file = parkourFiles.get(id);
			file.delete();
			parkourFiles.remove(id);
			parkourConfigs.remove(id);
			main.getDatabaseHandler().deleteParkourRows(id);
			return true;
		}
		return false;
	}

	public void saveConfig(String id) {
		try {
			File file = parkourFiles.get(id);
			if(file.exists()) {
				parkourConfigs.get(id).save(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FileConfiguration getConfig(String id) {
		return parkourConfigs.get(id);
	}

	public void loadParkours() {

		parkourConfigs.clear();
		parkourFiles.clear();
		parkours.clear();

		File directory = new File(main.getDataFolder(), "parkours");
		if(!directory.exists()) {
			directory.mkdir();
		}

		Main.log.sendMessage(ColorManager.translate(""));
		Main.log.sendMessage(ColorManager.translate("  &eLoading parkours:"));
		File[] allFiles = new File(main.getDataFolder(), "parkours").listFiles();
		for (File file : allFiles) {
			String id = file.getName().toLowerCase().replace(".yml", "");

			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			String name = config.getString("parkour.name");

			parkourFiles.put(id, file);
			parkourConfigs.put(id, config);

			if(!Character.isDigit(id.charAt(0))) {
				if (validParkourData(config)) {
					Location spawn = (Location) config.get("parkour.spawn");
					Location start = (Location) config.get("parkour.start");
					Location end = (Location) config.get("parkour.end");
					Location statsHologram = null;
					Location topHologram = null;

					if (main.isHologramsEnabled()) {
						if ((Location) config.get("parkour.holograms.stats") != null) {
							statsHologram = (Location) config.get("parkour.holograms.stats");
						}

						if ((Location) config.get("parkour.holograms.top") != null) {
							topHologram = (Location) config.get("parkour.holograms.top");
						}
					}

					Parkour parkour = new Parkour(main, id, name, spawn, start, end, statsHologram, topHologram);
					parkours.put(id, parkour);

					if (!config.contains("parkour.icon")) {
						config.set("parkour.icon", "item_frame");
					} else {
						String itemID = config.getString("parkour.icon").split(":")[0];
						byte itemData = Byte.parseByte(config.getString("parkour.icon").split(":")[1]);
						parkour.setIcon(new ItemBuilder(Material.getMaterial(itemID), 1).setDurability(itemData).toItemStack());
					}

					if (config.contains("parkour.walkableBlocks")) {
						List<WalkableBlock> walkable = getWalkableBlocks(id);
						parkour.setWalkableBlocks(walkable);
						saveWalkableBlocksString(id, walkable);
					}

					if (!config.contains("parkour.rewards")) {
						config.set("parkour.rewards", new ArrayList<>());
					}

					if (!config.contains("parkour.checkpoints")) {
						config.set("parkour.checkpoints", new ArrayList<>());
					}

					if (!config.contains("parkour.permissionRequired")) {
						config.set("parkour.permissionRequired.enabled", false);
						config.set("parkour.permissionRequired.permission", "aparkour.permission." + id);
						config.set("parkour.permissionRequired.message", "&cYou dont have permission to start this parkour!");
					}


					if (config.contains("parkour.permissionRequired")) {
						parkour.setPermissionRequired(config.getBoolean("parkour.permissionRequired.enabled"));
						parkour.setPermission(config.getString("parkour.permissionRequired.permission"));
						parkour.setPermissionMessage(config.getString("parkour.permissionRequired.message"));

						if (main.getServer().getPluginManager().getPermission(parkour.getPermission()) == null) {
							main.getServer().getPluginManager().addPermission(new Permission(parkour.getPermission()));
						}
					}

					if (!config.contains("parkour.checkpointsRequired")) {
						config.set("parkour.checkpointsRequired.enabled", true);
					}

					if (config.contains("parkour.checkpointsRequired")) {
						parkour.setRequireCheckpoints(config.getBoolean("parkour.checkpointsRequired.enabled", true));
					}

					if (!config.contains("parkour.plateHolograms.start")) {
						config.set("parkour.plateHolograms.start.enabled", false);
						config.set("parkour.plateHolograms.start.distanceBelowPlate", 2.5D);
					}

					if (!config.contains("parkour.plateHolograms.end")) {
						config.set("parkour.plateHolograms.end.enabled", false);
						config.set("parkour.plateHolograms.end.distanceBelowPlate", 2.5D);
					}

					if (!config.contains("parkour.plateHolograms.checkpoints")) {
						config.set("parkour.plateHolograms.checkpoints.enabled", false);
						config.set("parkour.plateHolograms.checkpoints.distanceBelowPlate", 2.5D);
					}

					if (!config.contains("parkour.titles.start")) {
						config.set("parkour.titles.start.enabled", false);
					}

					if (!config.contains("parkour.titles.end")) {
						config.set("parkour.titles.end.enabled", false);
					}

					if (!config.contains("parkour.titles.checkpoint")) {
						config.set("parkour.titles.checkpoint.enabled", false);
					}

					saveConfig(id);

					main.getCheckpointsHandler().loadCheckpoints(parkour, config);

					if (config.contains("parkour.plateHolograms")) {
						parkour.getStart().setHologramEnabled(config.getBoolean("parkour.plateHolograms.start.enabled"));
						parkour.getStart().setHologramDistance(config.getDouble("parkour.plateHolograms.start.distanceBelowPlate"));
						parkour.getEnd().setHologramEnabled(config.getBoolean("parkour.plateHolograms.end.enabled"));
						parkour.getEnd().setHologramDistance(config.getDouble("parkour.plateHolograms.end.distanceBelowPlate"));

						if(!parkour.getCheckpoints().isEmpty()) {
							boolean enabled = config.getBoolean("parkour.plateHolograms.checkpoints.enabled");
							double distance = config.getDouble("parkour.plateHolograms.checkpoints.distanceBelowPlate");
							for(Plate checkpoint : parkour.getCheckpoints()) {
								checkpoint.setHologramEnabled(enabled);
								checkpoint.setHologramDistance(distance);
							}
						}
					}

					if (config.contains("parkour.titles.start")) {
						parkour.setStartTitleEnabled(config.getBoolean("parkour.titles.start.enabled"));
					}

					if (config.contains("parkour.titles.end")) {
						parkour.setEndTitleEnabled(config.getBoolean("parkour.titles.end.enabled"));
					}

					if (config.contains("parkour.titles.checkpoint")) {
						parkour.setCheckpointTitleEnabled(config.getBoolean("parkour.titles.checkpoint.enabled"));
					}

					main.getPlateManager().loadPlates(parkour);

					Main.log.sendMessage(ColorManager.translate("    &a'" + name + "' &7- " + (parkour.getCheckpoints().size() > 0 ? "&a" : "&c") + parkour.getCheckpoints().size() + " checkpoints"));
				} else {
					Main.log.sendMessage(ColorManager.translate("    &c'" + name + "' not loaded because parkour data is not correct!"));
				}
			} else {
				Main.log.sendMessage(ColorManager.translate("    &c'" + name + "' not loaded because parkour id starts with a number!"));
			}
		}
		
		if(parkours.size() == 0)
			Main.log.sendMessage(ColorManager.translate("    &cNo parkour has been loaded!"));
		
		Main.log.sendMessage(ColorManager.translate(""));
	}

	HologramLines hologramLines = new HDLineImplementer();
	HolographicDisplaysAPI api = new HDAPIImplementer();
	Hologram HD = new HDImplementer();


	public void loadHolograms() {
		if(main.isHologramsEnabled()) {
            Parkour parkour = new Parkour();
			Location location= parkour.getStart().getLocation().clone().add(0.5D, parkour.getStart().getHologramDistance(), 0.5D);
            Hologram hologram = api.createHologram(location);
            parkours.values().forEach(parkour1 ->  {
				if (parkour.getStart().isHologramEnabled()) {
					hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.Start.Line1"));
					hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.Start.Line2"));
					parkour.getStart().setHologram(hologram);
				}
				if (parkour.getEnd().isHologramEnabled()) {
					hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.End.Line1"));
					hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.End.Line2"));
					parkour.getEnd().setHologram(hologram);
				}
				if (!parkour.getCheckpoints().isEmpty()) {
					int iterator = 1;
					for (Plate checkpoint : parkour.getCheckpoints()) {
						if (checkpoint.isHologramEnabled()) {
							hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.Checkpoint.Line1")
									.replaceAll("%checkpoint%", Integer.toString(iterator)));
							hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.Checkpoint.Line2")
									.replaceAll("%checkpoint%", Integer.toString(iterator)));
							checkpoint.setHologram(hologram);
							iterator++;
						}
					}
				}
			});
		}
	}


	public void loadHolograms(Parkour parkour) {
		if(main.isHologramsEnabled()) {
			if (parkour.getStart().isHologramEnabled()) {
				HD = api.createHologram(parkour.getStart().getLocation().clone().add(0.5D, parkour.getStart().getHologramDistance(), 0.5D));
				hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.Start.Line1"));
				hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.Start.Line2"));
				parkour.getStart().setHologram(HD);
			}
			if (parkour.getEnd().isHologramEnabled()) {
				HD = api.createHologram(parkour.getEnd().getLocation().clone().add(0.5D, parkour.getEnd().getHologramDistance(), 0.5D));
				hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.End.Line1"));
				hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.End.Line2"));
				parkour.getEnd().setHologram(HD);
			}
			if (!parkour.getCheckpoints().isEmpty()) {
				int iterator = 1;
				for (Plate checkpoint : parkour.getCheckpoints()) {
					if (checkpoint.isHologramEnabled()) {
						HD = api.createHologram(checkpoint.getLocation().clone().add(0.5D, checkpoint.getHologramDistance(), 0.5D));
						hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.Checkpoint.Line1")
								.replaceAll("%checkpoint%", Integer.toString(iterator)));
						hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.Checkpoint.Line2")
								.replaceAll("%checkpoint%", Integer.toString(iterator)));
						checkpoint.setHologram(HD);
						iterator++;
					}
				}
			}
		}
	}

	public void loadCheckpointHologram(Parkour parkour, Plate checkpoint) {
		if(main.isHologramsEnabled()) {
			FileConfiguration config = getConfig(parkour.getId());
			boolean enabled = config.getBoolean("parkour.plateHolograms.checkpoints.enabled");
			double distance = config.getDouble("parkour.plateHolograms.checkpoints.distanceBelowPlate");
			checkpoint.setHologramEnabled(enabled);
			checkpoint.setHologramDistance(distance);
			if (enabled) {
				HD = api.createHologram(checkpoint.getLocation().clone().add(0.5D, checkpoint.getHologramDistance(), 0.5D));
				hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.Checkpoint.Line1")
						.replaceAll("%checkpoint%", Integer.toString(parkour.getCheckpoints().size())));
				hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.Checkpoint.Line2")
						.replaceAll("%checkpoint%", Integer.toString(parkour.getCheckpoints().size())));
				checkpoint.setHologram(HD);
			}
		}
	}

	public void removeCheckpointHolograms(Parkour parkour) {
		if (main.isHologramsEnabled()) {
			for (Plate checkpoint : parkour.getCheckpoints()) {
				if (checkpoint.isHologramEnabled()) {
					checkpoint.getHologram().delete();
				}
			}
		}
	}

	public void loadCheckpointHolograms(Parkour parkour) {
		if(main.isHologramsEnabled()) {
			int iterator = 1;
			for (Plate checkpoint : parkour.getCheckpoints()) {
				if (checkpoint.isHologramEnabled()) {
					HD = api.createHologram(checkpoint.getLocation().clone().add(0.5D, checkpoint.getHologramDistance(), 0.5D));
					hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.Checkpoint.Line1")
							.replaceAll("%checkpoint%", Integer.toString(iterator)));
					hologramLines.appendText(main.getLanguageHandler().getMessage("Holograms.Plates.Checkpoint.Line2")
							.replaceAll("%checkpoint%", Integer.toString(iterator)));
					checkpoint.setHologram(HD);
					iterator++;
				}
			}
		}
	}

	public void removeHologram(String id) {
		if (main.isHologramsEnabled()) {
			Parkour parkour = parkours.get(id);
			if(parkour != null) {
				if (parkour.getStart().isHologramEnabled()) {
					parkour.getStart().getHologram().delete();
				}
				if (parkour.getEnd().isHologramEnabled()) {
					parkour.getEnd().getHologram().delete();
				}
				for (Plate checkpoint : parkour.getCheckpoints()) {
					if (checkpoint.getHologram() != null)
						checkpoint.getHologram().delete();
				}
			}
		}
	}

	public boolean parkourExists(String id) {
		return parkourFiles.containsKey(id);
	}

	public Parkour getParkourById(String id) {
		for (Parkour parkour : parkours.values()) {
			if (parkour.getId().equalsIgnoreCase(id))
				return parkour;
		}
		return null;
	}

	public Parkour getParkourByLocation(Location loc) {
		for (Parkour parkour : parkours.values()) {
			if (loc.equals(parkour.getStart().getLocation()) ||
					loc.equals(parkour.getEnd().getLocation()) ||
					parkour.getCheckpointLocations().contains(loc))
				return parkour;
		}
		return null;
	}

	public List<WalkableBlock> getWalkableBlocks(String id) {
		List<WalkableBlock> walkable = new ArrayList<WalkableBlock>();
		if(parkourConfigs.get(id).contains("parkour.walkableBlocks")) {
			for (String block : parkourConfigs.get(id).getStringList("parkour.walkableBlocks")) {
				String[] parts = block.split(":");
				Material material = null;
				material = Material.getMaterial(String.valueOf(Integer.parseInt(parts[0])));
				byte data = parts.length == 2 ? Byte.parseByte(parts[1]) : 0;
				WalkableBlock walkableBlockk = new WalkableBlock(parts[0], data);
				if (material != null && !walkable.contains(walkableBlockk)) {
					walkable.add(walkableBlockk);
				}
			}
		}
		return walkable;
	}

	public List<String> getWalkableBlocksString(List<WalkableBlock> walkable) {
		List<String> list = new ArrayList<String>();
		for(WalkableBlock block : walkable) {
			list.add(Material.getMaterial(block.getId()).getId() + ":" + block.getData());
		}
		return list;
	}

	public void saveWalkableBlocksString(String id, List<WalkableBlock> walkable) {
		List<String> list = new ArrayList<String>();
		for(WalkableBlock block : walkable) {
			list.add(Material.getMaterial(block.getId()).getId() + ":" + block.getData());
		}

		parkourConfigs.get(id).set("parkour.walkableBlocks", list);
		saveConfig(id);
	}

	public List<Reward> getRewards(String id) {
		List<Reward> rewards = new ArrayList<Reward>();
		FileConfiguration config = parkourConfigs.get(id);
		if (config.contains("parkour.rewards")) {
			if (config.getConfigurationSection("parkour.rewards") != null) {
				for (String rewardid : config.getConfigurationSection("parkour.rewards").getKeys(false)) {
					if(!config.contains("parkour.rewards." + rewardid + ".chance")) {
						config.set("parkour.rewards." + rewardid + ".chance", 100);
						saveConfig(id);
					}
					if (validRewardData(id, rewardid)) {
						String permission = config.getString("parkour.rewards." + rewardid + ".permission");
						String command = config.getString("parkour.rewards." + rewardid + ".command");
						boolean firstTime = config.getBoolean("parkour.rewards." + rewardid + ".firstTime");
						int chance = config.getInt("parkour.rewards." + rewardid + ".chance");
						rewards.add(new Reward(rewardid, permission, command, firstTime, chance));

						if (main.getServer().getPluginManager().getPermission(permission) == null) {
							main.getServer().getPluginManager().addPermission(new Permission(permission));
						}
					}
				}
			}
		}
		return rewards;
	}

	public List<Plate> getCheckpoints(String pid) {
		List<Plate> checkpoints = new ArrayList<Plate>();
		if (parkourConfigs.get(pid).contains("parkour.checkpoints")) {
			if (parkourConfigs.get(pid).getConfigurationSection("parkour.checkpoints") != null) {
				for (String id : parkourConfigs.get(pid).getConfigurationSection("parkour.checkpoints").getKeys(false)) {
					Location loc = (Location) parkourConfigs.get(id).get("parkour.checkpoints." + Integer.parseInt(id));
					checkpoints.add(new Plate(loc));
				}
			}
		}
		return checkpoints;
	}

	public boolean validParkourData(YamlConfiguration config) {
		return config.contains("parkour.spawn")
				&& config.contains("parkour.start")
				&& config.contains("parkour.end");
	}

	private boolean validRewardData(String parkourID, String rewardID) {
		FileConfiguration config = parkourConfigs.get(parkourID);
		return config.contains("parkour.rewards." + rewardID + ".permission")
				&& config.contains("parkour.rewards." + rewardID + ".command")
				&& config.contains("parkour.rewards." + rewardID + ".firstTime")
				&& config.contains("parkour.rewards." + rewardID + ".chance");
	}

	public void resetPlayer(Player p) {
		if(main.getSessionHandler().getSession(p) != null) {
			p.setFlying(false);
			p.setFallDistance(0);
			p.setNoDamageTicks(40);

			if (main.isParkourItemsEnabled()) {
				main.getPlayerDataHandler().restorePlayerInventory(p);
			}

			main.getSessionHandler().getSession(p).getParkour().getPlaying().remove(p.getUniqueId());
			main.getSessionHandler().getSession(p).cancelTimer();
			main.getSessionHandler().removeSession(p);
		}
	}

}
