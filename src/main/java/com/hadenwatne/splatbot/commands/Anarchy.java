package com.hadenwatne.splatbot.commands;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commandbuilder.CommandBuilder;
import com.hadenwatne.splatbot.commandbuilder.CommandStructure;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.enums.ErrorKeys;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.data.stages.RankedMode;
import com.hadenwatne.splatbot.models.data.stages.RankedStages;
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
        List<RankedStages> ranked = App.Splatbot.getStageData().getRanked().subList(0, 5);
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

                String startTime = (start.get(Calendar.HOUR) == 0 ? 12 : start.get(Calendar.HOUR)) + ":" + start.get(Calendar.MINUTE) + "0" + (start.get(Calendar.AM_PM) == Calendar.AM ? "a" : "p");
                String endTime = (end.get(Calendar.HOUR) == 0 ? 12 : end.get(Calendar.HOUR)) + ":" + end.get(Calendar.MINUTE) + "0" + (end.get(Calendar.AM_PM) == Calendar.AM ? "a" : "p");
                String timeHeader = (start.get(Calendar.MONTH)+1) + "/" + start.get(Calendar.DAY_OF_MONTH) + " " + startTime + " — " + (end.get(Calendar.MONTH)+1) + "/" + end.get(Calendar.DAY_OF_MONTH) + " " + endTime;

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

            builder.setDescription("Here are the deets for upcoming Anarchy battles!");
            builder.setThumbnail("https://i.imgur.com/4lUWWu7.png");
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