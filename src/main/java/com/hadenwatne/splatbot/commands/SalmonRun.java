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
import com.hadenwatne.splatbot.models.data.stages.RankedMode;
import com.hadenwatne.splatbot.models.data.stages.RankedStages;
import com.hadenwatne.splatbot.models.data.stages.SalmonRunStages;
import com.hadenwatne.splatbot.services.DataService;
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
                .addParameters(
                        new CommandParameter("update", "Whether this post should auto-update", ParameterType.BOOLEAN, false)
                                .setExample("true")
                )
                .build();
    }

    @Override
    public EmbedBuilder run(ExecutingCommand executingCommand) {
        List<SalmonRunStages> salmonRun = App.Splatbot.getStageData().getSalmonRun();
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
                String timezone = "America/New_York";

                if(executingCommand.getServer() != null) {
                    timezone = executingCommand.getSquid().getUserTimezones().getOrDefault(executingCommand.getAuthorUser().getIdLong(), timezone);
                }

                start.setTimeZone(TimeZone.getTimeZone(timezone));
                end.setTimeZone(TimeZone.getTimeZone(timezone));

                String timeHeader = DataService.BuildTimeWindowString(start,end);
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

            builder.setDescription(executingCommand.getLanguage().getMsg(LanguageKeys.SALMON_RUN_HEADING));
            builder.setThumbnail("https://splatoon3.ink/assets/little-buddy.445c3c88.png");

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