package com.hadenwatne.splatbot.models.data;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.enums.BotSettingName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Squid {
	private final String guildID;
	private final List<BotSetting> settings;
	private boolean sentWelcome;

	public Squid(String gid) {
		guildID = gid;
		settings = new ArrayList<>();
		sentWelcome = false;
		
		loadFirstRunDefaults();
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