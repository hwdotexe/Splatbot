package com.hadenwatne.splatbot.listeners;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commands.Command;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.enums.ErrorKeys;
import com.hadenwatne.splatbot.factories.EmbedFactory;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.data.Language;
import com.hadenwatne.splatbot.models.data.Squid;
import com.hadenwatne.splatbot.services.LoggingService;
import com.hadenwatne.splatbot.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.ArrayList;
import java.util.List;

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
            handleCommand(command, event, commandText, squid);
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

    private void handleCommand(Command command, SlashCommandInteractionEvent event, String commandText, Squid squid) {
        Language language = App.Splatbot.getLanguageService().getLangFor(squid);
        InteractionHook hook = event.getHook();
        ExecutingCommand executingCommand = new ExecutingCommand(language, squid);

        if(command != null) {
            executingCommand.setCommandName(command.getCommandStructure().getName());
            executingCommand.setInteractionHook(hook);
            executingCommand.setEvent(event);

            // Check that the bot has the necessary Discord permissions to process this command.
            if(executingCommand.getServer() != null) {
                Guild server = executingCommand.getServer();
                StringBuilder noPerms = new StringBuilder();

                for (Permission p : command.getRequiredPermissions()) {
                    if (!server.getSelfMember().hasPermission(hook.getInteraction().getGuildChannel(), p)) {
                        if (noPerms.length() > 0) {
                            noPerms.append(System.lineSeparator());
                        }

                        noPerms.append(p.getName());
                    }
                }

                if (noPerms.length() > 0) {
                    EmbedBuilder embed = EmbedFactory.GetEmbed(EmbedType.ERROR, ErrorKeys.PERMISSION_MISSING.name())
                            .setDescription(language.getError(ErrorKeys.PERMISSION_MISSING, new String[]{App.Splatbot.getBotName(), noPerms.toString()}));

                    List<EmbedBuilder> embeds = new ArrayList<>();

                    embeds.add(embed);

                    try {
                        MessageService.ReplyToMessage(hook, embeds, false);
                    } catch (InsufficientPermissionException e) {
                        MessageService.ReplyToMessage(hook, language.getError(ErrorKeys.PERMISSION_MISSING, new String[]{App.Splatbot.getBotName(), noPerms.toString()}), false);
                    } catch (Exception e) {
                        LoggingService.LogException(e);
                    }

                    return;
                }
            }

            App.Splatbot.getCommandHandler().HandleCommand(command, executingCommand, commandText);
        } else {
            EmbedBuilder embed = EmbedFactory.GetEmbed(EmbedType.ERROR, ErrorKeys.COMMAND_NOT_FOUND.name())
                    .setDescription(language.getError(ErrorKeys.COMMAND_NOT_FOUND));

            List<EmbedBuilder> embeds = new ArrayList<>();

            embeds.add(embed);

            MessageService.ReplyToMessage(hook, embeds, false);
        }
    }
}
