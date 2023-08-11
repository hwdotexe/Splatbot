package com.hadenwatne.splatbot.commands;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commandbuilder.CommandBuilder;
import com.hadenwatne.splatbot.commandbuilder.CommandParameter;
import com.hadenwatne.splatbot.commandbuilder.CommandStructure;
import com.hadenwatne.splatbot.commandbuilder.ParameterType;
import com.hadenwatne.splatbot.enums.BotSettingName;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.enums.ErrorKeys;
import com.hadenwatne.splatbot.factories.EmbedFactory;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.command.ExecutingCommandArguments;
import com.hadenwatne.splatbot.models.data.BotSetting;
import com.hadenwatne.splatbot.models.data.Squid;
import com.hadenwatne.splatbot.services.LanguageService;
import com.hadenwatne.splatbot.services.PaginationService;
import com.hadenwatne.splatbot.services.SplatbotService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

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
		return CommandBuilder.Create("alert", "Configure alerts for in-game events.")
				.addParameters(
						new CommandParameter("type", "The command you need help with", ParameterType.STRING, false)
								.setExample("turfwar")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		ExecutingCommandArguments args = executingCommand.getCommandArguments();
		Squid squid = executingCommand.getSquid();
		BotSetting canCreateAlerts = squid.getSettingFor(BotSettingName.CREATE_ALERTS);

		if (SplatbotService.CheckUserPermission(executingCommand.getServer(), canCreateAlerts, executingCommand.getAuthorMember())) {
			// TODO: alerts for splatfest announcements, others as they become necessary?

			return response(EmbedType.INFO);
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
