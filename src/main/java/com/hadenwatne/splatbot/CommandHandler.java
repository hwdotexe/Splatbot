package com.hadenwatne.splatbot;

import com.hadenwatne.splatbot.commandbuilder.CommandBuilder;
import com.hadenwatne.splatbot.commandbuilder.CommandParameter;
import com.hadenwatne.splatbot.commandbuilder.CommandStructure;
import com.hadenwatne.splatbot.commandbuilder.SubCommandGroup;
import com.hadenwatne.splatbot.commands.*;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.enums.ErrorKeys;
import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.factories.EmbedFactory;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.command.ExecutingCommandArguments;
import com.hadenwatne.splatbot.services.LoggingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler {
	private final List<Command> commands;

	public CommandHandler() {
		LoggingService.Log(LogType.SYSTEM, "Loading commands...");

		commands = new ArrayList<>();

		commands.add(new Anarchy());
		commands.add(new Challenge());
		commands.add(new Dev());
		commands.add(new Help());
		commands.add(new SalmonRun());
		commands.add(new SplatfestCmd());
		commands.add(new Timezone());
		commands.add(new TurfWar());
		commands.add(new RandomWeapon());
		commands.add(new XBattles());

		// Send Discord the syntax we plan to use for slash commands.
		updateSlashCommands();

		LoggingService.Log(LogType.SYSTEM, "Command loading complete!");
	}

	/**
	 * Gets a list of commands actively loaded.
	 * @return A list of commands.
	 */
	public List<Command> getLoadedCommands(){
		return commands;
	}

	/**
	 * Attempts to match the raw command data to a Command. This does not check for usage errors.
	 * @param commandText The raw text of the command being run.
	 * @return A Command object if found, otherwise null.
	 */
	public Command PreProcessCommand(String commandText) {
		for(Command command : commands) {
			Matcher commandMatcher = command.getCommandStructure().getPrimaryPattern().matcher(commandText);

			if(commandMatcher.find()) {
				return command;
			}
		}

		return null;
	}

	/**
	 * Begins processing a command, handling errors, and sending a response.
	 * @param command The command to handle.
	 * @param executingCommand The command context to use.
	 * @param commandText The raw text of the command.
	 */
	public void HandleCommand(Command command, ExecutingCommand executingCommand, String commandText) {
		if(App.Splatbot.getCommandHandler().ValidateCommand(command, commandText)) {
			// If this command requires a server, but none is accessible, throw an error.
			if(command.requiresGuild() && executingCommand.getServer() == null) {
				EmbedBuilder embed = EmbedFactory.GetEmbed(EmbedType.ERROR, "GUILD_REQUIRED")
						.setDescription("That command must be run on a server.");

				executingCommand.reply(embed);

				return;
			}

			App.Splatbot.getCommandHandler().ParseCommand(command, commandText, executingCommand);
			App.Splatbot.getCommandHandler().ExecuteCommand(command, executingCommand);
		} else {
			EmbedBuilder embed = EmbedFactory.GetEmbed(EmbedType.ERROR, ErrorKeys.WRONG_USAGE.name())
					.setDescription(executingCommand.getLanguage().getError(ErrorKeys.WRONG_USAGE));

			for(MessageEmbed.Field field : command.getHelpFields()) {
				embed.addField(field);
			}

			executingCommand.reply(embed);
		}
	}

	/**
	 * Attempts to validate the command for usage errors.
	 * @param command The command to validate against.
	 * @param commandText The command usage to validate.
	 * @return True, if the validation was successful. Otherwise false.
	 */
	private boolean ValidateCommand(Command command, String commandText) {
		Matcher commandMatcher = command.getCommandStructure().getMatcherPattern().matcher(commandText);

		if(commandMatcher.find()) {
			return commandMatcher.group("context") != null;
		}

		return false;
	}

	/**
	 * Breaks down a validated raw string command into a parseable context object with named parameters.
	 * @param command The command to parse against.
	 * @param commandText The raw command.
	 * @param executingCommand The command context to update.
	 */
	private void ParseCommand(Command command, String commandText, ExecutingCommand executingCommand) {
		Matcher commandMatcher = command.getCommandStructure().getMatcherPattern().matcher(commandText);

		if(commandMatcher.find()) {
			final String context = commandMatcher.group("context");
			final CommandStructure commandStructure = command.getCommandStructure();

			// Sub-command-group Specific
			for(SubCommandGroup subCommandGroup : commandStructure.getSubcommandGroups()) {
				final String groupMatch = matchStringToCommand(context, subCommandGroup.getName(), subCommandGroup.getAliases());

				if(groupMatch != null) {
					executingCommand.setSubCommandGroup(subCommandGroup.getName());

					// Sub-command Specific
					final String contextStripped = context.substring(groupMatch.length()).trim();
					ParseCommandSubCommands(subCommandGroup.getSubCommands(), contextStripped, executingCommand, commandMatcher);

					break;
				}
			}

			// Process sub commands on the primary command structure, if any.
			if(!executingCommand.hasSubCommandGroup()) {
				ParseCommandSubCommands(commandStructure.getSubCommands(), context, executingCommand, commandMatcher);
			}

			// Process command arguments on the primary command structure, if any.
			if(!executingCommand.hasSubCommand()) {
				ParseCommandArguments(commandStructure, executingCommand, commandMatcher);
			}
		}

		if(executingCommand.getServer() != null) {
			LoggingService.Log(LogType.COMMAND, "["+executingCommand.getAuthorUser().getId()+"@"+executingCommand.getServer().getId()+"] "+commandText);
		} else {
			LoggingService.Log(LogType.COMMAND, "["+executingCommand.getAuthorUser().getId()+"@"+"Private] "+commandText);
		}
	}

	/**
	 * Breaks down sub commands into a parseable object.
	 * @param subCommands A list of subcommands to test against.
	 * @param context The raw command.
	 * @param executingCommand The running context.
	 * @param commandMatcher The matcher object.
	 */
	private void ParseCommandSubCommands(List<CommandStructure> subCommands, String context, ExecutingCommand executingCommand, Matcher commandMatcher) {
		for(CommandStructure subCommand : subCommands) {
			final String subCommandMatch = matchStringToCommand(context, subCommand.getName(), subCommand.getAliases());

			if(subCommandMatch != null) {
				executingCommand.setSubCommand(subCommand.getName());

				ParseCommandArguments(subCommand, executingCommand, commandMatcher);

				break;
			}
		}
	}

	/**
	 * Breaks down parameter arguments into a parseable object.
	 * @param commandStructure The command to check against.
	 * @param executingCommand The running context.
	 * @param commandMatcher The matcher object.
	 */
	private void ParseCommandArguments(CommandStructure commandStructure, ExecutingCommand executingCommand, Matcher commandMatcher) {
		if(commandStructure.getParameters().size() > 0) {
			ExecutingCommandArguments commandArguments = new ExecutingCommandArguments();

			for (CommandParameter commandParameter : commandStructure.getParameters()) {
				String parameterMatch = commandMatcher.group(commandParameter.getRegexName());

				if (parameterMatch != null) {
					commandArguments.add(commandParameter.getName(), parameterMatch);
				}
			}

			executingCommand.setCommandArguments(commandArguments);
		}
	}

	/**
	 * Asynchronously executes the provided command given an ExecuringCommand context.
	 * @param command The command to execute.
	 * @param executingCommand An object containing context for the command running.
	 */
	private void ExecuteCommand(Command command, ExecutingCommand executingCommand) {
		// TODO clean up typing indicator
		// TODO splitting up a long reply (message service!)

		CompletableFuture.supplyAsync(() -> command.run(executingCommand))
				.thenAccept(executingCommand::reply)
				.exceptionally(exception -> {
					EmbedBuilder embedBuilder = EmbedFactory.GetEmbed(EmbedType.ERROR, "Error");
					embedBuilder.addField(ErrorKeys.BOT_ERROR.name(), executingCommand.getLanguage().getError(ErrorKeys.BOT_ERROR), false);

					executingCommand.reply(embedBuilder);
					LoggingService.LogException(exception);

					return null;
				});
	}

	private String matchStringToCommand(String toMatch, String commandName, List<String> aliases) {
		Matcher commandBorderMatcher = Pattern.compile("^("+commandName+")\\b(.+)?", Pattern.CASE_INSENSITIVE).matcher(toMatch);

		if(commandBorderMatcher.find()) {
			return commandBorderMatcher.group(1);
		} else {
			for(String alias : aliases) {
				Matcher aliasBorderMatcher = Pattern.compile("^("+alias+")\\b(.+)?", Pattern.CASE_INSENSITIVE).matcher(toMatch);

				if(aliasBorderMatcher.find()) {
					return aliasBorderMatcher.group(1);
				}
			}
		}

		return null;
	}

	private void updateSlashCommands() {
		// Update command syntax on individual test servers.
		if(App.IsDebug) {
			for(Guild g : App.Splatbot.getJDA().getGuilds()) {
				CommandListUpdateAction cUpdate = App.Splatbot.getJDA().getGuildById(g.getId()).updateCommands();

				issueSlashCommandUpdate(cUpdate);
			}

			return;
		}

		// Update command syntax on Discord if configured to do so.
		if(App.Splatbot.getStorageService().getGiantSquid().doUpdateDiscordSlashCommands()) {
			CommandListUpdateAction cUpdate = App.Splatbot.getJDA().updateCommands();

			issueSlashCommandUpdate(cUpdate);
			App.Splatbot.getStorageService().getGiantSquid().setUpdateDiscordSlashCommands(false);
		}
	}

	private void issueSlashCommandUpdate(CommandListUpdateAction cUpdate) {
		try {
			for (Command command : this.commands) {
				if (command.getCommandStructure().getDescription().length() > 0) {
					cUpdate.addCommands(CommandBuilder.BuildSlashCommandData(command));
				}
			}

			cUpdate.queue();
		}catch (Exception e) {
			LoggingService.LogException(e);
		}
	}
}
