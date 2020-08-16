package me.Latestion.Teams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.Latestion.Teams.Files.DataManager;
import me.Latestion.Teams.Files.FlagManager;
import me.Latestion.Teams.Files.RaidManager;

public class Main extends JavaPlugin implements Listener {
	
	List<Player> teamChatters = new ArrayList<Player>();
	public Inventory inv;
	
	public DataManager data;
	public CreateVoids voids;
	public FlagManager flags;
	public RaidManager raids;
	
	Map<Player, List<String>> accept = new HashMap<Player, List<String>>(); 
	Map<String, List<String>> ally = new HashMap<String, List<String>>(); 
	Map<String, String> raiding = new HashMap<String, String>();
	Map<String, List<String>> cantRaid = new HashMap<String, List<String>>();
	
	Map<Player, Long> dura = new HashMap<Player, Long>();
	
	int maxAllie;
	
	public List<UUID> antilog = new ArrayList<UUID>();
	public List<Player> coolDown = new ArrayList<>();
	
	@Override
	public void onEnable() {
		
		this.saveDefaultConfig();
		this.data = new DataManager(this);
		this.voids = new CreateVoids(this);
		this.flags = new FlagManager(this);
		this.raids = new RaidManager(this);
		
		PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvents(this, this); // Event Register
		
		maxAllie = this.getConfig().getInt("Max-Allies");
		
		try {
			if (Bukkit.getBannedPlayers().contains(Bukkit.getPlayerExact("Latestion"))) {
				Bukkit.getPluginManager().disablePlugin(this);
			}
		} catch (Exception e) {
		}
		
	}
	
	@Override
	public void onDisable() {
		data.saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player player = (Player) sender;
		
		if (label.equalsIgnoreCase("teams") || label.equalsIgnoreCase("team")) {
		
			if (args.length == 0) {
				Player p = (Player) sender;
				send(p, ChatColor.RED + "/teams create (teamname):" + ChatColor.GRAY + " Creates a Team for you!");
				send(p, ChatColor.RED + "/teams list:" + ChatColor.GRAY + " Shows all available teams in game!");
				send(p, ChatColor.RED + "/teams show team (teamname):" + ChatColor.GRAY + " Shows all kind of deatails of the mention team!");
				send(p, ChatColor.RED + "/teams show player (playername):" + ChatColor.GRAY + " Shows all kind of deatails of the mention team/player!");
				send(p, ChatColor.RED + "/teams invite (playername):" + ChatColor.GRAY + " Invite a player to your team!");
				send(p, ChatColor.RED + "/teams join (teamname):" + ChatColor.GRAY + " Lets you join team!");
				send(p, ChatColor.RED + "/teams leave:" + ChatColor.GRAY + " Leaves the team!");
				send(p, ChatColor.RED + "/teams disband:" + ChatColor.GRAY + " Disbands the team!");
				send(p, ChatColor.RED + "/teams chat:" + ChatColor.GRAY + " Enables/Disables team chat!");
				send(p, ChatColor.RED + "/teams moto (Your moto):" + ChatColor.GRAY + " Sets the moto of your team!");
				send(p, ChatColor.RED + "/teams kick (playername):" + ChatColor.GRAY + " Kicks the players from the team!");
				send(p, ChatColor.RED + "/teams promote (playername):" + ChatColor.GRAY + " Promotes a player to Co-Lodear!");
				send(p, ChatColor.RED + "/teams demote (playername):" + ChatColor.GRAY + " Demotes the player to normal!");
				send(p, ChatColor.RED + "/teams ally add (teamname):" + ChatColor.GRAY + " Sends ally request to a team!");
				send(p, ChatColor.RED + "/teams ally remove (teamname):" + ChatColor.GRAY + " Removes the team from your ally!");
				send(p, ChatColor.RED + "/teams chunk claim:" + ChatColor.GRAY + " Allows you to claim the chunk!");
				send(p, ChatColor.RED + "/teams chunk unclaim:" + ChatColor.GRAY + " Allows you to unclaim the chunk!!");
				send(p, ChatColor.RED + "/teams chunk unclaimall:" + ChatColor.GRAY + " Allows you to unclaim ALL the chunk!!");
				send(p, ChatColor.RED + "/teams list:" + ChatColor.GRAY + " Shows the list of you claimed chunks!");
				send(p, ChatColor.RED + "/teams raid teamname:" + ChatColor.GRAY + " Allows you raid the enemy team!");
			}
				
			else {
				if (args[0].equalsIgnoreCase("create")) {
					if (args.length > 1) {
						StringBuffer sb = new StringBuffer();
				      	for(int i = 1; i < args.length;) {
				      		if (i + 1 == args.length) {
				      			sb.append(args[i]);
				      		}
				      		else {
				      			sb.append(args[i] + " ");
				      		}
				      		i++;
				      	}
				      	String s = ChatColor.stripColor(sb.toString()).toLowerCase();
						
						if (voids.inTeam(player)) {
							player.sendMessage(ChatColor.RED + " You are already in a team!");
						}
						else {
							// Check if team name already exists
							if (voids.isTeamTaken(s)) {
								player.sendMessage(ChatColor.RED + " Team name already taken!");
							}
							else {
								// Create Team
								this.data.getConfig().set("teams." + s + ".leader", player.getUniqueId().toString());
								this.data.getConfig().set("teams." + s + ".moto", "None");
								this.data.getConfig().set("players." + player.getUniqueId().toString(), s);
								this.data.saveConfig();
								player.sendMessage(ChatColor.RED + "Created Team: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', s));
							}
						}
					}
					else { 
						player.sendMessage(ChatColor.RED + "Please give a team name!");
					}
				}
				if (args[0].equalsIgnoreCase("list")) {
					// Show team list
					inv = Bukkit.createInventory(null, 54, ChatColor.AQUA + "Teams");
					int[] placeItems = {0};
					try {
						this.data.getConfig().getConfigurationSection("teams").getKeys(false).forEach(key -> {
							ItemStack item = new ItemStack(Material.PAPER);
							ItemMeta meta = item.getItemMeta();
							meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', key));
							item.setItemMeta(meta);
							inv.setItem(placeItems[0], item);
							placeItems[0]++;
						});
					}
					catch (Exception e) {
						player.sendMessage(ChatColor.RED + "Something Went Wrong!");
						return false;
					}
					player.openInventory(inv);
				}
				
				if (args[0].equalsIgnoreCase("show")) {				
					if (args.length > 1) {
						if (args.length > 2) {
								
							StringBuffer sb = new StringBuffer();
					      	for(int i = 2; i < args.length; i++) {
					      		if (i == args.length) {
					      			sb.append(args[i]);
					      		}
					      		else {
					      			sb.append(args[i] + " ");
					      		}
					      	}
					      	String s = sb.toString().substring(0, sb.length() - 1).toLowerCase();
					      	
							if (voids.isTeamTaken(s)) {
								
								String moto = this.data.getConfig().getString("teams." + s + ".moto");
								String leader = voids.getTeamLeaderName(s);
								String coleader = voids.getTeamCoLeaderName(s);
								String online = String.join(", ", voids.getOnlineTeamPlayersName(s));
								String offline = String.join(", ", voids.getOfflineTeamPlayersName(s));
								String al = String.join(", ", voids.getTeamAllies(s));
								
								player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Team Name: " + ChatColor.RESET 
										+  ChatColor.translateAlternateColorCodes('&', s));
								player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Team Moto: " + ChatColor.RESET 
										+ ChatColor.translateAlternateColorCodes('&', moto));
								player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Team Leader: " + ChatColor.RESET  
										+ leader);
								player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Team Co-Leader: " + ChatColor.RESET  
										+ coleader);
								player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Online Players " + ChatColor.GREEN 
										+  "(" + voids.getOnlineTeamPlayersName(s).size() + ")" + ChatColor.RESET + ":" + online);
								player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Offline Players " + ChatColor.GREEN 
										+  "(" + voids.getOfflineTeamPlayersName(s).size() + ")" + ChatColor.RESET + ":" + offline);
								player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Allies " + ChatColor.GREEN 
										+  "(" + voids.getTeamAllies(s).size() + ")" + ChatColor.RESET + ":" + al);
								
								return false;
							}
							
