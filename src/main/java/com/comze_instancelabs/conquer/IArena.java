package com.comze_instancelabs.conquer;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaConfigStrings;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.ArenaType;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;

public class IArena extends Arena {
	
	public static final String TEAM_RED = "red";
	public static final String TEAM_BLUE = "blue";

	public static Main m;

	int blue = 0;
	int red = 0;

	int bluecp = 0;
	int redcp = 0;

	int winscore = 50;

	boolean cteam = true;

	int checkpoint_y_check = 100;

	ArrayList<CheckPoint> cps = new ArrayList<CheckPoint>();

	public IArena(Main m, String arena_id) {
		super(m, arena_id, ArenaType.DEFAULT);
		this.m = m;
		this.loadCheckpoints();
	}

	public boolean addBluePoints() {
		blue++;
		/*
		 * if (blue > 100) { for (String p_ : this.getAllPlayers()) { if (m.pteam.containsKey(p_)) { if (m.pteam.get(p_).equalsIgnoreCase(IArena.TEAM_RED)) {
		 * MinigamesAPI.getAPI().pinstances.get(m).global_lost.put(p_, this); } } } this.stop(); return true; }
		 */
		return false;
	}

	public boolean addRedPoints() {
		red++;
		/*
		 * if (red > 100) { for (String p_ : this.getAllPlayers()) { if (m.pteam.containsKey(p_)) { if (m.pteam.get(p_).equalsIgnoreCase(IArena.TEAM_BLUE)) {
		 * MinigamesAPI.getAPI().pinstances.get(m).global_lost.put(p_, this); } } } this.stop(); return true; }
		 */
		return false;
	}

	@Override
	public void joinPlayerLobby(String playername) {
		Bukkit.getScheduler().runTaskLater(m, new Runnable() {
			public void run() {
				Player p = Bukkit.getPlayer(playername);
				if (p != null) {
					if (m.pli.global_players.containsKey(p.getName())) {
						ItemStack teamselector = new ItemStack(Material.WOOL, 1, (byte) 14);
						ItemMeta itemm = teamselector.getItemMeta();
						itemm.setDisplayName(ChatColor.RED + "Team");
						teamselector.setItemMeta(itemm);
						p.getInventory().setItem(4, teamselector);
						p.updateInventory();
					}
				}
			}
		}, 25L);
		
		super.joinPlayerLobby(playername);
		
		if (cteam) {
			m.pteam.put(playername, IArena.TEAM_RED);
			cteam = false;
		} else {
			m.pteam.put(playername, IArena.TEAM_BLUE);
			cteam = true;
		}
		this.checkBalancedTeams();
	}
	
	public int redTeamCount()
	{
		return (int) m.pteam.entrySet().stream().filter(t -> this.getAllPlayers().contains(t.getKey())).filter(t -> t.getValue().equals(TEAM_RED)).count();
	}
	
	public int blueTeamCount()
	{
		return (int) m.pteam.entrySet().stream().filter(t -> this.getAllPlayers().contains(t.getKey())).filter(t -> t.getValue().equals(TEAM_BLUE)).count();
	}

	public boolean checkBalancedTeams() {
		// check for only one team
		if (this.redTeamCount() == 0 && this.blueTeamCount() > 1)
		{
			sendOnlyOneTeamMsg("blue");
			return false;
		}
		if (this.redTeamCount() > 1 && this.blueTeamCount() == 0)
		{
			sendOnlyOneTeamMsg("red");
			return false;
		}
		
		// check for misbalanced teams
		int max = Math.max(this.redTeamCount(), this.blueTeamCount());
		if (this.redTeamCount() > 0 && this.redTeamCount() < max - 1)
		{
			sendMisbalancedTeamMsg("red");
			return false;
		}
		if (this.blueTeamCount() > 0 && this.blueTeamCount() < max - 1)
		{
			sendMisbalancedTeamMsg("blue");
			return false;
		}
		
		return true;
	}

	private void sendOnlyOneTeamMsg(final String team) {
		for (final String p_2 : this.getAllPlayers())
		{
		    if (Validator.isPlayerOnline(p_2))
		    {
		        final Player p2 = Bukkit.getPlayer(p_2);
		        p2.sendMessage(m.msg().unbalanced_teams_onlyone.replace("<team>", m.msg().getTextFromTeam(team)));
		    }
		}
	}

	private void sendMisbalancedTeamMsg(final String team) {
		for (final String p_2 : this.getAllPlayers())
		{
		    if (Validator.isPlayerOnline(p_2))
		    {
		        final Player p2 = Bukkit.getPlayer(p_2);
		        p2.sendMessage(m.msg().unbalanced_teams_moreplayers.replace("<team>", m.msg().getTextFromTeam(team)));
		    }
		}
	}
	
	@Override
	protected void onLobbyCountdownComplete()
    {
		if (checkBalancedTeams())
		{
			super.onLobbyCountdownComplete();
		}
		else
		{
			this.abortStarting();
		}
    }

