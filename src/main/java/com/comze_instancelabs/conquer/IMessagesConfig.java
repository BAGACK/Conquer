package com.comze_instancelabs.conquer;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.config.MessagesConfig;

public class IMessagesConfig extends MessagesConfig {

	public String unbalanced_teams_onlyone = "&4Unbalanced teams! All players selected only one team: <team>";
	public String unbalanced_teams_moreplayers = "&4Unbalanced teams! A team needs more players: <team>";
	public String RED = "&4RED";
	public String BLUE = "§9BLUE";
	
	public String teamselector_title = "TEAM";
	public String teamselector_success = "§2Successfully set team: §4<team>";
	public String teamselector_selectred = "Select the red team.";
	public String teamselector_selectblue = "Select the blue team.";
	public String teamselector_titlered = RED;
	public String teamselector_titleblue = BLUE;

	public IMessagesConfig(JavaPlugin arg0) {
		super(arg0);

		this.getConfig().addDefault("messages.unbalanced_teams_onlyone", this.unbalanced_teams_onlyone);
		this.getConfig().addDefault("messages.unbalanced_teams_moreplayers", this.unbalanced_teams_moreplayers);
		this.getConfig().addDefault("messages.red", this.RED);
		this.getConfig().addDefault("messages.blue", this.BLUE);
		this.getConfig().addDefault("messages.teamselector_title", this.teamselector_title);
		this.getConfig().addDefault("messages.teamselector_success", this.teamselector_success);
		this.getConfig().addDefault("messages.teamselector_selectred", this.teamselector_selectred);
		this.getConfig().addDefault("messages.teamselector_selectblue", this.teamselector_selectblue);
		this.getConfig().addDefault("messages.teamselector_titlered", this.teamselector_titlered);
		this.getConfig().addDefault("messages.teamselector_titleblue", this.teamselector_titleblue);

		this.getConfig().options().copyDefaults(true);
		this.saveConfig();

		this.unbalanced_teams_onlyone = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.unbalanced_teams_onlyone"));
		this.unbalanced_teams_moreplayers = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.unbalanced_teams_moreplayers"));
		this.RED = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.red"));
		this.BLUE = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.blue"));
		this.teamselector_title = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.teamselector_title"));
		this.teamselector_success = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.teamselector_success"));
		this.teamselector_selectred = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.teamselector_selectred"));
		this.teamselector_selectblue = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.teamselector_selectblue"));
		this.teamselector_titlered = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.teamselector_titlered"));
		this.teamselector_titleblue = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.teamselector_titleblue"));
	}
	
	public String getTextFromTeam(String team)
	{
		switch (team)
		{
		case "red":
			return RED;
		case "blue":
			return BLUE;
		}
		return "";
	}

}
