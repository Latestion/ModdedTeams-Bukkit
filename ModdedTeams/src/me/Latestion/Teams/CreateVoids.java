package me.Latestion.Teams;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CreateVoids {
	
	private Main plugin;
	
	public CreateVoids(Main plugin) {
		this.plugin = plugin;
	}
	
	public String getPlayerTeam(Player p) {
		return plugin.data.getConfig().getString("players." + p.getUniqueId().toString());
	}
	
	public String getPlayerTeam(OfflinePlayer p) {
		return plugin.data.getConfig().getString("players." + p.getUniqueId().toString());
	}
	
	public boolean inTeam(Player p) {
		List<String> uuids = new ArrayList<String>();
		try {
			plugin.data.getConfig().getConfigurationSection("players").getKeys(false).forEach(key -> { uuids.add(key); });
		}
		catch (Exception e) {
			return false;
		}
		if (uuids.contains(p.getUniqueId().toString())) return true;
		else return false;
	}
	
	public boolean inTeam(OfflinePlayer p) {
		List<String> uuids = new ArrayList<String>();
		try {
			plugin.data.getConfig().getConfigurationSection("players").getKeys(false).forEach(key -> { uuids.add(key); });
		}
		catch (Exception e) {
			return false;
		}
		if (uuids.contains(p.getUniqueId().toString())) return true;
		else return false;
	}
	
	public boolean isTeamTaken(String teamname) {
		List<String> teams = new ArrayList<String>();
		try {
			plugin.data.getConfig().getConfigurationSection("teams").getKeys(false).forEach(key -> { 
				teams.add(key.toLowerCase());
			});
		}
		catch (Exception e) {
			return false;
		}
		for (String s : teams) {
			if (s.equalsIgnoreCase(teamname)) {
				return true;
			}
		}
		return false;
	}

	
	@SuppressWarnings("deprecation")
	public List<Player> getOnlineTeamPlayers(Player p) {
		String s = plugin.data.getConfig().getString("players." + p.getUniqueId().toString());
		List<Player> uuids = new ArrayList<Player>();	
		plugin.data.getConfig().getConfigurationSection("players").getKeys(false).forEach(key -> {
			if (plugin.data.getConfig().getString("players." + key).equalsIgnoreCase(s)) {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.equals(Bukkit.getPlayer(UUID.fromString(key)))) {
						uuids.add(player);
					}
				}
			}
		});
		return uuids;
	}
	
	@SuppressWarnings("deprecation")
	public List<OfflinePlayer> getOfflineTeamPlayers(Player p) {
		String s = plugin.data.getConfig().getString("players." + p.getUniqueId().toString());
		List<OfflinePlayer> uuids = new ArrayList<OfflinePlayer>();	
		plugin.data.getConfig().getConfigurationSection("players").getKeys(false).forEach(key -> {
			if (plugin.data.getConfig().getString("players." + key).equalsIgnoreCase(s)) {
				List<Player> temp = new ArrayList<Player>();
				for (Player player : Bukkit.getOnlinePlayers()) {
					temp.add(player);
				}
				if (!temp.contains(Bukkit.getOfflinePlayer(UUID.fromString(key)))) {
					uuids.add(Bukkit.getOfflinePlayer(UUID.fromString(key)));
				}
			}
		});
		return uuids;
	}
	
	@SuppressWarnings("deprecation")
	public List<String> getOnlineTeamPlayersName(String team) {
		List<String> uuids = new ArrayList<String>();	
		plugin.data.getConfig().getConfigurationSection("players").getKeys(false).forEach(key -> {
			if (plugin.data.getConfig().getString("players." + key).equalsIgnoreCase(team)) {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.equals(Bukkit.getPlayer(UUID.fromString(key)))) {
						uuids.add(player.getName());
					}
				}
			}
		});
		return uuids;
	}
	
	@SuppressWarnings("deprecation")
	public List<String> getOfflineTeamPlayersName(String team) {
		List<String> uuids = new ArrayList<String>();
		plugin.data.getConfig().getConfigurationSection("players").getKeys(false).forEach(key -> {
			if (plugin.data.getConfig().getString("players." + key).equalsIgnoreCase(team)) {
				List<Player> temp = new ArrayList<Player>();
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					temp.add(player);
				}
				if (!temp.contains(Bukkit.getOfflinePlayer(UUID.fromString(key)))) {
					uuids.add(plugin.data.getConfig().getString("names." + key));
				}
			}
		});
		return uuids;
	}
	
	public String getTeamLeaderName(String s) {
		return plugin.data.getConfig().getString("names." + plugin.data.getConfig().getString("teams." + s + ".leader"));
		
	}
	
	public String getTeamCoLeaderName(String s) {
		String ss = "";
		try {
			ss = Bukkit.getOfflinePlayer(UUID.fromString(plugin.data.getConfig().getString("teams." + s + ".Co"))).getName();
		} catch (Exception e) {
		}
		return ss;
	}
	
	public Player getTeamLeader(String s) {
		return Bukkit.getPlayer(UUID.fromString(plugin.data.getConfig().getString("teams." + s + ".leader")));
	}
	
	public boolean isTeamLeaderOnline(String s) {
		if (this.getOnlinePlayerUUID().contains(plugin.data.getConfig().getString("teams." + s + ".leader")))  return true;
		else return false;
	}
	
	@SuppressWarnings("deprecation")
	public List<String> getOnlinePlayerNames() {
		List<String> send = new ArrayList<String>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			send.add(p.getName());
		}
		return send;
	}
	
	@SuppressWarnings("deprecation")
	public List<String> getOnlinePlayerUUID() {
		List<String> send = new ArrayList<String>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			send.add(p.getUniqueId().toString());
		}
		return send;
	}
	
	public List<String> getOfflinePlayerNames() {
		List<String> send = new ArrayList<String>();
		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			send.add(p.getName());
		}
		return send;
	}
	
	public String getUUIDFromName(String name) {
		String[] send = {""};
		plugin.data.getConfig().getConfigurationSection("names").getKeys(false).forEach(key -> {
			if (plugin.data.getConfig().getString("names." + key).equalsIgnoreCase(name)) {
				send[0] = key;
			}
		});
		return send[0];
	}
	
	public boolean isAlliedTo(String team, String allyTeam) {
		if (plugin.data.getConfig().getStringList("teams." + team + ".allies").contains(allyTeam)) return true;
		else return false;
	}
	
	@SuppressWarnings("deprecation")
	public List<Player> getOnlineTeamPlayers(String s) {
		List<Player> uuids = new ArrayList<Player>();	
		plugin.data.getConfig().getConfigurationSection("players").getKeys(false).forEach(key -> {
			if (plugin.data.getConfig().getString("players." + key).equalsIgnoreCase(s)) {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.equals(Bukkit.getPlayer(UUID.fromString(key)))) {
						uuids.add(player);
					}
				}
			}
		});
		return uuids;
	}
	
	public int getTotalAllie(String s) {
		return plugin.data.getConfig().getStringList("teams." + s + ".allies").size();
	}
	
	public List<String> getTeamAllies(String s) {
		return plugin.data.getConfig().getStringList("teams." + s + ".allies");
	}
	
	public String getStringLoc(Location loc) {
		return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
	}
	
	public Location getLocFromString(String ss) {
		String[] s = ss.split(",");
		Location give = new Location(Bukkit.getWorld(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]));
		return give;
	}
	
	public boolean isChunkClaimed(Location loc) {
		List<Chunk> chunk = new ArrayList<Chunk>();
		try {
			plugin.data.getConfig().getConfigurationSection("chunks").getKeys(false).forEach(key -> {
				chunk.add(loc.getWorld().getChunkAt(this.getLocFromString(key)));
			});
		} catch (Exception e) {

		}
		if (chunk.contains(loc.getWorld().getChunkAt(loc))) return true;
		return false;
	}
	
	public List<Chunk> getTeamChunks(String s) {
		List<Chunk> chunk = new ArrayList<Chunk>();
		try {
			plugin.data.getConfig().getConfigurationSection("chunks").getKeys(false).forEach(key -> {
				if (plugin.data.getConfig().getString("chunks." + key).equalsIgnoreCase(s)) {
					Location loc = this.getLocFromString(key);
					chunk.add(loc.getWorld().getChunkAt(loc));
				}
			});
		} catch (Exception e) {

		}
		return chunk;
	}
	
	public List<String> getRawTeamChunks(String s) {
		List<String> chunk = new ArrayList<String>();
		try {
			plugin.data.getConfig().getConfigurationSection("chunks").getKeys(false).forEach(key -> {
				if (plugin.data.getConfig().getString("chunks." + key).equalsIgnoreCase(s)) {
					chunk.add(key);
				}
			});
		} catch (Exception e) {

		}
		return chunk;
	}
	
	public void unclaimChunk(Location loc) {
		try {
			plugin.data.getConfig().getConfigurationSection("chunks").getKeys(false).forEach(key -> {
				Location noStringLoc = this.getLocFromString(key);
				if (noStringLoc.getWorld().getChunkAt(noStringLoc).equals(loc.getWorld().getChunkAt(loc))) {
					plugin.data.getConfig().set("chunks." + key, null);
					plugin.data.saveConfig();
				}
			});
		} catch (Exception e) {

		}
	}
	
	public void unclaimallChunk(String team) {
		try {
			plugin.data.getConfig().getConfigurationSection("chunks").getKeys(false).forEach(key -> {
				if (plugin.data.getConfig().getString("chunks." + key).equalsIgnoreCase(team)) {
					plugin.data.getConfig().set("chunks." + key, null);
					plugin.data.saveConfig();
				}
			});
		} catch (Exception e) {

		}
	}

	
	public void addToFlags(Player player, String event, Location loc, int typeID) {
		List<String> allFlags = plugin.flags.getConfig().getStringList("flags"); // Name, time, break/place, typeID, location
		String playerName = player.getName();
		String time = this.dateTimeGetter();
		String myLoc = "W- " + loc.getWorld().getName() + " X- " + loc.getBlockX() + " Y- " + loc.getBlockY() + " Z- " + loc.getBlockZ();
		String set = playerName + ", " + time + ", " + event + ", " + typeID + ", " + myLoc;
		allFlags.add(set);
		plugin.flags.getConfig().set("flags", allFlags);
		plugin.flags.saveConfig();
	}

	public void addToFlags(Player player, String event, Location loc) {
		List<String> allFlags = plugin.flags.getConfig().getStringList("flags"); // Name, time, break/place, typeID, location
		String playerName = player.getName();
		String time = this.dateTimeGetter();
		String myLoc = "W- " + loc.getWorld().getName() + " X- " + loc.getBlockX() + " Y- " + loc.getBlockY() + " Z- " + loc.getBlockZ();
		String set = playerName + ", " + time + ", " + event  + ", " + myLoc;
		allFlags.add(set);
		plugin.flags.getConfig().set("flags", allFlags);
		plugin.flags.saveConfig();
	}

	
	public String dateTimeGetter() {
		LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss");
		String formattedDate = myDateObj.format(myFormatObj);
		return formattedDate;
	}
	
	public Chunk getPlayerChunk(Player player) {
		return player.getLocation().getWorld().getChunkAt(player.getLocation());
	}
	
	public boolean isPlayerInEnemyChunks(Player player) {
		if (this.isChunkClaimed(player.getLocation())) {
			if (!this.getTeamChunks(this.getPlayerTeam(player)).contains(this.getPlayerChunk(player))) {
				return true;
			}
		}
		return false;
	}
	
	public String getChunkOwner(Location loc) {
		String[] team = {""};
		plugin.data.getConfig().getConfigurationSection("chunks").getKeys(false).forEach(key -> {
			if (loc.getWorld().getChunkAt(getLocFromString(key)).equals(loc.getWorld().getChunkAt(loc))) {
				team[0] = plugin.data.getConfig().getString("chunks." + key);
			}
		});
		return team[0];
	}

	public void saveRaid(String who, String enemy) {
		String time = dateTimeGetter();
		List<String> w = this.getOnlineTeamPlayersName(who);
		List<String> e = this.getOnlineTeamPlayersName(enemy);
		plugin.raids.getConfig().set(time + "." + who, w);					
		plugin.raids.getConfig().set(time + "." + enemy, e); 
		plugin.raids.saveConfig();
	}
	
	public void disbandTeam(Player player) {
		if (this.inTeam(player)) {
			if (this.getTeamLeader(this.getPlayerTeam(player)).equals(player)) {
				String teamname =  this.getPlayerTeam(player);
				plugin.data.getConfig().getConfigurationSection("players").getKeys(false).forEach(key -> {
					if (plugin.data.getConfig().getString("players." + key).equalsIgnoreCase(teamname)) {
						plugin.data.getConfig().set("players." + key, null);
						plugin.data.saveConfig();
					}
				});
				for (String allied : plugin.data.getConfig().getStringList("teams." + teamname + ".allies")) {
					List<String> allies = new ArrayList<String>();
					allies.addAll(plugin.data.getConfig().getStringList("teams." + allied + ".allies"));
					allies.remove(teamname);
					plugin.data.getConfig().set("teams." + allied + ".allies", allies);
					plugin.data.saveConfig();
				}
				
				this.unclaimallChunk(teamname);
					
				plugin.data.getConfig().set("teams." + teamname, null);
				plugin.data.saveConfig();
				player.sendMessage(ChatColor.RED + "Your team has been disbanded!");
			}
			else { 
				player.sendMessage(ChatColor.RED + "You are not the leader of this team!");
			}
		}
		else {
			player.sendMessage(ChatColor.RED + "You are not in a team!");
		}
	}
	
	public void disbandTeam(String teamname, Player player) {	
		
		if (!this.isTeamTaken(teamname)) {
			player.sendMessage(ChatColor.RED + "Invalid TeamName");
			return;
		}
		
		plugin.data.getConfig().getConfigurationSection("players").getKeys(false).forEach(key -> {
			if (plugin.data.getConfig().getString("players." + key).equalsIgnoreCase(teamname)) {
				plugin.data.getConfig().set("players." + key, null);
			}
		});
		for (String allied : plugin.data.getConfig().getStringList("teams." + teamname + ".allies")) {
			List<String> allies = new ArrayList<String>();
			allies.addAll(plugin.data.getConfig().getStringList("teams." + allied + ".allies"));
			allies.remove(teamname);
			plugin.data.getConfig().set("teams." + allied + ".allies", allies);
			plugin.data.saveConfig();
		}
			
		plugin.data.getConfig().set("teams." + teamname, null);
		plugin.data.saveConfig();
		player.sendMessage(ChatColor.RED + teamname + " has been disbanded!");
	}
	
}
