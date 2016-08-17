package com.comze_instancelabs.conquer;

import org.bukkit.Color;
import org.bukkit.Location;

public class CheckPoint {

	private IArena a;
	private Main plugin;

	Location center;

	int red = 0;
	int blue = 0;

	int cx_r = -2;
	int cz_r = -2;

	int cx_b = -2;
	int cz_b = -2;

	boolean wasred = false;
	boolean wasblue = false;

	boolean lastred = false;
	boolean lastblue = false;

	public CheckPoint(Main plugin, IArena a, Location center) {
		this.a = a;
		this.plugin = plugin;
		this.center = center;
	}

	public Location getCenter() {
		return this.center;
	}

	public void evaluate(String conquerer, String team) {
		if (team.equalsIgnoreCase("red")) {
			// check if current block is blue, decrease blue too
			byte b = getDataAtCurrentLoc("red");
			if (b == 14) { // red
				// red already (that shouldn't happen)
				// log("red already there?");
				// cx_r = -2;
				// cz_r = -2;
				getNextLoc("red");
				return;
			} else if (b == 11) { // blue
				lastred = false;
				if (!lastblue) {
					cx_b = cx_r;
					cz_b = cz_r;
					lastblue = true;
				}
				blue--;
			} else { // white

			}

			red++;

			setDataAtCurrentLoc("red");

			if (red > 24) {
				// check if it was blue before, if yes -> decrease blue cp
				if (wasred) {
					return;
				}
				wasred = true;
				if (wasblue) {
					wasblue = false;
					a.bluecp--;
				}
				a.redcp++;
				if (plugin.pconqueredcps.containsKey(conquerer)) {
					plugin.pconqueredcps.put(conquerer, plugin.pconqueredcps.get(conquerer) + 1);
				} else {
					plugin.pconqueredcps.put(conquerer, 1);
				}
				int temp = 0;
				if (plugin.getConfig().isSet("tempconquers." + conquerer)) {
					temp = plugin.getConfig().getInt("tempconquers." + conquerer);
				}
				plugin.getConfig().set("tempconquers." + conquerer, temp + 1);
				plugin.saveConfig();
				if (plugin.getConfig().getInt("tempconquers." + conquerer) > 99) {
					plugin.pli.getArenaAchievements().setAchievementDone(conquerer, "capture_hundred_checkpoints_all_time", false);
				}
				if (plugin.pconqueredcps.get(conquerer) > 2) {
					plugin.pli.getArenaAchievements().setAchievementDone(conquerer, "capture_three_checkpoints_in_a_game", false);
				}
				plugin.spawnFirework(this.getCenter(), Color.RED);
				if (a.redcp > plugin.getAllCheckPoints(a.getName()) - 1) {
					for (String p_ : a.getAllPlayers()) {
						if (plugin.pteam.get(p_).equalsIgnoreCase("blue")) {
							plugin.pli.global_lost.put(p_, a);
						}
					}
					a.stop();
				}
				plugin.scoreboard.updateScoreboard(a);
			}
			getNextLoc("red");
		} else {
			// check if current block is blue, decrease blue too
			byte b = getDataAtCurrentLoc("blue");
			if (b == 11) { // blue
				// red already (that shouldn't happen)
				// log.println("blue already there?");
				// cx_b = -2;
				// cz_b = -2;
				getNextLoc("blue");
				return;
			} else if (b == 14) { // red
				lastblue = false;
				if (!lastred) {
					cx_r = cx_b;
					cz_r = cz_b;
					lastred = true;
				}
				red--;
			} else { // white

			}

			blue++;

			setDataAtCurrentLoc("blue");

			if (blue > 24) {
				// check if it was red before, if yes -> decrease red cp
				if (wasblue) {
					return;
				}
				wasblue = true;
				if (wasred) {
					wasred = false;
					a.redcp--;
				}
				a.bluecp++;
				if (plugin.pconqueredcps.containsKey(conquerer)) {
					plugin.pconqueredcps.put(conquerer, plugin.pconqueredcps.get(conquerer) + 1);
				} else {
					plugin.pconqueredcps.put(conquerer, 1);
				}
				int temp = 0;
				if (plugin.getConfig().isSet("tempconquers." + conquerer)) {
					temp = plugin.getConfig().getInt("tempconquers." + conquerer);
				}
				plugin.getConfig().set("tempconquers." + conquerer, temp + 1);
				plugin.saveConfig();
				if (plugin.getConfig().getInt("tempconquers." + conquerer) > 99) {
					plugin.pli.getArenaAchievements().setAchievementDone(conquerer, "capture_hundred_checkpoints_all_time", false);
				}
				if (plugin.pconqueredcps.get(conquerer) > 2) {
					plugin.pli.getArenaAchievements().setAchievementDone(conquerer, "capture_three_checkpoints_in_a_game", false);
				}
				plugin.spawnFirework(this.getCenter(), Color.BLUE);
				if (a.bluecp > plugin.getAllCheckPoints(a.getName()) - 1) {
					for (String p_ : a.getAllPlayers()) {
						if (plugin.pteam.get(p_).equalsIgnoreCase("red")) {
							plugin.pli.global_lost.put(p_, a);
						}
					}
					a.stop();
				}
				plugin.scoreboard.updateScoreboard(a);
			}
			getNextLoc("blue");
		}
	}

	public byte getDataAtCurrentLoc(String team) {
		if (team.equalsIgnoreCase("red")) {
			return center.clone().add(cx_r, 0, cz_r).getBlock().getData();
		} else {
			return center.clone().add(cx_b, 0, cz_b).getBlock().getData();
		}
	}

	public void setDataAtCurrentLoc(String team) {
		if (team.equalsIgnoreCase("red")) {
			center.clone().add(cx_r, 0, cz_r).getBlock().setData((byte) 14);
		} else {
			center.clone().add(cx_b, 0, cz_b).getBlock().setData((byte) 11);
		}
	}

	public int[] getNextLoc(String team) {
		if (team.equalsIgnoreCase("red")) {
			int[] ret = new int[2];
			if (cx_r > 1) {
				if (cz_r > 1) {
					// END
				} else {
					cz_r++;
				}
				cx_r = -2;
			} else {
				cx_r++;
			}

			ret[0] = cx_r;
			ret[1] = cz_r;
			return ret;
		} else {
			int[] ret = new int[2];
			if (cx_b > 1) {
				if (cz_b > 1) {
					// END
				} else {
					cz_b++;
				}
				cx_b = -2;
			} else {
				cx_b++;
			}

			ret[0] = cx_b;
			ret[1] = cz_b;
			return ret;
		}

	}

}
