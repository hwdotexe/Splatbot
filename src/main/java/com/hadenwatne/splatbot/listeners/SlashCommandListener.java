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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandText = normalizeCommandText(event);
        Command command = App.Splatbot.getCommandHandler().PreProcessCommand(commandText);

        // Ensure we own this command before continuing.
        if(command != null) {
            Squid squid = null;

            if(event.isFromGuild()) {
                squid = App.Splatbot.getStorageService().getSquid(event.getGuild().getId());
            }

            event.deferReply().queue();
            handleCommand(command, event.getHook(), commandText, squid);
        }
    }

    private String normalizeCommandText(SlashCommandInteractionEvent event) {
        StringBuilder sb = new StringBuilder();

        sb.append(event.getName());

        if(event.getSubcommandGroup() != null) {
            sb.append(" ");
            sb.append(event.getSubcommandGroup());
        }

        if(event.getSubcommandName() != null) {
            sb.append(" ");
            sb.append(event.getSubcommandName());
        }

        for(OptionMapping option : event.getOptions()) {
            sb.append(" ");
            sb.append(option.getAsString());
        }

        return sb.toString();
    }

    private void handleCommand(Command command, InteractionHook hook, String commandText, Squid squid) {
        Language language = App.Splatbot.getLanguageService().getLangFor(squid);
        ExecutingCommand executingCommand = new ExecutingCommand(language, squid);

        if(command != null) {
            executingCommand.setCommandName(command.getCommandStructure().getName());
            executingCommand.setInteractionHook(hook);

            App.Splatbot.getCommandHandler().HandleCommand(command, executingCommand, commandText);
        } else {
            EmbedBuilder embed = EmbedFactory.GetEmbed(EmbedType.ERROR, ErrorKeys.COMMAND_NOT_FOUND.name())
                    .setDescription(language.getError(ErrorKeys.COMMAND_NOT_FOUND));

            MessageService.ReplyToMessage(hook, embed, false);
        }
    }
}
