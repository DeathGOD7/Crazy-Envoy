package com.badbones69.crazyenvoys.paper;

import com.badbones69.crazyenvoys.paper.api.FileManager.Files;
import com.badbones69.crazyenvoys.paper.api.enums.Messages;
import com.badbones69.crazyenvoys.paper.controllers.FireworkDamageAPI;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Methods {

    private final Random random = new Random();

    private final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final FireworkDamageAPI fireworkDamageAPI = plugin.getFireworkDamageAPI();

    public String color(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }

    public String getPrefix() {
        return color(Files.CONFIG.getFile().getString("Settings.Prefix"));
    }

    public String getPrefix(String message) {
        return color(Files.CONFIG.getFile().getString("Settings.Prefix") + message);
    }

    @SuppressWarnings({"deprecation", "squid:CallToDeprecatedMethod"})
    public ItemStack getItemInHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    public Calendar getTimeFromString(String time) {
        Calendar cal = Calendar.getInstance();

        for (String i : time.split(" ")) {
            if (i.contains("D") || i.contains("d")) cal.add(Calendar.DATE, Integer.parseInt(i.replace("D", "").replace("d", "")));

            if (i.contains("H") || i.contains("h")) cal.add(Calendar.HOUR, Integer.parseInt(i.replace("H", "").replace("h", "")));

            if (i.contains("M") || i.contains("m")) cal.add(Calendar.MINUTE, Integer.parseInt(i.replace("M", "").replace("m", "")));

            if (i.contains("S") || i.contains("s")) cal.add(Calendar.SECOND, Integer.parseInt(i.replace("S", "").replace("s", "")));
        }

        return cal;
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    public boolean isOnline(String name) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public Player getPlayer(String name) {
        return plugin.getServer().getPlayer(name);
    }

    public boolean isInvFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public void firework(Location loc, List<Color> colors) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(colors).trail(false).flicker(false).build());
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);

        fireworkDamageAPI.addFirework(firework);

        detonate(firework);
    }

    private void detonate(Firework firework) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, firework::detonate, 2);
    }

    public Color getColor(String color) {
        if (color != null) {
            switch (color.toUpperCase()) {
                case "AQUA" -> {
                    return Color.AQUA;
                }
                case "BLACK" -> {
                    return Color.BLACK;
                }
                case "BLUE" -> {
                    return Color.BLUE;
                }
                case "FUCHSIA" -> {
                    return Color.FUCHSIA;
                }
                case "GRAY" -> {
                    return Color.GRAY;
                }
                case "GREEN" -> {
                    return Color.GREEN;
                }
                case "LIME" -> {
                    return Color.LIME;
                }
                case "MAROON" -> {
                    return Color.MAROON;
                }
                case "NAVY" -> {
                    return Color.NAVY;
                }
                case "OLIVE" -> {
                    return Color.OLIVE;
                }
                case "ORANGE" -> {
                    return Color.ORANGE;
                }
                case "PURPLE" -> {
                    return Color.PURPLE;
                }
                case "RED" -> {
                    return Color.RED;
                }
                case "SILVER" -> {
                    return Color.SILVER;
                }
                case "TEAL" -> {
                    return Color.TEAL;
                }
                case "WHITE" -> {
                    return Color.WHITE;
                }
                case "YELLOW" -> {
                    return Color.YELLOW;
                }
            }

            try {
                String[] rgb = color.split(",");
                return Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
            } catch (Exception ignore) {}
        }
        return Color.WHITE;
    }

    public List<String> getPage(List<String> list, Integer page) {
        List<String> locations = new ArrayList<>();

        if (page <= 0) page = 1;

        int max = 10;
        int index = page * max - max;
        int endIndex = index >= list.size() ? list.size() - 1 : index + max;

        for (; index < endIndex; index++) {
            if (index < list.size()) locations.add(list.get(index));
        }

        for (; locations.isEmpty(); page--) {
            if (page <= 0) break;

            index = page * max - max;
            endIndex = index >= list.size() ? list.size() - 1 : index + max;

            for (; index < endIndex; index++) {
                if (index < list.size()) locations.add(list.get(index));
            }
        }

        return locations;
    }

    public boolean isSuccessful(int min, int max) {
        if (max <= min || max <= 0) return true;

        int chance = 1 + random.nextInt(max);
        return chance <= min;
    }

    @SuppressWarnings({"deprecation", "squid:CallToDeprecatedMethod"})
    public List<Entity> getNearbyEntities(Location loc, double x, double y, double z) {
        List<Entity> out = new ArrayList<>();

        if (loc.getWorld() != null) {
            FallingBlock ent = loc.getWorld().spawnFallingBlock(loc.subtract(0, 0, 0), Material.AIR, (byte) 0);
            out = ent.getNearbyEntities(x, y, z);
            ent.remove();
        }

        return out;
    }

    public String convertTimeToString(Calendar timeTill) {
        Calendar rightNow = Calendar.getInstance();
        int total = ((int) (timeTill.getTimeInMillis() / 1000) - (int) (rightNow.getTimeInMillis() / 1000));
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;

        for (; total > 86400; total -= 86400, day++) ;
        for (; total > 3600; total -= 3600, hour++) ;
        for (; total >= 60; total -= 60, minute++) ;

        second += total;
        String message = "";

        if (day > 0) message += day + Messages.DAY.getMessage() + ", ";
        if (day > 0 || hour > 0) message += hour + Messages.HOUR.getMessage() + ", ";
        if (day > 0 || hour > 0 || minute > 0) message += minute + Messages.MINUTE.getMessage() + ", ";
        if (day > 0 || hour > 0 || minute > 0 || second > 0) message += second + Messages.SECOND.getMessage() + ", ";

        if (message.length() < 2) {
            message = "0" + Messages.SECOND.getMessage();
        } else {
            message = message.substring(0, message.length() - 2);
        }

        return message;
    }

    public String getUnBuiltLocation(Location location) {
        return "World:" + location.getWorld().getName() + ", X:" + location.getBlockX() + ", Y:" + location.getBlockY() + ", Z:" + location.getBlockZ();
    }

    public Location getBuiltLocation(String locationString) {
        World w = plugin.getServer().getWorlds().get(0);
        int x = 0;
        int y = 0;
        int z = 0;

        for (String i : locationString.toLowerCase().split(", ")) {
            if (i.startsWith("world:")) {
                w = plugin.getServer().getWorld(i.replace("world:", ""));
            } else if (i.startsWith("x:")) {
                x = Integer.parseInt(i.replace("x:", ""));
            } else if (i.startsWith("y:")) {
                y = Integer.parseInt(i.replace("y:", ""));
            } else if (i.startsWith("z:")) {
                z = Integer.parseInt(i.replace("z:", ""));
            }
        }

        return new Location(w, x, y, z);
    }

    public List<String> getPlaceholders(List<String> message, HashMap<String, String> lorePlaceholders) {
        List<String> lore = new ArrayList<>();

        for (String msg : message) {
            for (String placeholder : lorePlaceholders.keySet()) {
                msg = msg.replace(placeholder, lorePlaceholders.get(placeholder)).replace(placeholder.toLowerCase(), lorePlaceholders.get(placeholder));
            }

            lore.add(msg);
        }

        return lore;
    }
}