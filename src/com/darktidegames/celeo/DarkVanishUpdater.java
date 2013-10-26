package com.darktidegames.celeo;

import org.bukkit.entity.Player;

/**
 * Makes sure that players have the appropriate levels of vision
 * 
 * @author Celeo
 */
public class DarkVanishUpdater implements Runnable
{

	private final DarkVanish plugin;

	public DarkVanishUpdater(DarkVanish instance)
	{
		plugin = instance;
	}

	@Override
	public void run()
	{
		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers())
		{
			if (plugin.isVanished(onlinePlayer))
				plugin.getObject(onlinePlayer).refreshVanish();
			else
				for (Player toShow : plugin.getServer().getOnlinePlayers())
					if (!toShow.canSee(onlinePlayer))
						toShow.showPlayer(onlinePlayer);
		}
	}

	public DarkVanish getPlugin()
	{
		return plugin;
	}

}