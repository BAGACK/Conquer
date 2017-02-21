package com.comze_instancelabs.conquer;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.util.ArenaScoreboard;

public class IArenaScoreboard extends ArenaScoreboard {

	HashMap<String, Scoreboard> ascore = new HashMap<String, Scoreboard>();
	HashMap<String, Objective> aobjective = new HashMap<String, Objective>();

	JavaPlugin plugin = null;

	public IArenaScoreboard(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public void updateScoreboard(final IArena arena) {
		for (String p_ : arena.getAllPlayers()) {
			Player p = Bukkit.getPlayer(p_);
			if (!ascore.containsKey(arena.getInternalName())) {
				ascore.put(arena.getInternalName(), Bukkit.getScoreboardManager().getNewScoreboard());
			}
			final Scoreboard scoreboard = ascore.get(arena.getInternalName());
			if (!aobjective.containsKey(arena.getInternalName())) {
				aobjective.put(arena.getInternalName(), scoreboard.registerNewObjective(arena.getInternalName(), "dummy"));
			}

			final Objective objective = aobjective.get(arena.getInternalName());
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);

			objective.setDisplayName(MinigamesAPI.getAPI().pinstances.get(plugin).getMessagesConfig().scoreboard_title.replaceAll("<arena>", arena.getDisplayName()));

			reset(scoreboard, Integer.toString(arena.redcp - 1) + " ");
			reset(scoreboard, Integer.toString(arena.redcp + 1) + " ");
			reset(scoreboard, Integer.toString(arena.bluecp - 1) + "  ");
			reset(scoreboard, Integer.toString(arena.bluecp + 1) + "  ");

			get(objective, ChatColor.AQUA + "CHECKPOINTS:").setScore(5);
			get(objective, ChatColor.RED + "RED:").setScore(4);
			get(objective, Integer.toString(arena.redcp) + " ").setScore(3);
			get(objective, ChatColor.BLUE + "BLUE:").setScore(2);
			get(objective, Integer.toString(arena.bluecp) + "  ").setScore(1);
			p.setScoreboard(scoreboard);
		}
	}

	@Override
	public void updateScoreboard(JavaPlugin plugin, final Arena arena) {
		IArena a = (IArena) MinigamesAPI.getAPI().pinstances.get(plugin).getArenaByName(arena.getInternalName());
		this.updateScoreboard(a);
	}

	@Override
	public void removeScoreboard(String arena, Player p) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard sc = manager.getNewScoreboard();
		sc.clearSlot(DisplaySlot.SIDEBAR);
		p.setScoreboard(sc);
	}

}
