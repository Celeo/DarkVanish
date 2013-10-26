package com.darktidegames.celeo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Celeo
 */
public class DarkVanish extends JavaPlugin
{

	private final static Logger log = Logger.getLogger("Minecraft");
	private final static boolean DEBUGGING = true;
	public static DarkVanishUpdater updater = null;
	public List<DarkVanishPlayer> vanished = new ArrayList<DarkVanishPlayer>();

	@Override
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new DarkVanishListener(this), this);
		getDataFolder().mkdirs();
		if (!new File(getDataFolder(), "/config.yml").exists())
			saveDefaultConfig();
		Config.load(this);
		getCommand("vanish").setExecutor(this);
		getCommand("poof").setExecutor(this);
		getCommand("pickup").setExecutor(this);
		updater = new DarkVanishUpdater(this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, updater, 60L, 600L);
		log("Enabled");
	}

	@Override
	public void onDisable()
	{
		List<String> saveVanish = new ArrayList<String>();
		for (DarkVanishPlayer p : vanished)
		{
			saveVanish.add(p.getPlayer().getName());
		}
		Config.save();
		getServer().getScheduler().cancelTasks(this);
		log("Disabled");
	}

	@SuppressWarnings("static-method")
	public void log(String message)
	{
		log.info("[DarkVanish] " + message);
	}

	public void debug(String message)
	{
		if (DEBUGGING)
			log("<DEBUG>" + message);
	}

	public DarkVanishPlayer getObject(Player player)
	{
		return getObject(player.getName());
	}

	public DarkVanishPlayer getObject(String name)
	{
		for (DarkVanishPlayer p : vanished)
			if (p.getPlayer().getName().equalsIgnoreCase(name))
				return p;
		if (getServer().getPlayer(name) != null
				&& hasPermission(getServer().getPlayer(name), Config.useNode))
			return addNewPlayer(new DarkVanishPlayer(this, getServer().getPlayer(name)));
		return null;
	}

	public DarkVanishPlayer addNewPlayer(DarkVanishPlayer dvPlayer)
	{
		vanished.add(dvPlayer);
		return dvPlayer;
	}

	public boolean isVanished(Player player)
	{
		return isVanished(player.getName());
	}

	public boolean isVanished(String name)
	{
		if (getObject(name) != null && getObject(name).isVanished())
			return true;
		return false;
	}

	public boolean isItemPickupDisabled(String name)
	{
		if (getObject(name) != null && getObject(name).isItemPickupDisabled())
			return true;
		return false;
	}

	public void toggleVanish(String name, boolean verbose, int mode)
	{
		try
		{
			getObject(name).toggleVanish(verbose, mode);
		}
		catch (NullPointerException npe)
		{
		}
	}

	public void toggleVanish(Player player, boolean verbose, int mode)
	{
		toggleVanish(player.getName(), verbose, mode);
	}

	public void toggleVanish(Player player)
	{
		toggleVanish(player, false, 1);
	}

	public void toggleVanish(String name)
	{
		toggleVanish(name, false, 1);
	}

	public void toggleItemPickup(String name) throws NullPointerException
	{
		getObject(name).toggleItemPickup();
	}

	@SuppressWarnings("static-method")
	public boolean hasPermission(Player player, String node)
	{
		if (player.isOp())
			return true;
		return player.hasPermission(node);
	}

	public static String randomName()
	{
		return String.valueOf(new Random().nextInt(1000));
	}

	public static boolean isNumber(String string)
	{
		try
		{
			Integer.valueOf(string);
			return true;
		}
		catch (NumberFormatException ex)
		{
		}
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!label.equalsIgnoreCase("vanish")
				&& !label.equalsIgnoreCase("pickup")
				&& !label.equalsIgnoreCase("poof"))
			return false;
		if (!(sender instanceof Player))
		{
			if (label.equalsIgnoreCase("vanish") && args.length == 1)
			{
				if (args[0].equalsIgnoreCase("list"))
				{
					String names = "";
					boolean first = true;
					for (DarkVanishPlayer dvPlayer : vanished)
					{
						if (!dvPlayer.isVanished())
							continue;
						if (first)
							names = String.valueOf(dvPlayer.getPlayer().getName());
						else
							names += ", "
									+ String.valueOf(dvPlayer.getPlayer().getName());
						first = false;
					}
					log("vanished players:");
					log(names);
				}
				else if (args[0].equalsIgnoreCase("-reload"))
				{
					Config.loadNodes();
					log("Settings loaded from the configuration file.");
				}
			}
			return true;
		}
		Player player = (Player) sender;
		String name = player.getName();
		if (label.equalsIgnoreCase("vanish") || label.equalsIgnoreCase("poof"))
		{
			if (args == null || args.length == 0)
			{
				if (!hasPermission(player, Config.useNode))
				{
					player.sendMessage(format("&cYou cannot use this command"));
					return true;
				}
				toggleVanish(player, label.equalsIgnoreCase("vanish"), 1);
			}
			else if (args.length >= 1)
			{
				String param = args[0];
				if (param.equalsIgnoreCase("-codename"))
				{
					if (!hasPermission(player, Config.useNode))
					{
						player.sendMessage(format("&cYou cannot use this command."));
						return true;
					}
					if (args.length == 2)
					{
						if (args[1].length() > 16)
						{
							player.sendMessage(format("&cCodenames can only be up to 16 chars long."));
							return true;
						}
						Config.save();
						player.sendMessage(format("&7Codename set to &c"
								+ args[1]));
					}
					else
						player.sendMessage(format("&c/vanish -codename [desired codename]"));
				}
				else if (param.equalsIgnoreCase("-list"))
				{
					if (!hasPermission(player, Config.seeOthersNode))
					{
						player.sendMessage(format("&You cannot use this command."));
						return true;
					}
					String names = "";
					boolean first = true;
					for (DarkVanishPlayer dvPlayer : vanished)
					{
						if (first)
							names = dvPlayer.getPlayer().getName();
						else
							names += ", " + dvPlayer.getPlayer().getName();
						first = false;
					}
					player.sendMessage(format("&7Vanished: &9" + names));
				}
				else if (args[0].equalsIgnoreCase("-reload"))
				{
					if (!hasPermission(player, Config.useNode))
					{
						player.sendMessage(format("&cYou cannot use this command."));
						return true;
					}
					Config.loadNodes();
					player.sendMessage(format("&7Settings loaded from the configuration file."));
				}
				else if (args[0].equalsIgnoreCase("-flush"))
				{
					if (!hasPermission(player, Config.useNode))
					{
						player.sendMessage(format("&cYou cannot use this command."));
						return true;
					}
					Config.saveNodes(true);
					player.sendMessage(format("&7Settings saved to the configuration file."));
				}
				else if (args[0].equalsIgnoreCase("-check"))
					player.sendMessage(format("&7You are "
							+ (isVanished(name) ? "" : "not ") + "vanished."));
				else
					player.sendMessage(format("&cWhat?"));
			}
			return true;
		}
		else if (label.equalsIgnoreCase("pickup"))
		{
			try
			{
				toggleItemPickup(name);
			}
			catch (Exception e)
			{
				player.sendMessage(format("&cYou are not vanished."));
			}
			return true;
		}
		return false;
	}

	public static String format(String raw)
	{
		if (!raw.contains("&"))
			return raw;
		String[] chunks = raw.split("&");
		String retString = chunks[0];
		if (chunks.length == 1)
			return raw;
		retString = chunks[0];
		for (int i = 1; i < chunks.length; i++)
			retString += "\u00A7" + chunks[i];
		return retString;
	}

}