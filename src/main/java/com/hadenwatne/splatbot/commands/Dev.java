package com.hadenwatne.splatbot.commands;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commandbuilder.CommandBuilder;
import com.hadenwatne.splatbot.commandbuilder.CommandParameter;
import com.hadenwatne.splatbot.commandbuilder.CommandStructure;
import com.hadenwatne.splatbot.commandbuilder.ParameterType;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.data.GiantSquid;
import com.hadenwatne.splatbot.models.data.Squid;
import com.hadenwatne.splatbot.services.DataService;
import com.hadenwatne.splatbot.services.LoggingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This command is not exposed to users by default, and is here only for the benefit of the bot developer.
 * The goal is to provide easy-access commands in the event of bot maintenance, or to gauge which bot features
 * are used most.
 */
public class Dev extends Command {
	public Dev() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("dev", "")
				.addAlias("developer")
				.addParameters(
						new CommandParameter("subCommand", "The subcommand to run", ParameterType.SELECTION)
								.addSelectionOptions("addstatus")
								.addSelectionOptions("getguilds")
								.addSelectionOptions("getcommandstats")
								.addSelectionOptions("clearcommandstats")
								.addSelectionOptions("leave")
								.addSelectionOptions("savebrains")
								.setExample("addstatus"),
						new CommandParameter("data", "The optional data to send to the subcommand", ParameterType.STRING, false)
								.setExample("stuff")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		if (executingCommand.getChannel() instanceof PrivateChannel) {
			if (executingCommand.getAuthorUser().getId().equals(App.Splatbot.getStorageService().getGiantSquid().getBotAdminID())) {
				String subCommand = executingCommand.getCommandArguments().getAsString("subCommand");
				String commandData = executingCommand.getCommandArguments().getAsString("data");

				switch (subCommand.toLowerCase()) {
					case "addstatus":
						return addStatus(commandData);
					case "getguilds":
						return getGuilds();
					case "getcommandstats":
						return getCommandStats();
					case "clearcommandstats":
						clearCommandStats();

						return response(EmbedType.INFO)
								.setDescription("Command statistics cleared!");
					case "leave":
						leave(commandData);

						return response(EmbedType.INFO)
								.setDescription(App.Splatbot.getBotName()+" is queued to leave the server!");
					case "savebrains":
						saveBrains();

						return response(EmbedType.INFO)
								.setDescription("All brains were saved!");
					default:
						return response(EmbedType.ERROR)
								.setDescription("That command wasn't recognized!");
				}
			} else {
				return response(EmbedType.ERROR)
						.setDescription("You cannot use the Developer command! This is used for bot maintenance tasks, and is restricted " +
								"to the bot developer.");
			}
		}

		return null;
	}

	private EmbedBuilder addStatus(String args) {
		Matcher m = Pattern.compile("^([a-z]+)\\s(.+)$", Pattern.CASE_INSENSITIVE).matcher(args);

		if (m.find()) {
			try {
				GiantSquid b = App.Splatbot.getStorageService().getGiantSquid();
				ActivityType type = ActivityType.valueOf(m.group(1).toUpperCase());
				String msg = m.group(2);

				b.getStatuses().put(msg, type);
				App.Splatbot.getJDA().getPresence().setActivity(Activity.of(type, msg));
				App.Splatbot.getStorageService().getSquidController().saveGiantSquid();

				return response(EmbedType.INFO)
						.setDescription("New status added!");
			} catch (Exception e) {
				LoggingService.LogException(e);
			}
		}

		return response(EmbedType.ERROR)
				.setDescription("There was a problem adding your new status.");
	}

	private EmbedBuilder getGuilds() {
		StringBuilder sb = new StringBuilder();

		for (Guild g : App.Splatbot.getJDA().getGuilds()) {
			if (sb.length() > 0)
				sb.append("\n");

			sb.append("> ");
			sb.append(g.getName());
			sb.append(" (");
			sb.append(g.getId());
			sb.append(" )");
		}

		sb.insert(0, "**Guilds the bot runs on**\n");

		return response(EmbedType.INFO)
				.addField("Guilds the bot runs on", sb.toString(), false);
	}

	private EmbedBuilder getCommandStats() {
		StringBuilder answer = new StringBuilder();

		// Sort
		LinkedHashMap<String, Integer> cmdStats = DataService.SortHashMap(App.Splatbot.getStorageService().getGiantSquid().getCommandStats());

		for (String c : cmdStats.keySet()) {
			if(answer.length() > 0) {
				answer.append("\n");
			}

			answer.append("`").append(c).append("`: ").append(cmdStats.get(c));
		}

		return response(EmbedType.INFO)
				.setDescription(answer.toString());
	}

	private void clearCommandStats() {
		App.Splatbot.getStorageService().getGiantSquid().getCommandStats().clear();
	}

	private void leave(String gid) {
		for (Guild g : App.Splatbot.getJDA().getGuilds()) {
			if (g.getId().equals(gid)) {
				g.leave().queue();

				break;
			}
		}
	}

	private void saveBrains() {
		for (Squid b : App.Splatbot.getStorageService().getSquidController().getSquids()) {
			App.Splatbot.getStorageService().getSquidController().saveSquid(b);
		}

		App.Splatbot.getStorageService().getSquidController().saveGiantSquid();

		LoggingService.Write();
	}
}
