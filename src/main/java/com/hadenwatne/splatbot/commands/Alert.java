package com.hadenwatne.splatbot.commands;

import com.hadenwatne.splatbot.commandbuilder.CommandBuilder;
import com.hadenwatne.splatbot.commandbuilder.CommandParameter;
import com.hadenwatne.splatbot.commandbuilder.CommandStructure;
import com.hadenwatne.splatbot.commandbuilder.ParameterType;
import com.hadenwatne.splatbot.enums.AlertType;
import com.hadenwatne.splatbot.enums.BotSettingName;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.enums.ErrorKeys;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.command.ExecutingCommandArguments;
import com.hadenwatne.splatbot.models.data.BotSetting;
import com.hadenwatne.splatbot.models.data.ConfiguredAlert;
import com.hadenwatne.splatbot.models.data.Squid;
import com.hadenwatne.splatbot.services.SplatbotService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;

public class Alert extends Command {
	public Alert() {
		super(true);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		CommandParameter alertType = new CommandParameter("alertType", "The game mode to create an alert for", ParameterType.SELECTION);

		for (AlertType r : AlertType.values()) {
			alertType.addSelectionOptions(r.name());
		}

		return CommandBuilder.Create("alert", "Configure alerts for in-game events.")
				.addParameters(
						alertType
						.setExample("SPLATFEST")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		ExecutingCommandArguments args = executingCommand.getCommandArguments();
		Squid squid = executingCommand.getSquid();
		BotSetting canCreateAlerts = squid.getSettingFor(BotSettingName.CREATE_ALERTS);

		if (SplatbotService.CheckUserPermission(executingCommand.getServer(), canCreateAlerts, executingCommand.getAuthorMember())) {
			if(executingCommand.getChannel().getType() == ChannelType.TEXT) {
				AlertType alertType = AlertType.valueOf(args.getAsString("alertType"));
				long channelID = executingCommand.getChannel().getIdLong();

				// Timezone settings.
				String timezone = "America/New_York";

				if(executingCommand.getServer() != null) {
					timezone = executingCommand.getSquid().getUserTimezones().getOrDefault(executingCommand.getAuthorUser().getIdLong(), timezone);
				}

				if(squid.getAlerts().stream().noneMatch(a -> a.getChannel() == channelID && a.getType() == alertType)) {
					ConfiguredAlert newAlert = new ConfiguredAlert(alertType, executingCommand.getChannel().getIdLong(), timezone);

					squid.getAlerts().add(newAlert);

					return response(EmbedType.INFO)
							.setDescription("The new alert was created successfully!");
				}

				return response(EmbedType.ERROR, ErrorKeys.ALREADY_EXISTS.name())
						.setDescription(executingCommand.getLanguage().getError(ErrorKeys.ALREADY_EXISTS));
			}

			return response(EmbedType.ERROR, ErrorKeys.WRONG_CHANNEL_TYPE.name())
					.setDescription(executingCommand.getLanguage().getError(ErrorKeys.WRONG_CHANNEL_TYPE));
		} else {
			return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
					.setDescription(executingCommand.getLanguage().getError(ErrorKeys.NO_PERMISSION_USER));
		}
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
