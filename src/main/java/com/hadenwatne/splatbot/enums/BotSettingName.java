package com.hadenwatne.splatbot.enums;

public enum BotSettingName {
	ALLOW_MODIFY("Sets the role (other than Administrator) allowed to use the Modify command."),
	SERVER_LANG("Sets which Lang preset to use on this server.");

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
