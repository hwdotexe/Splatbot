package com.hadenwatne.splatbot.tasks;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commands.*;
import com.hadenwatne.splatbot.enums.HTTPVerb;
import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.enums.PostType;
import com.hadenwatne.splatbot.models.data.Squid;
import com.hadenwatne.splatbot.models.data.StickyPost;
import com.hadenwatne.splatbot.models.data.stages.*;
import com.hadenwatne.splatbot.services.DataService;
import com.hadenwatne.splatbot.services.HTTPService;
import com.hadenwatne.splatbot.services.LoggingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.rmi.runtime.Log;

import java.util.*;

public class RefreshStickyPosts {
	public RefreshStickyPosts() {
		Timer t = new Timer();

		// Run this in 30 seconds, and then each time it runs, schedule it to run at the next turfwar end.
		t.schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				runTimer();
			}
		}, 10*1000);
	}

	public void rescheduleTimer(Date execTime){
		Timer t = new Timer();

		t.schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				runTimer();
			}
		}, execTime);
	}

	public void runTimer() {
		// Schedule the next run of this task based on Turf War since it changes every 2 hours.
		Date nextRun = DataService.ParseDate(App.Splatbot.getStageData().getTurfWar().get(0).getEndTime());
		Calendar c = Calendar.getInstance();

		c.setTime(nextRun);
		c.add(Calendar.SECOND, 5);

		// For each squid, check their sticky posts and execute the command using object data
		for(Squid squid : App.Splatbot.getStorageService().getSquidController().getSquids()) {
			Guild guild = App.Splatbot.getJDA().getGuildById(squid.getGuildID());

			for(StickyPost stickyPost : squid.getStickyPosts()) {
				try {
					TextChannel channel = guild.getTextChannelById(stickyPost.getChannelID());
					channel.retrieveMessageById(stickyPost.getMessageID()).queue(message -> {
						EmbedBuilder stages;

						if(stickyPost.getType() == PostType.TURF_WAR) {
							stages = getTurfWarCommand().BuildStageList(stickyPost.getTimezone(), App.Splatbot.getLanguageService().getDefaultLang(), true);
						} else if(stickyPost.getType() == PostType.ANARCHY) {
							stages = getAnarchyCommand().BuildStageList(stickyPost.getTimezone(), App.Splatbot.getLanguageService().getDefaultLang(), true);
						} else if(stickyPost.getType() == PostType.X_BATTLES) {
							stages = getXBattlesCommand().BuildStageList(stickyPost.getTimezone(), App.Splatbot.getLanguageService().getDefaultLang(), true);
						} else if(stickyPost.getType() == PostType.CHALLENGE) {
							stages = getChallengeCommand().BuildStageList(stickyPost.getTimezone(), App.Splatbot.getLanguageService().getDefaultLang(), true);
						} else{
							stages = getSalmonRunCommand().BuildStageList(stickyPost.getTimezone(), App.Splatbot.getLanguageService().getDefaultLang(), true);
						}

						message.editMessageEmbeds(stages.build()).queue();
					});
				} catch (Exception e) {
					squid.getStickyPosts().remove(stickyPost);
					LoggingService.LogException(e);
				}
			}
		}

		rescheduleTimer(c.getTime());

		LoggingService.Log(LogType.SYSTEM, "Sticky post refresh task ran.");
	}

	private TurfWar getTurfWarCommand() {
		for(Command c : App.Splatbot.getCommandHandler().getLoadedCommands()) {
			if(c.getCommandStructure().getName().equals("turfwar")) {
				return (TurfWar)c;
			}
		}

		return null;
	}

	private Anarchy getAnarchyCommand() {
		for(Command c : App.Splatbot.getCommandHandler().getLoadedCommands()) {
			if(c.getCommandStructure().getName().equals("anarchy")) {
				return (Anarchy) c;
			}
		}

		return null;
	}

	private SalmonRun getSalmonRunCommand() {
		for(Command c : App.Splatbot.getCommandHandler().getLoadedCommands()) {
			if(c.getCommandStructure().getName().equals("salmonrun")) {
				return (SalmonRun) c;
			}
		}

		return null;
	}

	private XBattles getXBattlesCommand() {
		for(Command c : App.Splatbot.getCommandHandler().getLoadedCommands()) {
			if(c.getCommandStructure().getName().equals("xbattles")) {
				return (XBattles) c;
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