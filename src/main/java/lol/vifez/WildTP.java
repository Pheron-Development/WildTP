package lol.vifez;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WildTP extends JavaPlugin implements CommandExecutor {

    private Map<String, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("---------------------------------------------");
        getLogger().info("WildTP - [" + getDescription().getVersion() + "]");
        getLogger().info(" ");
        getLogger().info("Credits: Zephion Studios");
        getLogger().info("Discord: https://discord.gg/GNFHkPy6zC");
        getLogger().info("---------------------------------------------");

        getCommand("wild").setExecutor(this);

        saveDefaultConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            getLogger().info("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("wild")) {
            if (!player.hasPermission("wild.use") && !player.isOp() && !player.hasPermission(getConfig().getString("bypass-cooldown-permission"))) {
                player.sendMessage(ChatColor.RED + getConfig().getString("no-permission-message"));
                return true;
            }

            if (!player.isOp() || (getConfig().getBoolean("cooldown.bypass-for-operators") && getConfig().getBoolean("allow-op-bypass-cooldown")) || player.hasPermission(getConfig().getString("bypass-cooldown-permission"))) {
                if (getConfig().getBoolean("cooldown.enabled") && cooldowns.containsKey(player.getName())) {
                    long secondsLeft = ((cooldowns.get(player.getName()) / 1000) + getConfig().getInt("cooldown.time")) - (System.currentTimeMillis() / 1000);
                    if (secondsLeft > 0) {
                        player.sendMessage(ChatColor.RED + getConfig().getString("cooldown-message").replace("%time%", String.valueOf(secondsLeft)));
                        return true;
                    }
                }

                teleportPlayerRandomly(player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("command-message")));

                if (getConfig().getBoolean("cooldown.enabled")) {
                    cooldowns.put(player.getName(), System.currentTimeMillis());
                }
            }
        }

        return true;
    }

    private void teleportPlayerRandomly(Player player) {
        Random random = new Random();
        int x = random.nextInt(20000) - 10000;
        int z = random.nextInt(20000) - 10000;
        int y = player.getWorld().getHighestBlockYAt(x, z);

        player.teleport(new Location(player.getWorld(), x, y, z));
    }
}
