package org.glockinmybape.tattyrtp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class RtpManager {
    private ArrayList<String> groups;
    private Chat vaultChat = (Chat)Bukkit.getServicesManager().getRegistration(Chat.class).getProvider();
    private Main main;

    public ArrayList<String> getGroups() {
        return this.groups == null ? (this.groups = new ArrayList()) : this.groups;
    }

    public RtpManager(Main main) {
        this.main = main;
        main.getConfig().getConfigurationSection("groups").getKeys(false).forEach(this::addGroup);
    }

    public void addGroup(String group) {
        if (!this.getGroups().contains(group)) {
            this.getGroups().add(group);
        }
    }

    public String randomTeleport(Player player) {
        Location spot = this.findRandomLocation(Bukkit.getWorld(this.main.getConfig().getString("world")));
        player.teleport(spot);
        return spot.getBlockX() + " " + spot.getBlockY() + " " + spot.getBlockZ();
    }

    private int getRandom(int min, int max) {
        return (int)(Math.random() * (double)(max - min + 1) + (double)min);
    }

    private Location findRandomLocation(World world) {
        FileConfiguration cfg = this.main.getConfig();
        int xmax = cfg.getInt("x-max");
        int xmin = cfg.getInt("x-min");
        int zmax = cfg.getInt("z-max");
        int zmin = cfg.getInt("z-min");

        Material material;
        int randomX;
        int randomY;
        int randomZ;
        do {
            randomX = this.getRandom(xmin, xmax);
            randomZ = this.getRandom(zmin, zmax);
            randomY = world.getHighestBlockYAt(randomX, randomZ) - 1;
            material = world.getBlockAt(randomX, randomY, randomZ).getType();
        } while(cfg.contains("avoid-blocks") && this.getAvoidBlocks().contains(material));

        return new Location(world, (double)randomX + 0.5D, (double)randomY + 1.0D, (double)randomZ + 0.5D);
    }

    private String teleportNearbyLocation(Player player) {
        List<Player> players = new ArrayList();
        Iterator var3 = Bukkit.getOnlinePlayers().iterator();

        while(var3.hasNext()) {
            Player p = (Player)var3.next();
            if (p.getWorld().getName().equals(this.main.getConfig().getString("world")) && !p.getName().equalsIgnoreCase(player.getName())) {
                players.add(p);
            }
        }

        if (players.size() == 0) {
            throw new IllegalStateException("EMPTY_WORLD");
        } else {
            Player randomPlayer = (Player)players.get((new Random()).nextInt(players.size()));
            Location location2 = randomPlayer.getLocation();
            int distanceX = this.getRandom(this.main.getConfig().getInt("near"), -this.main.getConfig().getInt("near"));
            int distanceZ = this.getRandom(this.main.getConfig().getInt("near"), -this.main.getConfig().getInt("near"));
            location2.setX(location2.getX() + (double)distanceX);
            location2.setZ(location2.getZ() + (double)distanceZ);
            int y2 = player.getWorld().getHighestBlockYAt(location2.getBlockX(), location2.getBlockZ());
            location2.setY((double)y2);
            player.teleport(location2);
            return randomPlayer.getName();
        }
    }

    public String nearbyRandomTeleport(Player player) {
        return this.teleportNearbyLocation(player);
    }

    private List<Material> getAvoidBlocks() {
        List<Material> list = new ArrayList();
        Iterator var2 = this.main.getConfig().getStringList("avoid-blocks").iterator();

        while(var2.hasNext()) {
            String type = (String)var2.next();
            list.add(Material.matchMaterial(type));
        }

        return list;
    }

    public void removeGroup(String group) {
        this.getGroups().remove(group);
    }

    public boolean exists(String group) {
        return this.getGroups().contains(group);
    }

    public boolean hasNear(Player player) {
        return !this.exists(this.vaultChat.getPrimaryGroup(player)) ? false : this.main.getConfig().getBoolean("groups." + this.vaultChat.getPrimaryGroup(player) + ".near");
    }

    public int getCooldownNear(Player player) {
        return !this.exists(this.vaultChat.getPrimaryGroup(player)) ? 0 : this.main.getConfig().getInt("groups." + this.vaultChat.getPrimaryGroup(player) + ".near-cooldown");
    }

    public int getCooldownSafe(Player player) {
        return !this.exists(this.vaultChat.getPrimaryGroup(player)) ? 0 : this.main.getConfig().getInt("groups." + this.vaultChat.getPrimaryGroup(player) + ".safe-cooldown");
    }
}
