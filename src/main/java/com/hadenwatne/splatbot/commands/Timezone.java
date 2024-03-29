package com.hadenwatne.splatbot.commands;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commandbuilder.CommandBuilder;
import com.hadenwatne.splatbot.commandbuilder.CommandParameter;
import com.hadenwatne.splatbot.commandbuilder.CommandStructure;
import com.hadenwatne.splatbot.commandbuilder.ParameterType;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.enums.ErrorKeys;
import com.hadenwatne.splatbot.enums.LanguageKeys;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.command.ExecutingCommandArguments;
import com.hadenwatne.splatbot.models.data.stages.TurfWarStages;
import com.hadenwatne.splatbot.services.LanguageService;
import com.hadenwatne.splatbot.services.LoggingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Timezone extends Command {
    public Timezone() {
        super(true);
    }

    @Override
    protected Permission[] configureRequiredBotPermissions() {
        return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected CommandStructure buildCommandStructure() {
        return CommandBuilder.Create("timezone", "Set your preferred timezone for Splatbot data!")
                .addAlias("tz")
                .addParameters(new CommandParameter("timezone", "The timezone to use", ParameterType.SELECTION)
                        .addSelectionOptions(
                                "Pacific/Honolulu",
                                "America/Anchorage",
                                "America/Vancouver",
                                "America/Los_Angeles",
                                "America/Phoenix",
                                "America/Edmonton",
                                "America/Denver",
                                "America/Chicago",
                                "America/Regina",
                                "America/New_York",
                                "America/Panama",
                                "America/Winnipeg",
                                "America/Toronto",
                                "America/Montreal",
                                "America/Puerto_Rico",
                                "Europe/London",
                                "Europe/Berlin",
                                "Asia/Tokyo",
                                "Asia/Bangkok",
                                "Asia/Seoul",
                                "Asia/Shanghai")
                        .setExample("America/New_York"))
                .build();
    }

    @Override
    public EmbedBuilder run(ExecutingCommand executingCommand) {
        ExecutingCommandArguments args = executingCommand.getCommandArguments();
        String timezone = args.getAsString("timezone");

        executingCommand.getSquid().getUserTimezones().put(executingCommand.getAuthorUser().getIdLong(), timezone);

        return response(EmbedType.INFO)
                .setDescription(executingCommand.getLanguage().getMsg(LanguageKeys.TIMEZONE_SET, new String[]{timezone}));
    }
}