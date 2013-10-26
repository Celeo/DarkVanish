package com.darktidegames.celeo;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Celeo
 */
public class DarkVanishPlayer
{

	private final DarkVanish plugin;
	private final Player player;
	private boolean isVanished;
	private boolean isItemPickupDisabled;
	private boolean isVerboselyVanished;

	/**
	 * 
	 * @param plugin
	 * @param player
	 */
	public DarkVanishPlayer(DarkVanish plugin, Player player)
	{
		this.plugin = plugin;
		this.player = player;
		this.isVanished = false;
		this.isItemPickupDisabled = false;
		this.isVerboselyVanished = false;
	}

	/**
	 * 
	 * @param verbose
	 * @param toMode
	 */
	public void toggleVanish(boolean verbose, int toMode)
	{
		if (isVanished)
		{
			// Show this player to everyone and default booleans to false
			isVanished = false;
			isItemPickupDisabled = false;
			isVerboselyVanished = false;
			for (Player onlinePlayer : plugin.getServer().getOnlinePlayers())
			{
				if (!onlinePlayer.canSee(player))
				{
					onlinePlayer.showPlayer(player);
				}
			}
			// Going to just remove this object from the list - no need to keep
			// it around anymore
			plugin.vanished.remove(this);
		}
		else
		{
			// Setup the variables and hide the player from those who should not
			// see
			isVanished = true;
			isItemPickupDisabled = true;
			for (Player onlinePlayer : plugin.getServer().getOnlinePlayers())
			{
				if (onlinePlayer.hasPermission(Config.seeOthersNode))
					onlinePlayer.showPlayer(player);
				else
					onlinePlayer.hidePlayer(player);
			}
		}

		// Update the viewable status of other vanished players
		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers())
		{
			if (onlinePlayer.hasPermission("darkvanish.seeothers"))
			{
				onlinePlayer.showPlayer(player);
			}
		}

		// Update the player, server, and logs of the player's new vanished
		// state
		if (isVerboselyVanished || verbose)
			plugin.getServer().broadcastMessage(ChatColor.YELLOW
					+ player.getName() + (isVanished ? " left" : " joined")
					+ " the game.");
		player.sendMessage(ChatColor.GRAY
				+ "You are now "
				+ (isVanished ? ChatColor.GREEN + "vanished" : ChatColor.RED
						+ "visible")
				+ ChatColor.GRAY
				+ (toMode > 1 && isVanished ? " at level " + ChatColor.BLUE
						+ toMode : "") + ChatColor.GRAY + ".");
		plugin.log(isVanished ? player.getName() + " is now vanished" : player.getName()
				+ " is now visible.");
	}

	/**
	 * 
	 */
	public void refreshVanish()
	{
		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers())
		{
			if (onlinePlayer.hasPermission("darkvanish.seeothers"))
				onlinePlayer.showPlayer(player);
			else
				onlinePlayer.hidePlayer(player);
		}
	}

	/**
	 * 
	 * @param toHide
	 */
	public void hideOtherFromThis(Player toHide)
	{
		toHide.hidePlayer(player);
	}

	/**
	 * 
	 */
	public void toggleItemPickup()
	{
		isItemPickupDisabled = !isItemPickupDisabled;
		player.sendMessage(ChatColor.GRAY
				+ (!isItemPickupDisabled ? "You are now picking up items." : "You are no longer picking up items."));
		plugin.log(isItemPickupDisabled ? player.getName()
				+ " is not picking up items." : player.getName()
				+ "is picking up items.");
	}

	public boolean isVanished()
	{
		return isVanished;
	}

	public Player getPlayer()
	{
		return player;
	}

	public DarkVanishPlayer setVanished(boolean isVanished)
	{
		this.isVanished = isVanished;
		return this;
	}

	public boolean isItemPickupDisabled()
	{
		return isItemPickupDisabled;
	}

	public DarkVanishPlayer setItemPickupDisabled(boolean isItemPickupDisabled)
	{
		this.isItemPickupDisabled = isItemPickupDisabled;
		return this;
	}

	public boolean isVerboselyVanished()
	{
		return isVerboselyVanished;
	}

	public DarkVanishPlayer setVerboselyVanished(boolean isVerboselyVanished)
	{
		this.isVerboselyVanished = isVerboselyVanished;
		return this;
	}

	public DarkVanish getPlugin()
	{
		return plugin;
	}

}