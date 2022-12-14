package com.hadenwatne.splatbot.commands;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commandbuilder.CommandBuilder;
import com.hadenwatne.splatbot.commandbuilder.CommandStructure;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.enums.ErrorKeys;
import com.hadenwatne.splatbot.enums.LanguageKeys;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.data.stages.RankedMode;
import com.hadenwatne.splatbot.models.data.stages.RankedStages;
import com.hadenwatne.splatbot.models.data.stages.TurfWarStages;
import com.hadenwatne.splatbot.services.DataService;
import com.hadenwatne.splatbot.services.LoggingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Anarchy extends Command {
    public Anarchy() {
        super(false);
    }

    @Override
    protected CommandStructure buildCommandStructure() {
        return CommandBuilder.Create("anarchy", "Get details about Anarchy Battles.")
                .addAlias("a")
                .build();
    }

    @Override
    public EmbedBuilder run(ExecutingCommand executingCommand) {
        List<RankedStages> ranked = App.Splatbot.getStageData().getRanked();
        List<MessageEmbed.Field> fields = new ArrayList<>();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

        try {
            for (RankedStages stage : ranked) {
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
                StringBuilder modes = new StringBuilder();

                for(RankedMode mode : stage.getModes()) {
                    if(modes.length() > 0) {
                        modes.append(System.lineSeparator());
                    }

                    modes.append("__**");
                    modes.append(mode.getMode());
                    modes.append("**__");
                    modes.append(System.lineSeparator());

                    StringBuilder stages = new StringBuilder();

                    for(String s : mode.getStages()) {
                        if(stages.length() > 0) {
                            stages.append(System.lineSeparator());
                        }

                        stages.append(s);
                    }

                    modes.append(stages);
                }

                MessageEmbed.Field field = new MessageEmbed.Field(timeHeader, modes.toString(), false);

                fields.add(field);
            }

            EmbedBuilder builder = response(EmbedType.RANKED);

            builder.setDescription(executingCommand.getLanguage().getMsg(LanguageKeys.ANARCHY_HEADING));
            builder.setThumbnail("https://i.imgur.com/4lUWWu7.png");

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