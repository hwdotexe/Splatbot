package com.hadenwatne.splatbot.models.data;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.enums.BotSettingName;
import com.hadenwatne.splatbot.enums.PostType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Squid {
	private final String guildID;
	private final List<BotSetting> settings;
	private boolean sentWelcome;
	private HashMap<Long, String> userTimezones;
	private List<StickyPost> stickyPosts;

	public Squid(String gid) {
		guildID = gid;
		settings = new ArrayList<>();
		sentWelcome = false;
		userTimezones = new HashMap<>();
		stickyPosts = new ArrayList<>();

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