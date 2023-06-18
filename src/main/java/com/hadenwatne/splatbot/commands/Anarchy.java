package com.hadenwatne.splatbot.commands;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commandbuilder.CommandBuilder;
import com.hadenwatne.splatbot.commandbuilder.CommandParameter;
import com.hadenwatne.splatbot.commandbuilder.CommandStructure;
import com.hadenwatne.splatbot.commandbuilder.ParameterType;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.enums.ErrorKeys;
import com.hadenwatne.splatbot.enums.LanguageKeys;
import com.hadenwatne.splatbot.enums.PostType;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.data.Language;
import com.hadenwatne.splatbot.models.data.Squid;
import com.hadenwatne.splatbot.models.data.StickyPost;
import com.hadenwatne.splatbot.models.data.stages.RankedMode;
import com.hadenwatne.splatbot.models.data.stages.RankedStages;
import com.hadenwatne.splatbot.services.DataService;
import com.hadenwatne.splatbot.services.LoggingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Anarchy extends Command {
    public Anarchy() {
        super(false);
    }

    @Override
    protected Permission[] configureRequiredBotPermissions() {
        return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected CommandStructure buildCommandStructure() {
        return CommandBuilder.Create("anarchy", "Get details about Anarchy Battles.")
                .addAlias("a")
                .addParameters(
                        new CommandParameter("update", "Whether this post should auto-update", ParameterType.BOOLEAN, false)
                                .setExample("true")
                )
                .build();
    }

    @Override
    public EmbedBuilder run(ExecutingCommand executingCommand) {
        // Timezone settings.
        String timezone = "America/New_York";

        if(executingCommand.getServer() != null) {
            timezone = executingCommand.getSquid().getUserTimezones().getOrDefault(executingCommand.getAuthorUser().getIdLong(), timezone);
        }

        EmbedBuilder response = BuildStageList(timezone, executingCommand.getLanguage());

        if(executingCommand.getCommandArguments().getAsBoolean("update")){
            if(executingCommand.getServer() != null) {
                Squid squid = executingCommand.getSquid();
                String finalTimezone = timezone;

                executingCommand.reply(response, false, message -> {
                    squid.getStickyPosts().add(new StickyPost(message.getChannel().getIdLong(), message.getIdLong(), PostType.ANARCHY, finalTimezone));
                });

                return null;
            }
        }


        return response;
    }

    public EmbedBuilder BuildStageList(String timezone, Language language) {
        List<RankedStages> ranked = App.Splatbot.getStageData().getRanked();
        List<MessageEmbed.Field> fields = new ArrayList<>();

        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());
            now.setTimeZone(TimeZone.getTimeZone(timezone));

            for (RankedStages stage : ranked) {
                Calendar start = Calendar.getInstance();
                start.setTime(DataService.ParseDate(stage.getStartTime()));

                Calendar end = Calendar.getInstance();
                end.setTime(DataService.ParseDate(stage.getEndTime()));

                // Don't show past rotations.
                if(end.getTime().before(new Date())) {
                    continue;
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
                    modes.append(mode.getGamemode());
                    modes.append("**__");
                    modes.append(" _(");
                    modes.append(mode.getMode());
                    modes.append(")_");
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

            builder.setDescription(language.getMsg(LanguageKeys.ANARCHY_HEADING));
            builder.setThumbnail("https://i.imgur.com/4lUWWu7.png");
            builder.setFooter(DataService.BuildUpdatedTimestamp(now));

            for(int i=0; i<Math.min(5, fields.size()); i++) {
                builder.addField(fields.get(i));
            }

            return builder;
        } catch (Exception e) {
            LoggingService.LogException(e);

            return response(EmbedType.ERROR)
                    .addField(ErrorKeys.BOT_ERROR.name(), language.getError(ErrorKeys.BOT_ERROR), false);
        }
    }
}