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
import com.hadenwatne.splatbot.models.data.stages.RankedStages;
import com.hadenwatne.splatbot.models.data.stages.XStages;
import com.hadenwatne.splatbot.services.DataService;
import com.hadenwatne.splatbot.services.LoggingService;
import com.hadenwatne.splatbot.services.StageEmbedService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.*;

public class XBattles extends Command {
    public XBattles() {
        super(false);
    }

    @Override
    protected Permission[] configureRequiredBotPermissions() {
        return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected CommandStructure buildCommandStructure() {
        return CommandBuilder.Create("xbattles", "Get details about X Battles.")
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

        if(executingCommand.getCommandArguments().getAsBoolean("update")){
            if(executingCommand.getServer() != null) {
                EmbedBuilder response = BuildStageList(timezone, executingCommand.getLanguage(), true);
                Squid squid = executingCommand.getSquid();
                String finalTimezone = timezone;

                executingCommand.reply(response, false, message -> {
                    squid.getStickyPosts().add(new StickyPost(message.getChannel().getIdLong(), message.getIdLong(), PostType.X_BATTLES, finalTimezone));
                });

                return null;
            }
        }

        return BuildStageList(timezone, executingCommand.getLanguage(), false);
    }

    public EmbedBuilder BuildStageList(String timezone, Language language, boolean refreshing) {
        List<XStages> xbattles = App.Splatbot.getStageData().getXRanked();

        try {
            List<MessageEmbed.Field> fields = new ArrayList<>();
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());
            now.setTimeZone(TimeZone.getTimeZone(timezone));

            for(int i=0; i<Math.min(5, xbattles.size()); i++) {
                fields.add(StageEmbedService.XBattlesField(xbattles.get(i), timezone));
            }

            EmbedBuilder builder = response(EmbedType.XBATTLE);

            builder.setDescription(language.getMsg(LanguageKeys.XBATTLES_HEADING));
            builder.setThumbnail("https://i.imgur.com/JqpIpcq.png");
            builder.setFooter(DataService.BuildUpdatedTimestamp(now, refreshing));

            fields.forEach(builder::addField);

            return builder;
        } catch (Exception e) {
            LoggingService.LogException(e);

            return response(EmbedType.ERROR)
                    .addField(ErrorKeys.BOT_ERROR.name(), language.getError(ErrorKeys.BOT_ERROR), false);
        }
    }
}