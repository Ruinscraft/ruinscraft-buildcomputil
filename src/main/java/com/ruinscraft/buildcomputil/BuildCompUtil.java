package com.ruinscraft.buildcomputil;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.intellectualcrafters.plot.object.PlotPlayer;

import net.md_5.bungee.api.ChatColor;

public class BuildCompUtil extends JavaPlugin implements Listener {

	private BossBar bossBar;

	private static final String PREFIX = "[" + ChatColor.AQUA + "P2" + ChatColor.RESET + "] ";

	private static final long START_TIME = 1530453600000L;
	private static final long END_TIME = 1531256400000L;

	@Override
	public void onEnable() {
		if (Bukkit.getPluginManager().isPluginEnabled("PlotSquared")) {
			getLogger().info("PlotSquared found!");
		} else {
			getLogger().info("PlotSquared not found.");
			return;
		}
		new PlotAuto();

		getServer().getPluginManager().registerEvents(this, this);

		bossBar = getServer().createBossBar(
				ChatColor.AQUA + "Time left", BarColor.YELLOW, BarStyle.SOLID);

		getServer().getScheduler().runTaskTimer(this, () -> {
			bossBar.setTitle(
					ChatColor.AQUA + "Time left: " + 
							ChatColor.RESET + getTimeRemaining());

			double pct = getPercentageLeft() / 100;

			bossBar.setProgress(pct);

			if (END_TIME - System.currentTimeMillis() < TimeUnit.DAYS.toMillis(1)) {
				bossBar.setColor(BarColor.RED);
			}
		}, 20L, 20L);
		for (Player player : Bukkit.getOnlinePlayers()) {
			bossBar.addPlayer(player);
		}
	}

	@Override
	public void onDisable() {
		bossBar.removeAll();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		bossBar.addPlayer(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		bossBar.removePlayer(player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (!(FilterUtils.isAppropriate(
				event.getMessage(), getConfig().getString("webpurify-api-key")))) {
			event.getPlayer().sendMessage(
					ChatColor.RED + "Please do not use inappropriate language!");
			event.setCancelled(true);
		}
	}

	public static String getTimeRemaining() {
		long elapsedTime = END_TIME - System.currentTimeMillis();

		return DurationFormatUtils.formatDurationWords(elapsedTime, true, true);
	}

	public static double getPercentageLeft() {
		if (System.currentTimeMillis() >= END_TIME) {
			return 0;
		}

		if (System.currentTimeMillis() <= START_TIME) {
			return 100;
		}

		return ((END_TIME - System.currentTimeMillis()) * 100 / (END_TIME - START_TIME));
	}

	public static void sendPrefixMessage(PlotPlayer player, String message) {
		player.sendMessage(PREFIX + message);
	}

}
