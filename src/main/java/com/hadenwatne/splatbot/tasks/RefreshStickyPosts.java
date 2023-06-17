package com.hadenwatne.splatbot.tasks;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commands.Anarchy;
import com.hadenwatne.splatbot.commands.Command;
import com.hadenwatne.splatbot.commands.SalmonRun;
import com.hadenwatne.splatbot.commands.TurfWar;
import com.hadenwatne.splatbot.enums.HTTPVerb;
import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.enums.PostType;
import com.hadenwatne.splatbot.models.data.Squid;
import com.hadenwatne.splatbot.models.data.StickyPost;
import com.hadenwatne.splatbot.models.data.stages.*;
import com.hadenwatne.splatbot.services.HTTPService;
import com.hadenwatne.splatbot.services.LoggingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.rmi.runtime.Log;

import java.util.*;

public class RefreshStickyPosts extends TimerTask {
	public RefreshStickyPosts() {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();

		c.setTime(new Date());

		// Run this now, and then again every 2 hours
//		t.schedule(this, c.getTime(), 2 * (60 * 60 * 1000));
		t.schedule(this, c.getTime(), (60 * 1000));
	}

	public void run() {
		// For each squid, check their sticky posts and execute the command using object data
		for(Squid squid : App.Splatbot.getStorageService().getSquidController().getSquids()) {
			Guild guild = App.Splatbot.getJDA().getGuildById(squid.getGuildID());

			for(StickyPost stickyPost : squid.getStickyPosts()) {
				try {
					TextChannel channel = guild.getTextChannelById(stickyPost.getChannelID());
					channel.retrieveMessageById(stickyPost.getMessageID()).queue(message -> {
						EmbedBuilder stages;

						if(stickyPost.getType() == PostType.TURF_WAR) {
							stages = getTurfWarCommand().BuildStageList(stickyPost.getTimezone(), App.Splatbot.getLanguageService().getDefaultLang());
						} else if(stickyPost.getType() == PostType.ANARCHY) {
							stages = getAnarchyCommand().BuildStageList(stickyPost.getTimezone(), App.Splatbot.getLanguageService().getDefaultLang());
						} else{
							stages = getSalmonRunCommand().BuildStageList(stickyPost.getTimezone(), App.Splatbot.getLanguageService().getDefaultLang());
						}

						message.editMessageEmbeds(stages.build()).queue();
					});
				} catch (Exception e) {
					squid.getStickyPosts().remove(stickyPost);
					LoggingService.LogException(e);
				}
			}
		}

		LoggingService.Log(LogType.SYSTEM, "Sticky post refresh task ran");
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
}