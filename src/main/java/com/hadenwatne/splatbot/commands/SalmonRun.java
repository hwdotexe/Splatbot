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
import com.hadenwatne.splatbot.models.gameData.schedules.ScheduleNode;
import com.hadenwatne.splatbot.services.DataService;
import com.hadenwatne.splatbot.services.LoggingService;
import com.hadenwatne.splatbot.services.StageEmbedService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.*;

public class SalmonRun extends Command {
    public SalmonRun() {
        super(false);
    }

    @Override
    protected Permission[] configureRequiredBotPermissions() {
        return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected CommandStructure buildCommandStructure() {
        return CommandBuilder.Create("salmonrun", "Get details about Salmon Run.")
                .addAlias("sr")
                .build();
    }

    @Override
    public List<EmbedBuilder> run(ExecutingCommand executingCommand) {
        // Timezone settings.
        String timezone = "America/New_York";

        if(executingCommand.getServer() != null) {
            timezone = executingCommand.getSquid().getUserTimezones().getOrDefault(executingCommand.getAuthorUser().getIdLong(), timezone);
        }

        return BuildStageList(timezone, executingCommand.getLanguage(), false);
    }

    public List<EmbedBuilder> BuildStageList(String timezone, Language language, boolean refreshing) {
        List<ScheduleNode> salmonRun = App.Splatbot.getStageData().getRegular().data.coopGroupingSchedule.regularSchedules.nodes;
        List<EmbedBuilder> embed = new ArrayList<>();

        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());
            now.setTimeZone(TimeZone.getTimeZone(timezone));

            for(int i=0; i<Math.min(3, salmonRun.size()); i++) {
                EmbedBuilder builder = response(EmbedType.SALMONRUN);

                builder.addField(StageEmbedService.SalmonRunField(salmonRun.get(i), timezone));
                builder.setThumbnail(salmonRun.get(i).setting.coopStage.image.url);
                builder.setFooter(DataService.BuildUpdatedTimestamp(now, refreshing));

                embed.add(builder);
            }
        } catch (Exception e) {
            LoggingService.LogException(e);

            embed.add(response(EmbedType.ERROR)
                    .addField(ErrorKeys.BOT_ERROR.name(), language.getError(ErrorKeys.BOT_ERROR), false));
        }

        return embed;
    }
}