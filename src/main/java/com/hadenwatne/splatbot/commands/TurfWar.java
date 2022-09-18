package com.hadenwatne.splatbot.commands;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commandbuilder.CommandBuilder;
import com.hadenwatne.splatbot.commandbuilder.CommandStructure;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.enums.ErrorKeys;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.data.stages.TurfWarStages;
import com.hadenwatne.splatbot.services.LoggingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TurfWar extends Command {
    public TurfWar() {
        super(false);
    }

    @Override
    protected CommandStructure buildCommandStructure() {
        return CommandBuilder.Create("turfwar", "Get details about Turf War.")
                .addAlias("tw")
                .build();
    }

    @Override
    public EmbedBuilder run(ExecutingCommand executingCommand) {
        List<TurfWarStages> tws = App.Splatbot.getStageData().getTurfWar().subList(0, 5);
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

                String startTime = (start.get(Calendar.HOUR) == 0 ? 12 : start.get(Calendar.HOUR)) + ":" + start.get(Calendar.MINUTE) + "0" + (start.get(Calendar.AM_PM) == Calendar.AM ? "a" : "p");
                String endTime = (end.get(Calendar.HOUR) == 0 ? 12 : end.get(Calendar.HOUR)) + ":" + end.get(Calendar.MINUTE) + "0" + (end.get(Calendar.AM_PM) == Calendar.AM ? "a" : "p");
                String timeHeader = (start.get(Calendar.MONTH)+1) + "/" + start.get(Calendar.DAY_OF_MONTH) + " " + startTime + " — " + (end.get(Calendar.MONTH)+1) + "/" + end.get(Calendar.DAY_OF_MONTH) + " " + endTime;
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

            builder.setDescription("Here are the deets for upcoming Turf War battles!");
            builder.setThumbnail("https://i.imgur.com/2SnrhMv.png");
            // https://splatoon3.ink/assets/little-buddy.445c3c88.png for Salmon Run

            for(MessageEmbed.Field f : fields) {
                builder.addField(f);
            }

            return builder;
        } catch (Exception e) {
            LoggingService.LogException(e);

            return response(EmbedType.ERROR)
                    .addField(ErrorKeys.BOT_ERROR.name(), executingCommand.getLanguage().getError(ErrorKeys.BOT_ERROR), false);
        }
    }
}