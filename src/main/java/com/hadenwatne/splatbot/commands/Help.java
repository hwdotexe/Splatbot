package com.hadenwatne.splatbot.commands;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commandbuilder.CommandBuilder;
import com.hadenwatne.splatbot.commandbuilder.CommandParameter;
import com.hadenwatne.splatbot.commandbuilder.CommandStructure;
import com.hadenwatne.splatbot.commandbuilder.ParameterType;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.enums.ErrorKeys;
import com.hadenwatne.splatbot.factories.EmbedFactory;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.command.ExecutingCommandArguments;
import com.hadenwatne.splatbot.services.PaginationService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

public class Help extends Command {
	public Help() {
		super(false);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("help", "Shows help & additional information.")
				.addAlias("h")
				.addParameters(
						new CommandParameter("command", "The command you need help with", ParameterType.STRING, false)
								.setExample("turfwar")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		ExecutingCommandArguments args = executingCommand.getCommandArguments();
		String commandHelp = args.getAsString("command");
		EmbedBuilder embedBuilder = null;

		if(commandHelp != null) {
			embedBuilder = getCommandHelp(commandHelp);

			if(embedBuilder == null) {
				return response(EmbedType.ERROR)
						.addField(ErrorKeys.COMMAND_NOT_FOUND.name(), executingCommand.getLanguage().getError(ErrorKeys.COMMAND_NOT_FOUND), false);
			}
		} else {
			List<String> cmds = new ArrayList<>();

			for(Command command : App.Splatbot.getCommandHandler().getLoadedCommands()) {
				if(command.getCommandStructure().getDescription().length() > 0) {
					cmds.add(command.getCommandStructure().getName());
				}
			}

			String list = PaginationService.GenerateList(cmds, -1, false, false);

			embedBuilder = EmbedFactory.GetEmbed(EmbedType.INFO, "Help");

			embedBuilder.addField("All Commands", list, false);
			embedBuilder.addField("Information", "View additional information for each command by using `"+App.Splatbot.getBotName()+" help <command>`!", false);
			embedBuilder.addField("Summoning "+App.Splatbot.getBotName(), "You can summon me with `"+App.Splatbot.getBotName()+"`, `!sb`, or by using a Slash Command.", false);

			embedBuilder.setFooter(App.Splatbot.getBotName() + (App.IsDebug ? " **Debug Mode**" : ""));

		}

		embedBuilder.setThumbnail(App.Splatbot.getBotAvatarUrl());

		embedBuilder.addField("Syntax", "`<angle brackets>` are **required**\n" +
				"`[square brackets]` are **optional**\n" +
				"`[items|in|a|list]` are **possible values**", false);

		return embedBuilder;
	}

	private EmbedBuilder getCommandHelp(String commandHelp) {
		for(Command command : App.Splatbot.getCommandHandler().getLoadedCommands()) {
			String commandName = command.getCommandStructure().getName();

			if(commandName.equalsIgnoreCase(commandHelp) || isAlias(command.getCommandStructure(), commandHelp)) {
				EmbedBuilder embedBuilder = EmbedFactory.GetEmbed(EmbedType.INFO, "Help", commandName);

				for(MessageEmbed.Field field : command.getHelpFields()) {
					embedBuilder.addField(field);
				}

				return embedBuilder;
			}
		}

		return null;
	}

	private boolean isAlias(CommandStructure structure, String commandHelp) {
		for(String alias : structure.getAliases()) {
			if(alias.equalsIgnoreCase(commandHelp)) {
				return true;
			}
		}

		return false;
	}
}
