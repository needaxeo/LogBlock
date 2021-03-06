package de.diddiz.LogBlock;

import static de.diddiz.util.Utils.parseTimeSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.util.config.Configuration;

public class Config
{
	public final Map<Integer, String> tables;
	public final String url, user, password;
	public final int delayBetweenRuns, forceToProcessAtLeast, timePerRun;
	public final boolean useBukkitScheduler;
	public final int keepLogDays;
	public final boolean dumpDeletedLog;
	public final boolean logBlockCreations, logBlockDestroyings, logSignTexts, logExplosions, logFire, logLeavesDecay, logLavaFlow, logChestAccess, logKills;
	public final boolean logCreeperExplosionsAsPlayerWhoTriggeredThese;
	public final LogKillsLevel logKillsLevel;
	public final Set<Integer> dontRollback;
	public final Set<Integer> replaceAnyway;
	public final QueryParams toolQuery, toolBlockQuery;
	public final int defaultDist, defaultTime;
	public final int toolID, toolblockID;
	public final boolean askRollbacks, askRedos, askClearLogs, askSavequeueBeforeRollback;
	public final Set<Integer> hiddenPlayers;

	public static enum LogKillsLevel {
		PLAYERS, MONSTERS, ANIMALS
	}

	Config(LogBlock logblock) throws DataFormatException, IOException {
		final Configuration config = logblock.getConfiguration();
		config.load();
		final List<String> keys = config.getKeys(null);
		List<String> subkeys;
		if (!keys.contains("version"))
			config.setProperty("version", logblock.getDescription().getVersion());
		if (!keys.contains("loggedWorlds"))
			config.setProperty("loggedWorlds", Arrays.asList(new String[]{"world", "world_nether"}));
		if (!keys.contains("tables"))
			config.setProperty("tables", Arrays.asList(new String[]{"lb-main", "lb-nether"}));
		subkeys = config.getKeys("mysql");
		if (subkeys == null)
			subkeys = new ArrayList<String>();
		if (!subkeys.contains("host"))
			config.setProperty("mysql.host", "localhost");
		if (!subkeys.contains("port"))
			config.setProperty("mysql.port", 3306);
		if (!subkeys.contains("database"))
			config.setProperty("mysql.database", "minecraft");
		if (!subkeys.contains("user"))
			config.setProperty("mysql.user", "username");
		if (!subkeys.contains("password"))
			config.setProperty("mysql.password", "pass");
		subkeys = config.getKeys("consumer");
		if (subkeys == null)
			subkeys = new ArrayList<String>();
		if (!subkeys.contains("delayBetweenRuns"))
			config.setProperty("consumer.delayBetweenRuns", 6);
		if (!subkeys.contains("forceToProcessAtLeast"))
			config.setProperty("consumer.forceToProcessAtLeast", 0);
		if (!subkeys.contains("timePerRun"))
			config.setProperty("consumer.timePerRun", 100);
		if (!subkeys.contains("useBukkitScheduler"))
			config.setProperty("consumer.useBukkitScheduler", true);
		subkeys = config.getKeys("clearlog");
		if (subkeys == null)
			subkeys = new ArrayList<String>();
		if (!subkeys.contains("dumpDeletedLog"))
			config.setProperty("clearlog.dumpDeletedLog", false);
		if (!subkeys.contains("keepLogDays"))
			config.setProperty("clearlog.keepLogDays", -1);
		subkeys = config.getKeys("logging");
		if (subkeys == null)
			subkeys = new ArrayList<String>();
		if (!subkeys.contains("logBlockCreations"))
			config.setProperty("logging.logBlockCreations", true);
		if (!subkeys.contains("logBlockDestroyings"))
			config.setProperty("logging.logBlockDestroyings", true);
		if (!subkeys.contains("logSignTexts"))
			config.setProperty("logging.logSignTexts", false);
		if (!subkeys.contains("logExplosions"))
			config.setProperty("logging.logExplosions", false);
		if (!subkeys.contains("logCreeperExplosionsAsPlayerWhoTriggeredThese"))
			config.setProperty("logging.logCreeperExplosionsAsPlayerWhoTriggeredThese", false);
		if (!subkeys.contains("logFire"))
			config.setProperty("logging.logFire", false);
		if (!subkeys.contains("logLeavesDecay"))
			config.setProperty("logging.logLeavesDecay", false);
		if (!subkeys.contains("logLavaFlow"))
			config.setProperty("logging.logLavaFlow", false);
		if (!subkeys.contains("logChestAccess"))
			config.setProperty("logging.logChestAccess", false);
		if (!subkeys.contains("logKills"))
			config.setProperty("logging.logKills", false);
		if (!subkeys.contains("logKillsLevel"))
			config.setProperty("logging.logKillsLevel", "PLAYERS");
		if (!subkeys.contains("hiddenPlayers"))
			config.setProperty("logging.hiddenPlayers", new ArrayList<String>());
		subkeys = config.getKeys("rollback");
		if (subkeys == null)
			subkeys = new ArrayList<String>();
		if (!subkeys.contains("dontRollback"))
			config.setProperty("rollback.dontRollback", Arrays.asList(new Integer[]{10, 11, 46, 51}));
		if (!subkeys.contains("replaceAnyway"))
			config.setProperty("rollback.replaceAnyway", Arrays.asList(new Integer[]{8, 9, 10, 11, 51}));
		subkeys = config.getKeys("lookup");
		if (subkeys == null)
			subkeys = new ArrayList<String>();
		if (!subkeys.contains("defaultDist"))
			config.setProperty("lookup.defaultDist", 20);
		if (!subkeys.contains("defaultTime"))
			config.setProperty("lookup.defaultTime", "30 minutes");
		if (!subkeys.contains("toolID"))
			config.setProperty("lookup.toolID", 270);
		if (!subkeys.contains("toolblockID"))
			config.setProperty("lookup.toolblockID", 7);
		if (!subkeys.contains("toolQuery"))
			config.setProperty("lookup.toolQuery", "area 0 all sum none limit 15 desc silent");
		if (!subkeys.contains("toolBlockQuery"))
			config.setProperty("lookup.toolBlockQuery", "area 0 all sum none limit 15 desc silent");
		subkeys = config.getKeys("questioner");
		if (subkeys == null)
			subkeys = new ArrayList<String>();
		if (!subkeys.contains("askRollbacks"))
			config.setProperty("questioner.askRollbacks", true);
		if (!subkeys.contains("askRedos"))
			config.setProperty("questioner.askRedos", true);
		if (!subkeys.contains("askClearLogs"))
			config.setProperty("questioner.askClearLogs", true);
		if (!subkeys.contains("askSavequeueBeforeRollback"))
			config.setProperty("questioner.askSavequeueBeforeRollback", true);
		if (!config.save())
			throw new IOException("Error while writing to config.yml");
		url = "jdbc:mysql://" + config.getString("mysql.host") + ":" + config.getString("mysql.port") + "/" + config.getString("mysql.database");
		user = config.getString("mysql.user");
		password = config.getString("mysql.password");
		delayBetweenRuns = config.getInt("consumer.delayBetweenRuns", 6);
		forceToProcessAtLeast = config.getInt("consumer.forceToProcessAtLeast", 0);
		timePerRun = config.getInt("consumer.timePerRun", 100);
		useBukkitScheduler = config.getBoolean("consumer.useBukkitScheduler", true);
		keepLogDays = config.getInt("clearlog.keepLogDays", -1);
		if (keepLogDays * 86400000L > System.currentTimeMillis())
			throw new DataFormatException("Too large timespan for keepLogDays. Must be shorter than " + (int)(System.currentTimeMillis() / 86400000L) + " days.");
		dumpDeletedLog = config.getBoolean("clearlog.dumpDeletedLog", false);
		logBlockCreations = config.getBoolean("logging.logBlockCreations", true);
		logBlockDestroyings = config.getBoolean("logging.logBlockDestroyings", true);
		logSignTexts = config.getBoolean("logging.logSignTexts", false);
		logExplosions = config.getBoolean("logging.logExplosions", false);
		logCreeperExplosionsAsPlayerWhoTriggeredThese = config.getBoolean("logging.logCreeperExplosionsAsPlayerWhoTriggeredThese", false);
		logFire = config.getBoolean("logging.logFire", false);
		logChestAccess = config.getBoolean("logging.logChestAccess", false);
		logLeavesDecay = config.getBoolean("logging.logLeavesDecay", false);
		logLavaFlow = config.getBoolean("logging.logLavaFlow", false);
		logKills = config.getBoolean("logging.logKills", false);
		try {
			logKillsLevel = LogKillsLevel.valueOf(config.getString("logging.logKillsLevel"));
		} catch (final IllegalArgumentException ex) {
			throw new DataFormatException("lookup.toolblockID doesn't appear to be a valid log level. Allowed are 'PLAYERS', 'MONSTERS' and 'ANIMALS'");
		}
		hiddenPlayers = new HashSet<Integer>();
		for (final String playerName : config.getStringList("logging.hiddenPlayers", new ArrayList<String>()))
			hiddenPlayers.add(playerName.hashCode());
		dontRollback = new HashSet<Integer>(config.getIntList("rollback.dontRollback", null));
		replaceAnyway = new HashSet<Integer>(config.getIntList("rollback.replaceAnyway", null));
		try {
			toolQuery = new QueryParams(logblock);
			toolQuery.prepareToolQuery = true;
			toolQuery.parseArgs(new ConsoleCommandSender(logblock.getServer()), Arrays.asList(config.getString("lookup.toolQuery").split(" ")));
		} catch (final IllegalArgumentException ex) {
			throw new DataFormatException("Error at lookup.toolQuery: " + ex.getMessage());
		}
		try {
			toolBlockQuery = new QueryParams(logblock);
			toolBlockQuery.prepareToolQuery = true;
			toolBlockQuery.parseArgs(new ConsoleCommandSender(logblock.getServer()), Arrays.asList(config.getString("lookup.toolBlockQuery").split(" ")));
		} catch (final IllegalArgumentException ex) {
			throw new DataFormatException("Error at lookup.toolBlockQuery: " + ex.getMessage());
		}
		defaultDist = config.getInt("lookup.defaultDist", 20);
		defaultTime = parseTimeSpec(config.getString("lookup.defaultTime").split(" "));
		toolID = config.getInt("lookup.toolID", 270);
		if (Material.getMaterial(toolID) == null || Material.getMaterial(toolID).isBlock())
			throw new DataFormatException("lookup.toolID doesn't appear to be a valid item id");
		toolblockID = config.getInt("lookup.toolblockID", 7);
		if (Material.getMaterial(toolblockID) == null || !Material.getMaterial(toolblockID).isBlock() || toolblockID == 0)
			throw new DataFormatException("lookup.toolblockID doesn't appear to be a valid block id");
		askRollbacks = config.getBoolean("questioner.askRollbacks", true);
		askRedos = config.getBoolean("questioner.askRedos", true);
		askClearLogs = config.getBoolean("questioner.askClearLogs", true);
		askSavequeueBeforeRollback = config.getBoolean("questioner.askSavequeueBeforeRollback", true);
		final List<String> worldNames = config.getStringList("loggedWorlds", null);
		final List<String> worldTables = config.getStringList("tables", null);
		tables = new HashMap<Integer, String>();
		if (worldNames == null || worldTables == null || worldNames.size() == 0 || worldNames.size() != worldTables.size())
			throw new DataFormatException("worldNames or worldTables not set properly");
		for (int i = 0; i < worldNames.size(); i++)
			tables.put(worldNames.get(i).hashCode(), worldTables.get(i));
	}
}
