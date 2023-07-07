package com.hadenwatne.splatbot.services;

import com.hadenwatne.splatbot.models.data.stages.*;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class StageEmbedService {
    public static MessageEmbed.Field TurfWarField(TurfWarStages stage, String timezone) {
        Calendar start = Calendar.getInstance();
        start.setTime(DataService.ParseDate(stage.getStartTime()));

        Calendar end = Calendar.getInstance();
        end.setTime(DataService.ParseDate(stage.getEndTime()));

        start.setTimeZone(TimeZone.getTimeZone(timezone));
        end.setTimeZone(TimeZone.getTimeZone(timezone));

        String timeHeader = DataService.BuildTimeWindowString(start,end);
        StringBuilder stages = new StringBuilder();

        for(String s : stage.getStages()) {
            if(stages.length() > 0) {
                stages.append(System.lineSeparator());
            }

            stages.append("• ");
            stages.append(s);
        }

        return new MessageEmbed.Field(timeHeader, stages.toString(), false);
    }

    public static MessageEmbed.Field AnarchyField(RankedStages stage, String timezone) {
        Calendar start = Calendar.getInstance();
        start.setTime(DataService.ParseDate(stage.getStartTime()));

        Calendar end = Calendar.getInstance();
        end.setTime(DataService.ParseDate(stage.getEndTime()));

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

                stages.append("• ");
                stages.append(s);
            }

            modes.append(stages);
        }

        return new MessageEmbed.Field(timeHeader, modes.toString(), false);
    }

    public static MessageEmbed.Field XBattlesField(XStages stage, String timezone) {
        Calendar start = Calendar.getInstance();
        start.setTime(DataService.ParseDate(stage.getStartTime()));

        Calendar end = Calendar.getInstance();
        end.setTime(DataService.ParseDate(stage.getEndTime()));

        start.setTimeZone(TimeZone.getTimeZone(timezone));
        end.setTimeZone(TimeZone.getTimeZone(timezone));

        String timeHeader = DataService.BuildTimeWindowString(start,end);
        StringBuilder stages = new StringBuilder();

        stages.append("__**");
        stages.append(stage.getGameMode());
        stages.append("**__");

        for(String s : stage.getStages()) {
            if(stages.length() > 0) {
                stages.append(System.lineSeparator());
            }

            stages.append("• ");
            stages.append(s);
        }

        return new MessageEmbed.Field(timeHeader, stages.toString(), false);
    }

    public static MessageEmbed.Field ChallengeField(ChallengeEvent stage, String timezone) {
        StringBuilder challengeText = new StringBuilder();

        challengeText.append("_");
        challengeText.append(stage.getDescription());
        challengeText.append("_");
        challengeText.append(System.lineSeparator());
        challengeText.append("**— ");
        challengeText.append(stage.getGameMode());
        challengeText.append("— **");

        for(String s : stage.getStages()) {
            challengeText.append(System.lineSeparator());
            challengeText.append("• ");
            challengeText.append(s);
        }

        for(ChallengeEventTime time : stage.getTimes()) {
            Calendar start = Calendar.getInstance();
            start.setTime(DataService.ParseDate(time.getStartTime()));

            Calendar end = Calendar.getInstance();
            end.setTime(DataService.ParseDate(time.getEndTime()));

            start.setTimeZone(TimeZone.getTimeZone(timezone));
            end.setTimeZone(TimeZone.getTimeZone(timezone));

            String timeWindowString = DataService.BuildTimeWindowString(start,end);

            challengeText.append(System.lineSeparator());
            challengeText.append(timeWindowString);
        }

        challengeText.append(System.lineSeparator());
        challengeText.append("> ");

        String regulation = stage.getRegulation().replaceAll("(<br />)+[^\\w\\d]+", System.lineSeparator()+"> ");

        challengeText.append(regulation);

        return new MessageEmbed.Field("__"+stage.getName()+"__", challengeText.toString(), false);
    }

    public static MessageEmbed.Field SalmonRunField(SalmonRunStages stage, String timezone) {
        Calendar start = Calendar.getInstance();
        start.setTime(DataService.ParseDate(stage.getStartTime()));

        Calendar end = Calendar.getInstance();
        end.setTime(DataService.ParseDate(stage.getEndTime()));

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

            weapons.append("• ");
            weapons.append(w);
        }

        detail.append(weapons);

        return new MessageEmbed.Field(timeHeader, detail.toString(), false);
    }

    public static MessageEmbed.Field SplatfestField(Splatfest fest, String timezone) {
        Calendar start = Calendar.getInstance();
        start.setTime(DataService.ParseDate(fest.getStartTime()));

        Calendar end = Calendar.getInstance();
        end.setTime(DataService.ParseDate(fest.getEndTime()));

        start.setTimeZone(TimeZone.getTimeZone(timezone));
        end.setTimeZone(TimeZone.getTimeZone(timezone));

        String timeHeader = DataService.BuildTimeWindowString(start,end);
        StringBuilder detail = new StringBuilder();

        detail.append("__**");
        detail.append(fest.getTitle());
        detail.append("**__");
        detail.append(" (");
        detail.append(fest.getStatus());
        detail.append(")");
        detail.append(System.lineSeparator());
        detail.append("**");
        detail.append("• ");
        detail.append(fest.getTeams().get(0));
        detail.append(System.lineSeparator());
        detail.append("• ");
        detail.append(fest.getTeams().get(1));
        detail.append(System.lineSeparator());
        detail.append("• ");
        detail.append(fest.getTeams().get(2));
        detail.append("**");

        StringBuilder weapons = new StringBuilder();

        detail.append(weapons);

        return new MessageEmbed.Field(timeHeader, detail.toString(), false);
    }

    public static MessageEmbed.Field SplatfestStageField(SplatfestStages stage, String timezone) {
        Calendar start = Calendar.getInstance();
        start.setTime(DataService.ParseDate(stage.getStartTime()));

        Calendar end = Calendar.getInstance();
        end.setTime(DataService.ParseDate(stage.getEndTime()));

        start.setTimeZone(TimeZone.getTimeZone(timezone));
        end.setTimeZone(TimeZone.getTimeZone(timezone));

        String timeHeader = DataService.BuildTimeWindowString(start,end);
        StringBuilder stages = new StringBuilder();

        for(String s : stage.getStages()) {
            if(stages.length() > 0) {
                stages.append(System.lineSeparator());
            }

            stages.append("• ");
            stages.append(s);
        }

        return new MessageEmbed.Field(timeHeader, stages.toString(), false);
    }
}
