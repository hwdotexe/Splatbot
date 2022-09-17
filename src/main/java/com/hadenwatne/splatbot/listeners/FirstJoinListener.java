package com.hadenwatne.splatbot.listeners;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.enums.BotSettingName;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.factories.EmbedFactory;
import com.hadenwatne.splatbot.models.data.BotSetting;
import com.hadenwatne.splatbot.models.data.Squid;
import com.hadenwatne.splatbot.services.LoggingService;
import com.hadenwatne.splatbot.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class FirstJoinListener extends ListenerAdapter {
	final EmbedBuilder welcomeMessage;

	public FirstJoinListener() {
		EmbedBuilder embedBuilder = EmbedFactory.GetEmbed(EmbedType.INFO, "Welcome");
		String name = App.Splatbot.getBotName();

		embedBuilder.setThumbnail(App.Splatbot.getBotAvatarUrl());
		embedBuilder.setDescription("Woomy! "+name+" has joined the server!");

		embedBuilder.addField(":bulb: Get Started", "To view a list of commands and general information, use `"+name+" help`.\n" +
				"\n" +
				"• Adjust settings and permissions with `"+name+" modify`", false);

		welcomeMessage = embedBuilder;
	}

	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Squid squid = App.Splatbot.getStorageService().getSquid(e.getGuild().getId());

		// Check a setting in the Brain, since this event can fire accidentally if Discord screws up.
		if(!squid.didSendWelcome()){
			TextChannel defaultChannel = e.getGuild().getSystemChannel();

			sendWelcomeMessage(squid, defaultChannel);
			setDefaultChannelSettings(squid, defaultChannel);
		}
	}

	private void sendWelcomeMessage(Squid squid, TextChannel channel) {
		try {
			MessageService.SendMessage(channel, welcomeMessage, false);
		} catch (InsufficientPermissionException e) {
			LoggingService.Log(LogType.ERROR, "Could not send a welcome message to a server.");
		}

		squid.setSentWelcome();
	}

	private void setDefaultChannelSettings(Squid squid, BaseGuildMessageChannel channel) {
		if(channel != null){
			BotSetting pin = squid.getSettingFor(BotSettingName.PIN_CHANNEL);

			pin.setValue(channel.getName(), squid);
		}
	}
}