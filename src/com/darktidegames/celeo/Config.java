package com.darktidegames.celeo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Celeo
 */
public class Config
{

	private static DarkVanish plugin;

	public static String useNode = "darkvanish.vanish";
	public static String seeOthersNode = "darkvanish.seeothers";

	public static void load(DarkVanish instance)
	{
		plugin = instance;
		plugin.getDataFolder().mkdirs();
		loadNodes();
		List<String> toVanish = plugin.getConfig().getStringList("vanished");
		String[] data = null;
		for (String s : toVanish)
		{
			data = s.split(":");
			try
			{
				plugin.toggleVanish(data[0], false, Integer.valueOf(data[1]).intValue());
			}
			catch (Exception e)
			{
				plugin.toggleVanish(data[0], false, 1);
			}
		}
	}

	public static void loadNodes()
	{
		useNode = "darkvanish.vanish";
		seeOthersNode = "darkvanish.seeothers";
		useNode = plugin.getConfig().getString("permissions.usevanish", useNode);
		seeOthersNode = plugin.getConfig().getString("permissions.seeothers", seeOthersNode);
	}

	public static void save()
	{
		saveNodes(false);
		List<String> toSave = new ArrayList<String>();
		for (DarkVanishPlayer dvPlayer : plugin.vanished)
		{
			if (dvPlayer.isVanished())
				toSave.add((String.valueOf(dvPlayer.getPlayer().getName())));
		}
		plugin.getConfig().set("vanished", toSave);
		plugin.saveConfig();
	}

	public static void saveNodes(boolean commit)
	{
		plugin.getConfig().set("permissions.usevanish", useNode);
		plugin.getConfig().set("permissions.seeothers", seeOthersNode);
		if (commit)
			plugin.saveConfig();
	}

}