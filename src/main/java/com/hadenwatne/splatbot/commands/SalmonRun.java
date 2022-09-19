package com.hadenwatne.splatbot.commands;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commandbuilder.CommandBuilder;
import com.hadenwatne.splatbot.commandbuilder.CommandStructure;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.enums.ErrorKeys;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.data.stages.RankedMode;
import com.hadenwatne.splatbot.models.data.stages.RankedStages;
import com.hadenwatne.splatbot.models.data.stages.SalmonRunStages;
import com.hadenwatne.splatbot.services.LoggingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SalmonRun extends Command {
    public SalmonRun() {
        super(false);
    }

    @Override
    protected CommandStructure buildCommandStructure() {
        return CommandBuilder.Create("salmonrun", "Get details about Salmon Run.")
                .addAlias("sr")
                .build();
    }

    @Override
    public EmbedBuilder run(ExecutingCommand executingCommand) {
        List<SalmonRunStages> salmonRun = App.Splatbot.getStageData().getSalmonRun().subList(0, 5);
        List<MessageEmbed.Field> fields = new ArrayList<>();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

        try {
            for (SalmonRunStages stage : salmonRun) {
                Calendar start = Calendar.getInstance();
                start.setTime(sdf.parse(stage.getStartTime()));

                Calendar end = Calendar.getInstance();
                end.setTime(sdf.parse(stage.getEndTime()));

                // Don't show past rotations.
                if(end.getTime().before(new Date())) {
                    continue;
                }

                // Timezone settings.
                String timezone = "America/Denver";

                if(executingCommand.getServer() != null) {
                    timezone = executingCommand.getSquid().getUserTimezones().getOrDefault(executingCommand.getAuthorUser().getIdLong(), timezone);
                }

                start.setTimeZone(TimeZone.getTimeZone(timezone));
                end.setTimeZone(TimeZone.getTimeZone(timezone));

                String startTime = (start.get(Calendar.HOUR) == 0 ? 12 : start.get(Calendar.HOUR)) + ":" + start.get(Calendar.MINUTE) + "0" + (start.get(Calendar.AM_PM) == Calendar.AM ? "a" : "p");
                String endTime = (end.get(Calendar.HOUR) == 0 ? 12 : end.get(Calendar.HOUR)) + ":" + end.get(Calendar.MINUTE) + "0" + (end.get(Calendar.AM_PM) == Calendar.AM ? "a" : "p");
                String timeHeader = (start.get(Calendar.MONTH)+1) + "/" + start.get(Calendar.DAY_OF_MONTH) + " " + startTime + " — " + (end.get(Calendar.MONTH)+1) + "/" + end.get(Calendar.DAY_OF_MONTH) + " " + endTime + " (" + start.getTimeZone().getDisplayName(start.getTimeZone().inDaylightTime(start.getTime()), TimeZone.SHORT) + ")";

                StringBuilder detail = new StringBuilder();

                detail.append("__**");
                detail.append(stage.getStage());
                detail.append("**__");
                detail.append(System.lineSeparator());

                StringBuilder weapons = new StringBuilder();

                for(String w : stage.getWeapons()) {
                    if(weapons.length() > 0) {
                        weapons.append(System.lineSeparator());
                    }

                    weapons.append(w);
                }

                detail.append(weapons);

                MessageEmbed.Field field = new MessageEmbed.Field(timeHeader, detail.toString(), false);

                fields.add(field);
            }

            EmbedBuilder builder = response(EmbedType.SALMONRUN);

            builder.setDescription("Here are the deets for upcoming Salmon Run shifts!");
            builder.setThumbnail("https://splatoon3.ink/assets/little-buddy.445c3c88.png");

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