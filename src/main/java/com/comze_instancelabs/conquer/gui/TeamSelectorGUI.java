/*
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.comze_instancelabs.conquer.gui;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comze_instancelabs.conquer.IArena;
import com.comze_instancelabs.conquer.Main;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.util.IconMenu;

public class TeamSelectorGUI {

	Main plugin;
	PluginInstance pli;
	public HashMap<String, IconMenu> lasticonm = new HashMap<String, IconMenu>();

	public TeamSelectorGUI(PluginInstance pli, Main plugin) {
		this.plugin = plugin;
		this.pli = pli;
	}

	public void openGUI(final String playername) {
		IconMenu iconm;
		if (lasticonm.containsKey(playername)) {
			iconm = lasticonm.get(playername);
		} else {
			iconm = new IconMenu(this.plugin.msg().teamselector_title, 9, new IconMenu.OptionClickEventHandler() {
				@Override
				public void onOptionClick(IconMenu.OptionClickEvent event) {
					if (event.getPlayer().getName().equalsIgnoreCase(playername)) {
						if (pli.global_players.containsKey(playername)) {
							if (pli.getArenas().contains(pli.global_players.get(playername))) {
								String d = event.getName();
								Player p = event.getPlayer();
								IArena a = (IArena) pli.global_players.get(playername);
								final String team = getTeamFromSelector(d);
								plugin.pteam.put(p.getName(), team);
								p.sendMessage(plugin.msg().teamselector_success.replace("<team>", plugin.msg().getTextFromTeam(team)));
								if (a.checkBalancedTeams() && a.getArenaState() == ArenaState.JOIN && a.getAllPlayers().size() > a.getMinPlayers() - 1)
								{
									a.startLobby();
								}
							}
						}
					}
					event.setWillClose(true);
				}

				private String getTeamFromSelector(String d) {
					if (d.equals(plugin.msg().teamselector_titleblue)) return "blue";
					if (d.equals(plugin.msg().teamselector_titlered)) return "red";
					return null;
				}
			}, plugin);

		}

		iconm.setOption(1, new ItemStack(Material.WOOL, 1, (byte) 14), plugin.msg().teamselector_titlered, plugin.msg().teamselector_selectred);
		iconm.setOption(3, new ItemStack(Material.WOOL, 1, (byte) 11), plugin.msg().teamselector_titleblue, plugin.msg().teamselector_selectblue);

		iconm.open(Bukkit.getPlayerExact(playername));
		lasticonm.put(playername, iconm);
	}

}
