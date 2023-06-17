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
import com.hadenwatne.splatbot.models.data.stages.TurfWarStages;
import com.hadenwatne.splatbot.services.DataService;
import com.hadenwatne.splatbot.services.LoggingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TurfWar extends Command {
    public TurfWar() {
        super(false);
    }

    @Override
    protected CommandStructure buildCommandStructure() {
        return CommandBuilder.Create("turfwar", "Get details about Turf War.")
                .addAlias("tw")
                .addParameters(
                        new CommandParameter("update", "Whether this post should auto-update", ParameterType.BOOLEAN, false)
                                .setExample("true")
                )
                .build();
    }

    @Override
    public EmbedBuilder run(ExecutingCommand executingCommand) {
        List<TurfWarStages> tws = App.Splatbot.getStageData().getTurfWar();
        List<MessageEmbed.Field> fields = new ArrayList<>();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

        try {
            for (TurfWarStages stage : tws) {
                Calendar start = Calendar.getInstance();
                start.setTime(sdf.parse(stage.getStartTime()));

                Calendar end = Calendar.getInstance();
                end.setTime(sdf.parse(stage.getEndTime()));

                // Don't show past rotations.
                if(end.getTime().before(new Date())) {
                    continue;
                }

                // Timezone settings.
                String timezone = "America/New_York";

                if(executingCommand.getServer() != null) {
                    timezone = executingCommand.getSquid().getUserTimezones().getOrDefault(executingCommand.getAuthorUser().getIdLong(), timezone);
                }

                start.setTimeZone(TimeZone.getTimeZone(timezone));
                end.setTimeZone(TimeZone.getTimeZone(timezone));

                String timeHeader = DataService.BuildTimeWindowString(start,end);
                StringBuilder stages = new StringBuilder();

                for(String s : stage.getStages()) {
                    if(stages.length() > 0) {
                        stages.append(System.lineSeparator());
                    }

                    stages.append(s);
                }

                MessageEmbed.Field field = new MessageEmbed.Field(timeHeader, stages.toString(), false);

                fields.add(field);
            }

            EmbedBuilder builder = response(EmbedType.TURFWAR);

            builder.setDescription(executingCommand.getLanguage().getMsg(LanguageKeys.TURF_WAR_HEADING));
            builder.setThumbnail("https://i.imgur.com/2SnrhMv.png");

            for(int i=0; i<Math.min(5, fields.size()); i++) {
                builder.addField(fields.get(i));
            }

            return builder;
        } catch (Exception e) {
            LoggingService.LogException(e);

            return response(EmbedType.ERROR)
                    .addField(ErrorKeys.BOT_ERROR.name(), executingCommand.getLanguage().getError(ErrorKeys.BOT_ERROR), false);
        }
    }
}