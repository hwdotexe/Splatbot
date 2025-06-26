package com.hadenwatne.splatbot.models.data;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.enums.AlertType;
import com.hadenwatne.splatbot.enums.BotSettingName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Squid {
	private final String guildID;
	private final List<BotSetting> settings;
	private boolean sentWelcome;
	private HashMap<Long, String> userTimezones;
	private List<StickyPost> stickyPosts;
	private List<ConfiguredAlert> alerts;
	private HashMap<AlertType, String> previousAlert;

	public Squid(String gid) {
		guildID = gid;
		settings = new ArrayList<>();
		sentWelcome = false;
		userTimezones = new HashMap<>();
		stickyPosts = new ArrayList<>();
		alerts = new ArrayList<>();
		previousAlert = new HashMap<>();

		loadFirstRunDefaults();
	}

	public List<StickyPost> getStickyPosts() {
		if (this.stickyPosts == null) {
			this.stickyPosts = new ArrayList<>();
		}

		return stickyPosts;
	}

	public HashMap<Long, String> getUserTimezones() {
		if(userTimezones == null) {
			userTimezones = new HashMap<>();
		}

		return userTimezones;
	}

	public List<ConfiguredAlert> getAlerts() {
		if(alerts == null) {
			alerts = new ArrayList<>();
		}

		return alerts;
	}

	public HashMap<AlertType, String> getPreviousAlert() {
		if(previousAlert == null) {
			previousAlert = new HashMap<>();
		}

		return previousAlert;
	}

	public boolean didSendWelcome(){
		return sentWelcome;
	}

	public void setSentWelcome(){
		sentWelcome = true;
	}
	
	public String getGuildID() {
		return guildID;
	}
	
	public List<BotSetting> getSettings(){
		return settings;
	}
	
	public BotSetting getSettingFor(BotSettingName n) {
		for(BotSetting s : settings) {
			if(s.getName() == n) {
				return s;
			}
		}
		
		return null;
	}

	public void loadFirstRunDefaults() {
		settings.addAll(App.Splatbot.getStorageService().getDefaultSettings());
	}
}