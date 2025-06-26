package com.hadenwatne.splatbot.models.data;

import net.dv8tion.jda.api.entities.Activity.ActivityType;

import java.util.HashMap;

public class GiantSquid {
	private HashMap<String, ActivityType> statuses;
	private HashMap<String, Integer> commandStats;
	private String botAPIKey;
	private String botAPIKeySecondary;
	private String botAdminID;
	private boolean updateDiscordSlashCommands;

	public GiantSquid() {
		this.statuses = new HashMap<String, ActivityType>();
		this.commandStats = new HashMap<String, Integer>();
		this.botAPIKey = "API_KEY_HERE";
		this.botAPIKeySecondary = "API_KEY_HERE";
		this.botAdminID = "Bot_Admin_Discord_User_ID";
		this.updateDiscordSlashCommands = false;
	}
	
	public HashMap<String, ActivityType> getStatuses(){
		return statuses;
	}
	
	public HashMap<String, Integer> getCommandStats(){
		if(commandStats == null)
			 commandStats = new HashMap<String, Integer>();
		
		return commandStats;
	}

	public String getBotAdminID() {
		if(this.botAdminID == null) {
			this.botAdminID = "Bot_Admin_Discord_User_ID";
		}

		return this.botAdminID;
	}

	public String getBotAPIKey(){
		if(botAPIKey == null)
			botAPIKey = "API_KEY_HERE";

		return botAPIKey;
	}

	public String getBotAPIKeySecondary() {
		if(botAPIKeySecondary == null)
			botAPIKeySecondary = "API_KEY_HERE";

		return botAPIKeySecondary;
	}

	public boolean doUpdateDiscordSlashCommands() {
		return this.updateDiscordSlashCommands;
	}

	public void setUpdateDiscordSlashCommands(boolean update) {
		this.updateDiscordSlashCommands = update;
	}

	public void loadDefaults() {
		statuses.put("Turf War", ActivityType.PLAYING);
	}
}
