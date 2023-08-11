package com.hadenwatne.splatbot.commands;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commandbuilder.CommandBuilder;
import com.hadenwatne.splatbot.commandbuilder.CommandParameter;
import com.hadenwatne.splatbot.commandbuilder.CommandStructure;
import com.hadenwatne.splatbot.commandbuilder.ParameterType;
import com.hadenwatne.splatbot.enums.*;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.command.ExecutingCommandArguments;
import com.hadenwatne.splatbot.models.data.BotSetting;
import com.hadenwatne.splatbot.models.data.Language;
import com.hadenwatne.splatbot.models.data.Squid;
import com.hadenwatne.splatbot.models.data.StickyPost;
import com.hadenwatne.splatbot.models.data.stages.RankedStages;
import com.hadenwatne.splatbot.services.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.*;

public class Settings extends Command {
    public Settings() {
        super(true);
    }

    @Override
    protected Permission[] configureRequiredBotPermissions() {
        return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected CommandStructure buildCommandStructure() {
        CommandParameter setting = new CommandParameter("setting", "The setting to view or update", ParameterType.SELECTION);

        for (BotSettingName name : BotSettingName.values()) {
            setting.addSelectionOptions(name.name());
        }

        return CommandBuilder.Create("modify", "The Administrator's command to customize bot settings and behavior.")
                .addAlias("settings")
                .addSubCommands(
                        CommandBuilder.Create("set", "Change a setting.")
                                .addParameters(
                                        setting
                                                .setExample("server_lang"),
                                        new CommandParameter("value", "The new value for this setting.", ParameterType.STRING)
                                                .setExample("pirate")
                                )
                                .build(),
                        CommandBuilder.Create("view", "View all current settings.")
                                .build(),
                        CommandBuilder.Create("help", "View help information for a setting.")
                                .addParameters(
                                        setting
                                                .setExample("server_lang")
                                )
                                .build()
                )
                .build();
    }

    @Override
    public EmbedBuilder run(ExecutingCommand executingCommand) {
        Language language = executingCommand.getLanguage();
        Squid squid = executingCommand.getSquid();
        BotSetting canModify = squid.getSettingFor(BotSettingName.ALLOW_SETTINGS);
        Member member = executingCommand.getAuthorMember();
        Guild server = executingCommand.getServer();
        String subCommand = executingCommand.getSubCommand();

        // Disallow users if they don't have permission.
        if(!SplatbotService.CheckUserPermission(server, canModify, member)) {
            return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
                    .setDescription(executingCommand.getLanguage().getError(ErrorKeys.NO_PERMISSION_USER));
        }

        switch (subCommand) {
            case "set":
                return cmdSet(language, squid, server, executingCommand);
            case "view":
                return cmdView(language, squid, server);
            case "help":
                return cmdHelp(squid, server, executingCommand.getCommandArguments());
        }

        return null;
    }

    private EmbedBuilder cmdView(Language language, Squid squid, Guild server) {
        EmbedBuilder embedBuilder = response(EmbedType.INFO, language.getMsg(LanguageKeys.SETTING_LIST_TITLE));

        for(BotSetting botSetting : squid.getSettings()) {
            embedBuilder.addField(getFormattedSettingField(botSetting, server));
        }

        return embedBuilder;
    }

    private EmbedBuilder cmdHelp(Squid squid, Guild server, ExecutingCommandArguments arguments) {
        String settingName = arguments.getAsString("setting").toUpperCase();
        BotSettingName botSettingName = BotSettingName.valueOf(settingName);
        BotSetting botSetting = squid.getSettingFor(botSettingName);
        EmbedBuilder embedBuilder = response(EmbedType.INFO, settingName);

        embedBuilder.addField(getFormattedSettingField(botSetting, server));
        embedBuilder.setDescription(botSettingName.getDescription());
        embedBuilder.addField("Type", botSetting.getType().name(), true);
        embedBuilder.addField("Possible Values", getSettingPossibleValues(botSetting), false);

        return embedBuilder;
    }

    private EmbedBuilder cmdSet(Language language, Squid squid, Guild server, ExecutingCommand executingCommand) {
        String settingName = executingCommand.getCommandArguments().getAsString("setting").toUpperCase();
        String settingValue = executingCommand.getCommandArguments().getAsString("value");
        BotSettingName botSettingName = BotSettingName.valueOf(settingName);
        BotSetting botSetting = squid.getSettingFor(botSettingName);

        // Ensure that this setting is only changed by an Administrator.
        if (botSettingName == BotSettingName.ALLOW_SETTINGS) {
            Member member = executingCommand.getAuthorMember();

            if (!member.hasPermission(Permission.ADMINISTRATOR) && !App.IsDebug) {
                return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
                        .setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
            }
        }

        // If the value is a mentionable, retrieve its ID.
        switch(botSetting.getType()) {
            case ROLE:
                Role role = executingCommand.getCommandArguments().getAsRole("value", server);

                if(role != null) {
                    // I have to do it this way, otherwise JDA returns a Snowflake.
                    settingValue = Long.toString(role.getIdLong());
                    break;
                }

                if(executingCommand.getCommandArguments().getAsString("value").equalsIgnoreCase("administrator")) {
                    settingValue = "administrator";
                }

                break;
            case EMOTE:
                CustomEmoji emote = executingCommand.getCommandArguments().getAsEmote("value", server);

                if(emote != null) {
                    settingValue = emote.getId();
                }

                break;
            case CHANNEL:
                MessageChannel channel = executingCommand.getCommandArguments().getAsChannel("value", server);

                if(channel != null) {
                    settingValue = channel.getId();
                }

                break;
        }

        // Set the value and return a success message if complete.
        if (botSetting.setValue(settingValue, squid)) {
            return response(EmbedType.INFO, botSettingName.name())
                    .setDescription(language.getMsg(LanguageKeys.SETTING_UPDATED_SUCCESS))
                    .addField(getFormattedSettingField(botSetting, server));
        } else {
            // Not successful
            return response(EmbedType.ERROR, ErrorKeys.SETTING_VALUE_INVALID.name())
                    .setDescription(language.getError(ErrorKeys.SETTING_VALUE_INVALID));
        }
    }

    private String getSettingPossibleValues(BotSetting botSetting) {
        switch(botSetting.getType()) {
            case BOOLEAN:
                return "`true`, `false`";
            case CHANNEL:
                return "#any_channel";
            case EMOTE:
                return ":AnyServerEmoji:";
            case NUMBER:
                return "1-99";
            case ROLE:
                return "@Any_Role, `administrator`, `everyone`";
            default:
                return "any";
        }
    }

    private MessageEmbed.Field getFormattedSettingField(BotSetting setting, Guild server){
        boolean isValid = false;
        String mention = "";
        String settingValue = setting.getAsString();

        switch(setting.getType()){
            case CHANNEL:
                try {
                    TextChannel mc = server.getTextChannelById(settingValue);

                    if (mc != null) {
                        isValid = true;
                        mention = mc.getAsMention();
                    }
                } catch (NumberFormatException e) {
                    isValid = false;
                }

                break;
            case EMOTE:
                try {
                    CustomEmoji em = server.getEmojiById(settingValue);

                    if(em != null) {
                        isValid = true;
                        mention = em.getAsMention();
                    }
                } catch (NumberFormatException e) {
                    isValid = false;
                }

                break;
            case ROLE:
                try {
                    if(settingValue.equals("administrator")) {
                        mention = settingValue;
                        isValid = true;
                        break;
                    }

                    Role role = server.getRoleById(settingValue);

                    if(role != null) {
                        isValid = true;
                        mention = role.getAsMention();
                    }
                } catch (Exception e) {
                    isValid = false;
                }

                break;
            default:
                isValid = true;
                mention = settingValue;
        }

        String value;

        if(isValid) {
            value = "**Current Value:** " + mention;
        } else {
            value = ":warning: INVALID :warning:";
        }

        return new MessageEmbed.Field("**__"+setting.getName()+"__**", value, true);
    }

    // TODO: Use modals to change settings.
    public Modal.Builder BuildModal() {
        Modal.Builder test = Modal.create(this.getCommandStructure().getName(), "Change Setting");

        //test.addComponents();

        return test;
    }
}