package com.hadenwatne.splatbot.enums;

public enum BotSettingName {
	ALLOW_SETTINGS("Sets the role (other than Administrator) allowed to change bot settings."),
	CREATE_ALERTS("Sets the role (other than Administrator) allowed to create automated server alerts.");

	private final String description;

	BotSettingName(String desc){
		this.description = desc;
	}

	public String getDescription(){
		return description;
	}

	public static boolean contains(String opt){
		for(BotSettingName v : BotSettingName.values()){
			if(v.toString().equalsIgnoreCase(opt)){
				return true;
			}
		}

		return false;
	}
}