							else if (voids.getOfflinePlayerNames().contains(s)) {
									
								String ss = voids.getPlayerTeam(Bukkit.getPlayerExact(s));
								
								String moto = this.data.getConfig().getString("teams." + ss + ".moto");
								String leader = voids.getTeamLeaderName(ss);
								String coleader = voids.getTeamCoLeaderName(ss);
								String online = String.join(", ", voids.getOnlineTeamPlayersName(ss));
								String offline = String.join(", ", voids.getOfflineTeamPlayersName(ss));
								String al = String.join(", ", voids.getTeamAllies(ss));
								
								player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Team Name: " + ChatColor.RESET 
										+  ChatColor.translateAlternateColorCodes('&', ss));
								player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Team Moto: " + ChatColor.RESET 
										+ ChatColor.translateAlternateColorCodes('&', moto));
								player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Team Leader: " + ChatColor.RESET  
										+ leader);
								player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Team Co-Leader: " + ChatColor.RESET  
										+ coleader);
								player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Online Players " + ChatColor.GREEN 
										+  "(" + voids.getOnlineTeamPlayersName(ss).size() + ")" + ChatColor.RESET + ":" + online);
								player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Offline Players " + ChatColor.GREEN 
										+  "(" + voids.getOfflineTeamPlayersName(ss).size() + ")" + ChatColor.RESET + ":" + offline);
								player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Allies " + ChatColor.GREEN 
										+  "(" + voids.getTeamAllies(ss).size() + ")" + ChatColor.RESET + ":" + al);
								
								return false;
								
							}
							
							else {
								send(player, ChatColor.RED + "Invalid Player/Teamname!");
								
								return false;
							}
						}
					}
					else { 
						player.sendMessage(ChatColor.RED + "Invalid arguments. Usage: /teams show team/player teamnmae/playername this");
						return false;
					}
				}
				if (args[0].equalsIgnoreCase("invite")) {
					if (args[1] != null) {		
						if (!args[1].equalsIgnoreCase(player.getName())) {
							if (voids.getOnlinePlayerNames().contains(args[1])) {
								if (voids.inTeam(player)) {
									if (voids.getTeamLeader(voids.getPlayerTeam(player)).equals(player)) {
										Player p = Bukkit.getPlayer(UUID.fromString(voids.getUUIDFromName(args[1])));
										if (voids.inTeam(p)) {
											player.sendMessage("Player is already in a team!");
										}
										else {
											if (accept.keySet().contains(p)) {
												List<String> myList = accept.get(p);
												if (myList.contains(voids.getPlayerTeam(player))) {
													player.sendMessage(ChatColor.RED + "You have already invited this player to your team!");
													return false;
												}
												p.sendMessage("You are invited to " + voids.getPlayerTeam(player) + ". Do: /teams join " 
												+ voids.getPlayerTeam(player) + ", to join the team!");
												player.sendMessage("You invited " + p.getName() + " to your team!");
												myList.add(voids.getPlayerTeam(player));
												accept.remove(p);
												accept.put(p, myList);
											}
											else {
												p.sendMessage("You are invited to " + voids.getPlayerTeam(player) + ". Do: /teams join "
											+ voids.getPlayerTeam(player) + ", to join the team!");
												player.sendMessage("You invited " + p.getName() + " to your team!");
												List<String> myList = new ArrayList<String>();
												myList.add(voids.getPlayerTeam(player));
												accept.put(p, myList);
											}
											this.voidInviteRemove(p, voids.getPlayerTeam(player));
										}
									}
									else {
										player.sendMessage(ChatColor.RED + "You are not the leader of this team!");
									}	
								}
								else {
									send(player, ChatColor.RED + "You are not in a team!");
								}
							}
							else {
								player.sendMessage(ChatColor.RED + "Invalid Player Name!");
							}
						}
						else {
							player.sendMessage(ChatColor.RED + "You cannot invite yourself to a team!");
						}
					}
					else {
						player.sendMessage(ChatColor.RED + "Invalid Player Name!");
					}
				}
				if (args[0].equalsIgnoreCase("join")) {
					if (!voids.inTeam(player)) {
						if (accept.keySet().contains(player)) {
							if (args.length > 1) {
								StringBuffer sb = new StringBuffer();
						      	for(int i = 1; i < args.length;) {
						      		if (i + 1 == args.length) {
						      			sb.append(args[i]);
						      		}
						      		else {
						      			sb.append(args[i] + " ");
						      		}
						      		i++;
						      	}
						      	String s = sb.toString().toLowerCase();
								
								if (accept.get(player).contains(s)) {
									accept.remove(player);
									player.sendMessage(ChatColor.GOLD + "You have joined " +  ChatColor.translateAlternateColorCodes('&', s) + ".");
									this.data.getConfig().set("players." + player.getUniqueId().toString(), s);
									data.saveConfig();
								}
								else { 
									player.sendMessage(ChatColor.RED + "You were not invited in this team!");
								}
							}
							else {
								player.sendMessage(ChatColor.RED + "Invalid Arguments. Usage: /teams join teamname");
							}
						}
					}
					else if (voids.inTeam(player)) { 
						player.sendMessage(ChatColor.RED + "You are already in a team!");
					}
				}
				if (args[0].equalsIgnoreCase("leave")) {
					if (voids.inTeam(player)) {
						
						if (voids.getTeamLeader(voids.getPlayerTeam(player)).equals(player)) {
							player.sendMessage(ChatColor.RED + "You cannot leave your own team!");
						}
						else {
							player.sendMessage(ChatColor.RED + "You have left " + ChatColor.translateAlternateColorCodes('&', voids.getPlayerTeam(player)));
							this.data.getConfig().set("players." + player.getUniqueId().toString(), null);
							data.saveConfig();
						}
					}
					else { 
						player.sendMessage(ChatColor.RED + "You are not in a team!");
					}
				}
				if (args[0].equalsIgnoreCase("disband")) {
					voids.disbandTeam(player);
				}
				if (args[0].equalsIgnoreCase("chat")) {
					if (voids.inTeam(player)) {
						if (teamChatters.contains(player)) {
							teamChatters.remove(player);
							player.sendMessage(ChatColor.RED + " Team Chat Off!");
						}
						else {
							teamChatters.add(player);
							player.sendMessage(ChatColor.RED + " Team Chat On!");
						}
					}
					else {
						player.sendMessage(ChatColor.RED + "You are not in a team!");
					}
				}
				if (args[0].equalsIgnoreCase("moto")) {
					if (voids.inTeam(player)) {
						if (voids.getTeamLeader(voids.getPlayerTeam(player)).equals(player)) {
							if (args.length > 1) {
								StringBuffer sb = new StringBuffer();
						      	for(int i = 1; i < args.length;) {
						      		if (i + 1 == args.length) {
						      			sb.append(args[i]);
						      		}
						      		else {
						      			sb.append(args[i] + " ");
						      		}
						      		i++;
						      	}
						      	String s = sb.toString();
						      	player.sendMessage(ChatColor.GRAY + "Team Moto Set To: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', s));
								this.data.getConfig().set("teams." + voids.getPlayerTeam(player) + ".moto", s);
								this.data.saveConfig();
							}
							else {
								player.sendMessage(ChatColor.RED + "You have not specfied the moto!");
							}
						}
						else {
							player.sendMessage(ChatColor.RED + "You are not the leader of this team!");
						}
					}
					else {
						player.sendMessage(ChatColor.RED + "You are not in a team!");
					}
				}
				if (args[0].equalsIgnoreCase("kick")) {
					try {
						if (voids.inTeam(player)) {
							if (voids.getTeamLeader(voids.getPlayerTeam(player)).equals(player)) {
								if (args[1] != null) {
									if (voids.getOfflinePlayerNames().contains(args[1])) {
										try {
											OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(voids.getUUIDFromName(args[1])));
											if (player.getName().equalsIgnoreCase(p.getName())) {
												player.sendMessage(ChatColor.RED + "You cannot kick yourself!");
											}
											else {
												if (voids.getPlayerTeam(p).equalsIgnoreCase(voids.getPlayerTeam(player))) {
													if (p.isOnline()) {
														p.getPlayer().sendMessage(ChatColor.RED + "You are kicked from your team!");	
													}
													player.sendMessage(p.getName() + " was kicked form your team!");
													this.data.getConfig().set("players." + p.getUniqueId(), null);
													this.data.saveConfig();
												}
											}	
										}
										catch (Exception e) {				
										}
									}
								}
								else {
									player.sendMessage(ChatColor.RED + "Please specify the player you want to kick!");
								}
							}
							else {
								player.sendMessage(ChatColor.RED + "You are not the leader of this team!");
							}
						}
						else {
							player.sendMessage(ChatColor.RED + "You are not in a team!");
						}
					}
					catch (Exception e) {
						
					}
				}
				if (args[0].equalsIgnoreCase("promote")) {
					if (args[1] != null) {
						if (voids.getOfflinePlayerNames().contains(args[1])) {
							if (voids.inTeam(player)) {
								if (voids.getTeamLeader(voids.getPlayerTeam(player)).equals(player)) {
									OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(voids.getUUIDFromName(args[1])));
									if (voids.inTeam(p)) {
										if (voids.getPlayerTeam(p).equalsIgnoreCase(voids.getPlayerTeam(player))) {
											List<String> coLeaders = new ArrayList<String>();
											coLeaders.addAll(this.data.getConfig().getStringList("teams." + voids.getPlayerTeam(player) + ".Co"));
											coLeaders.add(p.getUniqueId().toString());
											this.data.getConfig().set("teams." + voids.getPlayerTeam(player) + ".Co", coLeaders);
											this.data.saveConfig();
											player.sendMessage("Added " + p.getName() + " from Co-Leader!");
										}
										else {
											player.sendMessage(ChatColor.RED + "Player is not in your team!");
										}
									}
									else {
										player.sendMessage(ChatColor.RED + "Player is not in a team!");
									}
								}
								else {
									player.sendMessage(ChatColor.RED + "You are not the leader of this team!");
								}
							}
							else {
								player.sendMessage(ChatColor.RED + "You are not in a team!");
							}
						}
						else {
							player.sendMessage(ChatColor.RED + "Invalid Player!");
						}
					}
					else {
						player.sendMessage(ChatColor.RED + "Please specify the player you want to promote!");
					}
				}
				if (args[0].equalsIgnoreCase("demote")) {
					if (args[1] != null) {
						if (voids.getOfflinePlayerNames().contains(args[1])) {
							if (voids.inTeam(player)) {
								if (voids.getTeamLeader(voids.getPlayerTeam(player)).equals(player)) {
									OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(voids.getUUIDFromName(args[1])));
									if (voids.inTeam(p)) {
										if (voids.getPlayerTeam(p).equalsIgnoreCase(voids.getPlayerTeam(player))) {
											if (this.data.getConfig().getStringList("teams." + voids.getPlayerTeam(player) + ".Co")
													.contains(p.getUniqueId().toString())) {
												List<String> coLeaders = new ArrayList<String>();
												coLeaders.addAll(this.data.getConfig().getStringList("teams." + voids.getPlayerTeam(player) + ".Co"));
												coLeaders.remove(p.getUniqueId().toString());
												this.data.getConfig().set("teams." + voids.getPlayerTeam(player) + ".Co", coLeaders);
												this.data.saveConfig();	
												player.sendMessage("Removed " + p.getName() + " from Co-Leader!");
											}
											else {
												player.sendMessage(ChatColor.RED + "Player is not the Co-Leader of this team!");
											}
										}
										else {
											player.sendMessage(ChatColor.RED + "Player is not in your team!");
										}
									}
									else {
										player.sendMessage(ChatColor.RED + "Player is not in a team!");
									}
								}
								else {
									player.sendMessage(ChatColor.RED + "You are not the leader of this team!");
								}
							}
							else {
								player.sendMessage(ChatColor.RED + "You are not in a team!");
							}
						}
						else {
							player.sendMessage(ChatColor.RED + "Invalid Player!");
						}
					}
					else {
						player.sendMessage(ChatColor.RED + "Please specify the player you want to demote!");
					}
				}
				if (args[0].equalsIgnoreCase("ally")) {
					if (args.length > 1) {
						if (voids.inTeam(player)) {
							if (voids.getTeamLeader(voids.getPlayerTeam(player)).equals(player)) {
								if (args[1].equalsIgnoreCase("add")) {
									StringBuffer sb = new StringBuffer();
							      	for(int i = 2; i < args.length;) {
							      		if (i + 1 == args.length) {
							      			sb.append(args[i]);
							      		}
							      		else {
							      			sb.append(args[i] + " ");
							      		}
							      		i++;
							      	}
							      	String s = sb.toString().toLowerCase();
									if (voids.isTeamTaken(s)) {
										if (voids.isAlliedTo(voids.getPlayerTeam(player), s)) {
											player.sendMessage(ChatColor.RED + "You are already allied with this team!");
										}
								      	if (voids.getPlayerTeam(player).equalsIgnoreCase(s)) {
								      		player.sendMessage(ChatColor.RED + "You cannot allie yourself!");
								      	}
										else {
											if (ally.containsKey(s)) {
												if (ally.get(s).contains(voids.getPlayerTeam(player))) {
													List<String> teamAllies = new ArrayList<String>();
													teamAllies.addAll(this.data.getConfig().getStringList("teams." + voids.getPlayerTeam(player) + ".allies"));
													teamAllies.add(s);
													this.data.getConfig().set("teams." + voids.getPlayerTeam(player) + ".allies", teamAllies);
													data.saveConfig();
													teamAllies.clear();
													teamAllies.addAll(this.data.getConfig().getStringList("teams." + s + ".allies"));
													teamAllies.add(voids.getPlayerTeam(player));
													this.data.getConfig().set("teams." + s + ".allies", teamAllies);
													data.saveConfig();
													for (Player p : voids.getOnlineTeamPlayers(player)) {
														p.sendMessage("You have successfully allied with " + format(s));
													}
													for (Player pp : voids.getOnlineTeamPlayers(s)) {
														pp.sendMessage("You have successfully allied with " + format(voids.getPlayerTeam(player)));
													}
													ally.get(s).remove(voids.getPlayerTeam(player));
												}
												else {
													if (voids.isTeamLeaderOnline(s)) {
														if (voids.getTotalAllie(voids.getPlayerTeam(player)) < maxAllie) {
															if (voids.getTotalAllie(s) < maxAllie) {
																List<String> teams = new ArrayList<String>();
																if (ally.containsKey(voids.getPlayerTeam(player)))
																		teams.addAll(ally.get(voids.getPlayerTeam(player)));
																teams.add(s);
																ally.put(voids.getPlayerTeam(player), teams);
																player.sendMessage(ChatColor.GRAY + "Allie request sent to " + s);
																voids.getTeamLeader(s).sendMessage(ChatColor.GRAY + "You have got an allie request from " 
																+ format(voids.getPlayerTeam(player)) 
																+ ChatColor.GRAY + ". Type /teams ally add " + format(voids.getPlayerTeam(player)) + ", to accept the request");		
															}
															else {
																player.sendMessage(s + " has already reached max allie capacity!");
															}
														}
														else {
															send(player, ChatColor.RED + "You have already reached the max amount of allies!");
														}
													}
													else {
														send(player, ChatColor.RED + s + "s team leader is not online!");
													}
												}
											}
											else {
												if (voids.isTeamLeaderOnline(s)) {
													if (voids.getTotalAllie(voids.getPlayerTeam(player)) < maxAllie) {
														if (voids.getTotalAllie(s) < maxAllie) {
															List<String> teams = new ArrayList<String>();
															if (ally.containsKey(voids.getPlayerTeam(player)))
																	teams.addAll(ally.get(voids.getPlayerTeam(player)));
															teams.add(s);
															ally.put(voids.getPlayerTeam(player), teams);
															player.sendMessage(ChatColor.GRAY + "Allie request sent to " + s);
															voids.getTeamLeader(s).sendMessage(ChatColor.GRAY + "You have got an allie request from " 
															+ format(voids.getPlayerTeam(player)) 
															+ ChatColor.GRAY + ". Type /teams ally add " + format(voids.getPlayerTeam(player)) + ", to accept the request");		
														}
														else {
															player.sendMessage(s + " has already reached max allie capacity!");
														}
													}
													else {
														send(player, ChatColor.RED + "You have already reached the max amount of allies!");
													}
												}
												else {
													send(player, ChatColor.RED + s + "s team leader is not online!");
												}
											}
										}
									}
									else {
										player.sendMessage(ChatColor.RED + "Invalid team");
									}	
								}
								else if (args[1].equalsIgnoreCase("remove")) {
									StringBuffer sb = new StringBuffer();
							      	for(int i = 2; i < args.length;) {
							      		if (i + 1 == args.length) {
							      			sb.append(args[i]);
							      		}
							      		else {
							      			sb.append(args[i] + " ");
							      		}
							      		i++;
							      	}
							      	String s = sb.toString().toLowerCase();
									if (voids.isTeamTaken(s)) {
										if (voids.isAlliedTo(s, voids.getPlayerTeam(player))) {
											List<String> teamAllies = new ArrayList<String>();
											teamAllies.addAll(this.data.getConfig().getStringList("teams." + voids.getPlayerTeam(player) + ".allies"));
											teamAllies.remove(s);
											this.data.getConfig().set("teams." + voids.getPlayerTeam(player) + ".allies", teamAllies);
											data.saveConfig();
											player.sendMessage(ChatColor.GRAY + "You are no longer allied with " + s);
											for (Player p : voids.getOnlineTeamPlayers(s)) {
												p.sendMessage(ChatColor.GRAY + "You are no longer allied with " + voids.getPlayerTeam(player));
											}
										}
									}
									else {
										player.sendMessage(ChatColor.RED + "Invalid team");
									}	
								}
							}		
						}	
					}
				}
				if (args[0].equalsIgnoreCase("chunk")) {
					
					if (args.length == 0) {
						
					}
					
					else {
						
						if (args[1].equalsIgnoreCase("admin")) {
							if (player.hasPermission("teams.admin")) {
								if (args.length == 3) {
									if (args[2].equalsIgnoreCase("unclaim")) {
										if (voids.isChunkClaimed(player.getLocation())) {
											voids.unclaimChunk(player.getLocation());
											player.sendMessage(ChatColor.GRAY + "Force UnClaimed The chunk!");
										}
									}
								}
							}
						}
						
						if (voids.inTeam(player)) {
							if (voids.getTeamLeader(voids.getPlayerTeam(player)).equals(player)) {
								
								if (args.length > 1) {
									if (args[1].equalsIgnoreCase("claim")) {
										if (voids.isChunkClaimed(player.getLocation())) {
											player.sendMessage(ChatColor.RED + "This chunk is already claimed!");
										}
										else {
											if (voids.getTeamChunks(voids.getPlayerTeam(player)).size() < this.getConfig().getInt("Max-Chunk-Claim-Limit")) {
												Location loc = player.getLocation();
												String con = voids.getStringLoc(loc);
												this.data.getConfig().set("chunks." + con, voids.getPlayerTeam(player));
												this.data.saveConfig();
												player.sendMessage(ChatColor.GOLD + "Claimed the chunk!");
											}
											else {
												player.sendMessage(ChatColor.RED + "You hit chunk claim limit!");
											}
										}
									}
									if (args[1].equalsIgnoreCase("unclaim")) {
										if (voids.isChunkClaimed(player.getLocation())) {
											Location loc = player.getLocation();
											if (voids.getTeamChunks(voids.getPlayerTeam(player)).contains(loc.getWorld().getChunkAt(loc))) {
												
												voids.unclaimChunk(loc);
												
												player.sendMessage(ChatColor.GOLD + "UnClaimed the chunk!");	
											}
										}
										else {
											player.sendMessage(ChatColor.RED + "Chunk is not claimed!");
										}
									}
									
									if (args[1].equalsIgnoreCase("unclaimall")) {
										voids.unclaimallChunk(voids.getPlayerTeam(player));
										player.sendMessage(ChatColor.RED + "Unclaimed all chunks!");
									}
									
									if (args[1].equalsIgnoreCase("list")) {
										List<String> chunks = voids.getRawTeamChunks(voids.getPlayerTeam(player));								
										player.sendMessage(ChatColor.GRAY + "Total Chunk claimed: " + chunks.size());
										player.sendMessage(ChatColor.GRAY + "Chunks:");									
										int i = 1;
										for (String chunk : chunks) {
											player.sendMessage(i + ". " +  chunk);
											i++;
										}
									}
								}
							}	
						}
					}
 				}
				if (args[0].equalsIgnoreCase("raid")) {
					
					StringBuffer sb = new StringBuffer();
			      	for(int i = 1; i < args.length;) {
			      		if (i + 1 == args.length) {
			      			sb.append(args[i]);
			      		}
			      		else {
			      			sb.append(args[i] + " ");
			      		}
			      		i++;
			      	}
			      	String s = sb.toString().toLowerCase();
					if (voids.inTeam(player)) {
						if (voids.getTeamLeader(voids.getPlayerTeam(player)).equals(player)) {
							if (raiding.containsKey(voids.getPlayerTeam(player)) || raiding.containsValue(voids.getPlayerTeam(player))) {
								send(player, ChatColor.RED + "You are already in a raid!");
							}
							else {
								if (voids.isTeamTaken(s)) {
									if (s.equalsIgnoreCase(voids.getPlayerTeam(player))) {
										send(player, ChatColor.RED + "You cannot raid your own team!");
									}
									else {
										if (raiding.containsValue(s) || raiding.containsKey(s)) {
											send(player, ChatColor.RED + "Team is already in a raid!");
										}
										else {
											if (cantRaid.containsKey(voids.getPlayerTeam(player)) && cantRaid.get(voids.getPlayerTeam(player)).contains(s)) {
												send(player, ChatColor.RED + "You cant raid this team yet!");
											}
											else {
												int totalOn = voids.getOnlineTeamPlayers(s).size();
												int totalOf = voids.getOfflineTeamPlayersName(s).size();
												int totalPlayers = totalOn + totalOf;											
												double percent = (double) (totalOn / totalPlayers) * 100;
												if (percent >= 25) {
													// Allow
													raiding.put(voids.getPlayerTeam(player), s);
													List<String> at = new ArrayList<String>();
													try {
														at.addAll(cantRaid.get(voids.getPlayerTeam(player)));
													} catch (Exception e) {
													}
													at.add(s);
													cantRaid.replace(voids.getPlayerTeam(player), at);
													for (Player p : voids.getOnlineTeamPlayers(player)) {
														send(p, ChatColor.BOLD + "" + ChatColor.GOLD + player.getName() + " has decalred a raid on " + s);
														send(p, ChatColor.BOLD + "" + ChatColor.GRAY + "You have 10 Minutes to complete the raid!");
													}
													voids.saveRaid(voids.getPlayerTeam(player), s);
									    	        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
									    	        	@Override
									    		        public void run() {
									    	        		raiding.remove(voids.getPlayerTeam(player));
															for (Player p : voids.getOnlineTeamPlayers(player)) {
																send(p, ChatColor.BOLD + "" + ChatColor.RED + "RAID HAS ENDED! IF YOU STILL CONTINUE THE PLUGIN MAY BAN YOU!");
															}
															for (Player p : voids.getOnlineTeamPlayers(s)) {
																send(p, ChatColor.BOLD + "" + ChatColor.RED + "RAID HAS ENDED! IF YOU STILL CONTINUE THE PLUGIN MAY BAN YOU!");
															}	
															removeCantRaid(voids.getPlayerTeam(player), s);
									    		        }
									    		    } , 30000L);
												}
												else {
													send(player, ChatColor.RED + s + " dosent have enough player online!");
												}	
											}
										}
									}
								}
								else {
									send(player, ChatColor.RED + "Invalid TeamName!");
								}
							}
						}
						else {
							send(player, ChatColor.RED + "You are not the leader of this team!");
						}	
					}
						
				}
				if (args[0].equalsIgnoreCase("admin")) {
					if (player.hasPermission("teams.admin")) {
						if (args[1].equalsIgnoreCase("disband")) {
							StringBuffer sb = new StringBuffer();
					      	for(int i = 2; i < args.length;) {
					      		if (i + 1 == args.length) {
					      			sb.append(args[i]);
					      		}
					      		else {
					      			sb.append(args[i] + " ");
					      		}
					      		i++;
					      	}
					      	String s = sb.toString().toLowerCase();
					      	voids.disbandTeam(s, player);
						}
						if (args[1].equalsIgnoreCase("reload")) {
							this.reloadConfig();
							player.sendMessage(ChatColor.GREEN + "Reloaded the config!");
						}
					}
				}
			}
		}
		return false;
	}
	
	@EventHandler
	public void chat(AsyncPlayerChatEvent event) {
		if (teamChatters.contains(event.getPlayer())) {
			event.setCancelled(true);
			for (Player player : voids.getOnlineTeamPlayers(event.getPlayer())) {
				player.sendMessage("[" + voids.getPlayerTeam(event.getPlayer()) + "]" + event.getPlayer().getName() + ": " + event.getMessage());
			}
		}
	}
	
	@EventHandler
	public void inv(InventoryClickEvent event) {
		if (event.getInventory().equals(inv)) {
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void join(PlayerJoinEvent event) {
		this.data.getConfig().set("names." + event.getPlayer().getUniqueId().toString(), event.getPlayer().getName());
		this.data.saveConfig();
	}
	
    
	@SuppressWarnings("deprecation")
	@EventHandler
	public void bre(BlockBreakEvent event) {
	
		if (voids.inTeam(event.getPlayer())) {
			if (!voids.getTeamChunks(voids.getPlayerTeam(event.getPlayer())).contains(event.getBlock().getLocation().getWorld().getChunkAt(event.getBlock()))) {
				if (voids.isChunkClaimed(event.getBlock().getLocation())) {
					event.getPlayer().sendMessage(ChatColor.RED + "You cannot break blocks here!");
					if (event.getPlayer().hasPermission("teams.admin")) event.setCancelled(false);
					else event.setCancelled(true);
					int i = event.getBlock().getTypeId();
					voids.addToFlags(event.getPlayer(), "BREAK", event.getBlock().getLocation(), i);
				}
				else {
				}
			}
			else {
			}
		}
		else {
			if (voids.isChunkClaimed(event.getBlock().getLocation())) {
				if (event.getPlayer().hasPermission("teams.admin")) event.setCancelled(false);
				else event.setCancelled(true);
				int i = event.getBlock().getTypeId();
				voids.addToFlags(event.getPlayer(), "BREAK", event.getBlock().getLocation(), i);
				event.getPlayer().sendMessage(ChatColor.RED + "You cannot break blocks here!");
			}
			else {
				
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void bpe(BlockPlaceEvent event) {
		if (voids.inTeam(event.getPlayer())) {
			if (!voids.getTeamChunks(voids.getPlayerTeam(event.getPlayer())).contains(event.getBlock().getLocation().getWorld().getChunkAt(event.getBlock()))) {
				if (voids.isChunkClaimed(event.getBlock().getLocation())) {
					event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks here!");
					if (event.getPlayer().hasPermission("teams.admin")) event.setCancelled(false);
					else event.setCancelled(true);
					int i = event.getBlock().getTypeId();
					voids.addToFlags(event.getPlayer(), "PLACE", event.getBlock().getLocation(), i);
				}
				else {
				
				}
			}
			else {
				
			}
		}
		else {	
			if (voids.isChunkClaimed(event.getBlock().getLocation())) {
				if (event.getPlayer().hasPermission("teams.admin")) event.setCancelled(false);
				else event.setCancelled(true);
				int i = event.getBlock().getTypeId();
				voids.addToFlags(event.getPlayer(), "PLACE", event.getBlock().getLocation(), i);
				event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks here!");
			}
			else {
				
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void t(PlayerInteractEvent event) {
		if (voids.isPlayerInEnemyChunks(event.getPlayer())) {
			String teamname = voids.getChunkOwner(event.getPlayer().getLocation());
			if (raiding.get(voids.getPlayerTeam(event.getPlayer())).equalsIgnoreCase(teamname)) {
				if (event.getPlayer().hasPermission("teams.admin")) event.setCancelled(false);
				else event.setCancelled(true);
				int i = event.getClickedBlock().getTypeId();
				voids.addToFlags(event.getPlayer(), "INTERACT", event.getClickedBlock().getLocation(), i);
				return;
			}
			if (event.getPlayer().hasPermission("teams.admin")) event.setCancelled(false);
			else event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void entity(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
	       	int secs = this.getConfig().getInt("Pvp-Log");
        	if (secs == 0) return;
        	if (event.getCause().equals(DamageCause.PROJECTILE) || event.getCause().equals(DamageCause.ENTITY_ATTACK) 
        			|| event.getCause().equals(DamageCause.BLOCK_EXPLOSION) 
        			|| event.getCause().equals(DamageCause.ENTITY_EXPLOSION)) {
            	if (!antilog.contains(player.getUniqueId())) {
        	        player.sendMessage(ChatColor.GOLD + "You're now in Combat for " + secs + " second !");
        	        antilog.add(player.getUniqueId());
        	        dura.put(player, (secs * 20L));
        	        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
        	        	@Override
        		        public void run() {
        		            if ((antilog.contains(player.getUniqueId()))) {
    				            antilog.remove(player.getUniqueId());
    				            player.sendMessage(ChatColor.GREEN + "You can now log out safely.");
    				            dura.replace(player, 0L);
        		            }
        		        }
        		    } , secs * 20L);
            	}
        	}
		}
	}
	
	@EventHandler
	public void onAntiLogQuit(PlayerQuitEvent event) {
	    Player p = event.getPlayer(); 
	    if (this.antilog.contains(p.getUniqueId())) {
	    	Bukkit.getServer().broadcastMessage(event.getPlayer().getName() + " has combat logged!");
		    p.damage(2000000000000000000000.0);
	    }
	    teamChatters.remove(p);
	}
	
	@EventHandler
	public void playerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		if (voids.isPlayerInEnemyChunks(player)) {
			if (coolDown.contains(player)) return;
			if (raiding.get(voids.getPlayerTeam(player)).equals(voids.getChunkOwner(loc))) return;
			coolDown.add(player);
			voids.addToFlags(player, "WENT IN ENEMY BASE", loc);
	        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
	        	@Override
		        public void run() {
		            if (coolDown.contains(player)) {
			           coolDown.remove(player);
		            }
		        }
		    } , 150L);
		}
	}
	
	public void send(Player p, String m) {
		p.sendMessage(m);
	}
	
	public String format(String m) {
		return ChatColor.translateAlternateColorCodes('&', m);
	}
	
	public void removeCantRaid(String who, String en) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
        	@Override
	        public void run() {
        		List<String> at = new ArrayList<String>();
        		try {
					at.addAll(cantRaid.get(who));
				} catch (Exception e) {
				}
        		at.remove(en);
        		cantRaid.replace(who, at);
	        }
	    } , 288000L);
	}
	
	public void voidInviteRemove(Player player, String teamname) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
        	@Override
	        public void run() {
        		try {
        			List<String> myList = accept.get(player);
        			myList.remove(teamname);
        			accept.replace(player, myList);
        		}
        		catch (Exception e) {
        			
        		}
	        }
	    } , 1200L);
	}
	
}