	@Override
	public void leavePlayer(String p_, boolean arg1, boolean arg2) {
		m.pteam.remove(p_);
		super.leavePlayer(p_, arg1, arg2);
		if (this.getArenaState() == ArenaState.STARTING)
		{
			this.checkBalancedTeams();
		}
		else if (this.getArenaState() == ArenaState.JOIN)
		{
			if (this.checkBalancedTeams() && this.getAllPlayers().size() > this.getMinPlayers() - 1)
			{
				this.startLobby();
			}
		}
	}

	@Override
	public void spectate(String playername) {
		//
	}

	BukkitTask tt;
	int currentingamecount;

	@Override
	public void start(boolean tp) {
		red = 0;
		blue = 0;
		final IArena a = this;

		for (String p_ : a.getArena().getAllPlayers()) {
			Player p = Bukkit.getPlayer(p_);
			if (m.pteam.get(p_).equalsIgnoreCase(IArena.TEAM_RED)) {
				Util.teleportPlayerFixed(p, a.getSpawns().get(0));
			} else if (m.pteam.get(p_).equalsIgnoreCase(IArena.TEAM_BLUE)) {
				Util.teleportPlayerFixed(p, a.getSpawns().get(1));
			}
		}

		checkpoint_y_check = m.getConfig().getInt("config.checkpoint_register_y_axis");

		super.start(false);

		tt = Bukkit.getScheduler().runTaskTimer(m, new Runnable() {
			public void run() {
				ArrayList<String> temp = new ArrayList<String>(a.getAllPlayers());
				for (String p_ : temp) {
					Player p = Bukkit.getPlayer(p_);
					if (p != null) {
						CheckPoint c = isInCP(p);
						if (c != null) {
							c.evaluate(p.getName(), m.pteam.get(p.getName()));
							updateBeacons();
						}
					}
				}
			}
		}, 5L, 5L);

		m.scoreboard.updateScoreboard(this);
	}

	@Override
	public void started() {
		final IArena a = this;
		Bukkit.getScheduler().runTaskLater(m, new Runnable() {
			public void run() {
				for (String p_ : a.getAllPlayers()) {
					m.addGear(p_);
				}
			}
		}, 20L);
		updateBeacons();
	}

	public void updateBeacons() {
		for (CheckPoint cp : this.cps) {
			byte col = (byte) 0;
			if (cp.wasred) {
				col = (byte) 14;
			} else if (cp.wasblue) {
				col = (byte) 11;
			}
			for (String p_ : this.getAllPlayers()) {
				Player p = Bukkit.getPlayer(p_);
				if (p != null) {
					spawnBeacon(p, cp.getCenter().clone(), col);
				}
			}
		}
	}

	public void spawnBeacon(Player player, Location loc, byte col) {
		player.sendBlockChange(loc.clone(), Material.STAINED_GLASS, col);
		player.sendBlockChange(loc.clone().add(0D, -1D, 0D), Material.BEACON, (byte) 0);
		for (int x = -1; x < 2; x++) {
			for (int z = -1; z < 2; z++) {
				try {
					player.sendBlockChange(loc.clone().add(x, -2D, z), Material.DIAMOND_BLOCK, (byte) 0);
				} catch (Exception e) {
					MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
				}
			}
		}
	}

	@Override
	public void stop() {
		if (tt != null) {
			tt.cancel();
		}
		updateBeacons();
		super.stop();
		resetCPs();
		this.redcp = 0;
		this.bluecp = 0;
		this.red = 0;
		this.blue = 0;
	}

	public void loadCheckpoints() {
		FileConfiguration config = MinigamesAPI.getAPI().pinstances.get(m).getArenasConfig().getConfig();
		if (config.isSet(ArenaConfigStrings.ARENAS_PREFIX + this.getName() + ".checkpoints.")) {
			for (String cp : config.getConfigurationSection(ArenaConfigStrings.ARENAS_PREFIX + this.getName() + ".checkpoints.").getKeys(false)) {
				CheckPoint cp_ = new CheckPoint(m, this, Util.getComponentForArena(m, this.getName(), "checkpoints." + cp));
				cps.add(cp_);
			}
		}
	}

	public CheckPoint isInCP(Player p) {
		for (CheckPoint cp : this.cps) {
			Location l = p.getLocation();
			Location l2 = cp.getCenter();
			if ((Math.abs(l.getBlockX() - l2.getBlockX()) < 3) && (Math.abs(l.getBlockZ() - l2.getBlockZ()) < 3) && Math.abs(l.getBlockY() - l2.getBlockY()) < checkpoint_y_check) {
				return cp;
			}
		}
		return null;
	}

	public void resetCPs() {
		for (CheckPoint cp : cps) {
			Location l = cp.getCenter();
			for (int x = -2; x < 3; x++) {
				for (int z = -2; z < 3; z++) {
					l.clone().add(x, 0D, z).getBlock().setTypeIdAndData(35, (byte) 0, true);
				}
			}
			cp.red = 0;
			cp.blue = 0;
			cp.cx_b = -2;
			cp.cz_b = -2;
			cp.cx_r = -2;
			cp.cz_r = -2;
			cp.wasblue = false;
			cp.wasred = false;
		}
	}

}
