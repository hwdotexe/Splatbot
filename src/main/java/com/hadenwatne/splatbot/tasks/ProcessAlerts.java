package com.hadenwatne.splatbot.tasks;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commands.Challenge;
import com.hadenwatne.splatbot.commands.Command;
import com.hadenwatne.splatbot.commands.SplatfestCmd;
import com.hadenwatne.splatbot.enums.AlertType;
import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.models.data.ConfiguredAlert;
import com.hadenwatne.splatbot.models.data.Squid;
import com.hadenwatne.splatbot.models.gameData.SplatoonStageData;
import com.hadenwatne.splatbot.services.LoggingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.*;

public class ProcessAlerts extends TimerTask {
	public ProcessAlerts() {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();

		c.setTime(new Date());

		// Run this now, and then again every 1 hour
		t.schedule(this, c.getTime(), (60 * 60 * 1000));
	}

	public void run() {
		if(App.Splatbot.getStageData() != null) {
			SplatoonStageData stageData = App.Splatbot.getStageData();

			for (Squid squid : App.Splatbot.getStorageService().getSquidController().getSquids()) {
				for (ConfiguredAlert alert : squid.getAlerts()) {
					if(alert.getType() == AlertType.SPLATFEST) {
						String splatfestID = stageData.getSplatfests().US.data.festRecords.nodes.get(0).id;

						if (squid.getPreviousAlert().containsKey(alert.getType())) {
							if (squid.getPreviousAlert().get(alert.getType()).equals(splatfestID)) {
								continue;
							}
						}

						Guild guild = App.Splatbot.getJDA().getGuildById(squid.getGuildID());

						try {
							TextChannel channel = guild.getTextChannelById(alert.getChannel());
							List<EmbedBuilder> stageList = getSplatfestCommand().BuildStageList(alert.getTimezone(), App.Splatbot.getLanguageService().getDefaultLang(), false);
							List<MessageEmbed> embeds = stageList.stream().map(EmbedBuilder::build).toList();

							channel.sendMessageEmbeds(embeds).queue();

							squid.getPreviousAlert().put(alert.getType(), splatfestID);
						} catch (Exception ignored){

						}

						continue;
					}

					if(alert.getType() == AlertType.CHALLENGE) {
						String challengeID = stageData.getRegular().data.eventSchedules.nodes.get(0).leagueMatchSetting.leagueMatchEvent.id;

						if (squid.getPreviousAlert().containsKey(alert.getType())) {
							if (squid.getPreviousAlert().get(alert.getType()).equals(challengeID)) {
								continue;
							}
						}

						Guild guild = App.Splatbot.getJDA().getGuildById(squid.getGuildID());

						try {
							TextChannel channel = guild.getTextChannelById(alert.getChannel());
							List<EmbedBuilder> stageList = getChallengeCommand().BuildStageList(alert.getTimezone(), App.Splatbot.getLanguageService().getDefaultLang(), false);
							List<MessageEmbed> embeds = stageList.stream().map(EmbedBuilder::build).toList();

							channel.sendMessageEmbeds(embeds).queue();

							squid.getPreviousAlert().put(alert.getType(), challengeID);
						} catch (Exception ignored){

						}

						continue;
					}
				}
			}
		}

		LoggingService.Log(LogType.SYSTEM, "Processed alerts");
	}

	private SplatfestCmd getSplatfestCommand() {
		for(Command c : App.Splatbot.getCommandHandler().getLoadedCommands()) {
			if(c.getCommandStructure().getName().equals("splatfest")) {
				return (SplatfestCmd) c;
			}
		}

		return null;
	}

	private Challenge getChallengeCommand() {
		for(Command c : App.Splatbot.getCommandHandler().getLoadedCommands()) {
			if(c.getCommandStructure().getName().equals("challenge")) {
				return (Challenge) c;
			}
		}

		return null;
	}
}