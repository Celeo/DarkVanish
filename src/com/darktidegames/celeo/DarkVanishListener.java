package com.darktidegames.celeo;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Celeo
 */
public class DarkVanishListener implements Listener
{

	DarkVanish plugin;

	public DarkVanishListener(DarkVanish plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemPickup(PlayerPickupItemEvent event)
	{
		Player player = event.getPlayer();
		if (plugin.getObject(player) != null
				&& plugin.getObject(player).isItemPickupDisabled())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if (!event.getMessage().startsWith("/"))
			return;
		Player player = event.getPlayer();
		String[] args = event.getMessage().split(" ");
		if (args == null || args.length == 0)
			return;
		String label = args[0].replace("/", "");
		if (isMsgCmd(label) && args.length == 2 && plugin.isVanished(args[1]))
		{
			player.sendMessage(DarkVanish.format("&ePlayer not found."));
			event.setCancelled(true);
		}
		else if (label.equalsIgnoreCase("who")
				|| label.equalsIgnoreCase("online")
				|| label.equalsIgnoreCase("list"))
		{
			boolean first = true;
			String list = "";
			Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
			for (Player onlinePlayer : onlinePlayers)
			{
				if (plugin.isVanished(player)
						|| !plugin.isVanished(onlinePlayer)
						|| plugin.hasPermission(player, Config.useNode))
				{
					if (first)
						list = onlinePlayer.getName();
					else
						list += ", " + onlinePlayer.getName();
				}
				first = false;
			}
			player.sendMessage(String.valueOf(DarkVanish.format("&9There are &c"
					+ onlinePlayers.length
					+ " of "
					+ plugin.getServer().getMaxPlayers() + "&9 players online:")));
			player.sendMessage(list);
			event.setCancelled(true);
		}
	}

	public static boolean isMsgCmd(String label)
	{
		return label.equalsIgnoreCase("tell") || label.equalsIgnoreCase("msg")
				|| label.equalsIgnoreCase("message")
				|| label.equalsIgnoreCase("whisper");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (!(event instanceof EntityDamageByEntityEvent))
			return;
		EntityDamageByEntityEvent eveEvent = (EntityDamageByEntityEvent) event;
		Entity damager = eveEvent.getDamager();
		if (damager instanceof Player)
		{
			Player player = (Player) damager;
			if (plugin.isVanished(player))
			{
				player.sendMessage(DarkVanish.format("&7You cannot do damage while vanished."));
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTarget(EntityTargetEvent event)
	{
		if (event.getTarget() instanceof Player
				&& this.plugin.isVanished((Player) event.getTarget()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if (plugin.isVanished(player))
		{
			plugin.getObject(player).refreshVanish();
			event.setJoinMessage(null);
		}
		else
		{
			for (DarkVanishPlayer dvPlayer : plugin.vanished)
			{
				if (!player.hasPermission(Config.seeOthersNode))
				{
					dvPlayer.hideOtherFromThis(player);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		if (plugin.isVanished(event.getPlayer()))
			event.setQuitMessage(null);
	}

}