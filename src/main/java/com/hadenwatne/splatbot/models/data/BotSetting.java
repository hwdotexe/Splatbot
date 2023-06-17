package com.hadenwatne.splatbot.models.data;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.enums.BotSettingName;
import com.hadenwatne.splatbot.enums.BotSettingType;
import com.hadenwatne.splatbot.services.DataService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class BotSetting {
	private BotSettingName name;
	private BotSettingType type;
	private String value;
	
	public BotSetting(BotSettingName n, BotSettingType t, String v) {
		name=n;
		type=t;
		value=v;
	}
	
	public BotSettingName getName() {
		return name;
	}
	
	public BotSettingType getType() {
		return type;
	}
	
	public String getAsString() {
		return value;
	}

	public boolean getAsBoolean() {
		if(DataService.IsBoolean(value)) {
			return Boolean.parseBoolean(value);
		}

		return false;
	}

	public int getAsNumber() {
		if(DataService.IsInteger(value)) {
			return Integer.parseInt(value);
		}

		return -1;
	}

	public Role getAsRole(Guild server) {
		if(DataService.IsLong(value)) {
			return server.getRoleById(value);
		}

		return null;
	}

	public TextChannel getAsChannel(Guild server) {
		if(DataService.IsLong(value)) {
			return server.getTextChannelById(value);
		}

		return null;
	}

	public Emoji getAsEmote(Guild server) {
		if(DataService.IsLong(value)) {
			return server.getEmojiById(value);
		}

		return null;
	}
	
	public boolean setValue(String v, Squid b) {
		switch(type) {
		case BOOLEAN:
			if(DataService.IsBoolean(v)) {
				value = v;
				return true;
			}
			
			return false;
		case NUMBER:
			if(DataService.IsInteger(v)) {
				if(Integer.parseInt(v) > -1) {
					value = v;
					return true;
				}
			}
			
			return false;
		case ROLE:
			if(v.equalsIgnoreCase("administrator")) {
				value = v.toLowerCase();
				return true;
			} else {
				if(DataService.IsLong(v)) {
					Role role = App.Splatbot.getJDA().getGuildById(b.getGuildID()).getRoleById(v);

					if (role != null) {
						value = v;
						return true;
					}
				}
				
				return false;
			}
		case CHANNEL:
			if(DataService.IsLong(v)) {
				TextChannel channel = App.Splatbot.getJDA().getGuildById(b.getGuildID()).getTextChannelById(v);

				if (channel != null) {
					value = v;
					return true;
				}
			}
			
			return false;
		case EMOTE:
			if(DataService.IsLong(v)) {
				Emoji emote = App.Splatbot.getJDA().getGuildById(b.getGuildID()).getEmojiById(v);

				if (emote != null) {
					value = v;
					return true;
				}
			}
			
			return false;
		default:
			if(v.length() > 0) {
				value = v.toLowerCase();
				return true;
			} else {
				value = null;
				return false;
			}
		}
	}
}
