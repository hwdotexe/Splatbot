package com.hadenwatne.splatbot.listeners;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commands.Command;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.enums.ErrorKeys;
import com.hadenwatne.splatbot.factories.EmbedFactory;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.data.Language;
import com.hadenwatne.splatbot.models.data.Squid;
import com.hadenwatne.splatbot.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChatListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (!e.getAuthor().isBot()) {
			Message message = e.getMessage();
			final String messageText = message.getContentRaw();

			// Messages from a Guild may contain additional data or context for the bot.
			if (e.isFromGuild()) {
				Guild server = e.getGuild();
				Squid squid = App.Splatbot.getStorageService().getSquid(server.getId());
				String triggerUsed = "";

				if (messageText.toLowerCase().startsWith(App.Splatbot.getBotName().toLowerCase())) {
					triggerUsed = App.Splatbot.getBotName().toLowerCase();
				} else if (messageText.toLowerCase().startsWith("!sb")) {
					triggerUsed = "!sb";
				} else if (messageText.toLowerCase().startsWith("!splatbot")) {
					triggerUsed = "!splatbot";
				}

				// Check if this message is trying to run a command.
				if (triggerUsed.length() > 0) {
					Language language = App.Splatbot.getLanguageService().getLangFor(squid);

					if (messageText.trim().length() == triggerUsed.length()) {
						EmbedBuilder embed = EmbedFactory.GetEmbed(EmbedType.TURFWAR)
								.setDescription(language.getError(ErrorKeys.HEY_THERE, new String[]{App.Splatbot.getBotName()}));

						MessageService.ReplyToMessage(message, embed, false);

						return;
					}

					final String command = messageText.substring(triggerUsed.length()).trim();

					handleCommand(message, command, squid, language);

					return;
				}
			} else {
				// Messages sent to the bot directly are limited to basic commands.
				if (e.getChannelType() == ChannelType.PRIVATE || e.getChannelType() == ChannelType.GROUP) {
					final String botNameLower = App.Splatbot.getBotName().toLowerCase();

					if (messageText.toLowerCase().startsWith(botNameLower)) {
						final String command = messageText.substring(botNameLower.length()).trim();

						handleCommand(message, command, null, App.Splatbot.getLanguageService().getDefaultLang());
					}
				}
			}
		}
	}

	private void handleCommand(Message message, String commandText, Squid squid, Language language) {
		Command command = App.Splatbot.getCommandHandler().PreProcessCommand(commandText);
		ExecutingCommand executingCommand = new ExecutingCommand(language, squid);

		if (command != null) {
			executingCommand.setCommandName(command.getCommandStructure().getName());
			executingCommand.setMessage(message);

			App.Splatbot.getCommandHandler().HandleCommand(command, executingCommand, commandText);
		} else {
			EmbedBuilder embed = EmbedFactory.GetEmbed(EmbedType.ERROR, ErrorKeys.COMMAND_NOT_FOUND.name())
					.setDescription(language.getError(ErrorKeys.COMMAND_NOT_FOUND));

			MessageService.ReplyToMessage(message, embed, false);
		}
	}
}