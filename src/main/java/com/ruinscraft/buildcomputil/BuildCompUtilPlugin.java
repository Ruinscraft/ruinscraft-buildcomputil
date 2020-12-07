package com.ruinscraft.buildcomputil;

import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class BuildCompUtilPlugin extends JavaPlugin implements Listener {

	private BossBar bossBar;

	private static final String PREFIX = "[" + ChatColor.GOLD + "P2" + ChatColor.RESET + "] ";

	private static final long START_TIME = 1607382000000L;
	private static final long END_TIME = 1631052000000L;

	private PlotAuto plotAuto;

	@Override
	public void onEnable() {
		if (Bukkit.getPluginManager().isPluginEnabled("PlotSquared")) {
			getLogger().info("PlotSquared found!");
		} else {
			getLogger().info("PlotSquared not found.");
			return;
		}

		plotAuto = new PlotAuto();
		new PlotClaim();
		new PlotAdd();
		new PlotTrust();
		new PlotSetOwner();
		new PlotDelete();

		getServer().getPluginManager().registerEvents(this, this);

		bossBar = getServer().createBossBar(
				ChatColor.GOLD + "Time left", BarColor.YELLOW, BarStyle.SOLID);

		getServer().getScheduler().runTaskTimer(this, () -> {
			bossBar.setTitle(
					ChatColor.GOLD + "Time left: " +
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
	public void onChunkLoad(ChunkLoadEvent event) {
		for (Entity entity : event.getChunk().getEntities()) {
			if (entity instanceof Fireball
					|| entity.getType() == EntityType.FIREBALL) {
				entity.remove();
			}
		}
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		if (event.getEntity() instanceof Fireball
				|| event.getEntityType() == EntityType.FIREBALL) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		Player player = event.getPlayer();

		if (!player.hasPlayedBefore()) {
			givePowders(player);
		}

		PlotPlayer plotPlayer = PlotPlayer.wrap(player);

		if (plotPlayer.getPlotCount() < 1) {
			plotAuto.onCommand(plotPlayer, new String[] {});
		}

		bossBar.addPlayer(player);
	}

	private void givePowders(Player player) {
		String[] powders = new String[] {"2020", "AmongUs", "AmongUsImposter", "Biden", "Coronavirus", "GoogleHangouts", "KillerHornets", "Mask", "Skype", "SkypeRingtone", "StocksDown", "StocksUp", "ToiletPaper", "Trump", "Zoom"};

		for (String powder : powders) {
			getServer().dispatchCommand(getServer().getConsoleSender(), "lp user " + player.getUniqueId() + " permission set powder.powder." + powder + " true");
		}

		player.sendTitle(org.bukkit.ChatColor.GOLD + "POWDER UP!", "You received some 2020 Powders!", 5, 80, 20);
		player.sendMessage(org.bukkit.ChatColor.GOLD + "You received some 2020 Powders! Use '/pow events' to view them");
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1.0F);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);

		Player player = event.getPlayer();

		bossBar.removePlayer(player);
	}

	@EventHandler
	public void onWeather(WeatherChangeEvent event) {
		event.setCancelled(true);
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
