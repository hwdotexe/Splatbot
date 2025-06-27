package com.hadenwatne.splatbot.services;

import com.hadenwatne.splatbot.models.gameData.schedules.*;
import com.hadenwatne.splatbot.models.gameData.splatfests.Festival;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Calendar;
import java.util.TimeZone;

public class StageEmbedService {
    public static MessageEmbed.Field TurfWarField(ScheduleNode stage, String timezone) {
        Calendar start = Calendar.getInstance();
        start.setTime(DataService.ParseDate(stage.startTime));

        Calendar end = Calendar.getInstance();
        end.setTime(DataService.ParseDate(stage.endTime));

        start.setTimeZone(TimeZone.getTimeZone(timezone));
        end.setTimeZone(TimeZone.getTimeZone(timezone));

        String timeHeader = DataService.BuildTimeWindowString(start,end);
        StringBuilder stages = new StringBuilder();

        for(VsStage s : stage.regularMatchSetting.vsStages) {
            if(stages.length() > 0) {
                stages.append(System.lineSeparator());
            }

            stages.append("• ");
            stages.append(s.name);
        }

        return new MessageEmbed.Field(timeHeader, stages.toString(), false);
    }

    public static MessageEmbed.Field AnarchyField(ScheduleNode stage, String timezone) {
        Calendar start = Calendar.getInstance();
        start.setTime(DataService.ParseDate(stage.startTime));

        Calendar end = Calendar.getInstance();
        end.setTime(DataService.ParseDate(stage.endTime));

        start.setTimeZone(TimeZone.getTimeZone(timezone));
        end.setTimeZone(TimeZone.getTimeZone(timezone));

        String timeHeader = DataService.BuildTimeWindowString(start,end);
        StringBuilder modes = new StringBuilder();

        for(MatchSetting mode : stage.bankaraMatchSettings) {
            if(modes.length() > 0) {
                modes.append(System.lineSeparator());
            }

            modes.append("__**");
            modes.append(mode.vsRule.name);
            modes.append("**__");
            modes.append(System.lineSeparator());

            StringBuilder stages = new StringBuilder();

            for(VsStage s : mode.vsStages) {
                if(stages.length() > 0) {
                    stages.append(System.lineSeparator());
                }

                stages.append("• ");
                stages.append(s.name);
            }

            modes.append(stages);
        }

        return new MessageEmbed.Field(timeHeader, modes.toString(), false);
    }

    public static MessageEmbed.Field XBattlesField(ScheduleNode stage, String timezone) {
        Calendar start = Calendar.getInstance();
        start.setTime(DataService.ParseDate(stage.startTime));

        Calendar end = Calendar.getInstance();
        end.setTime(DataService.ParseDate(stage.endTime));

        start.setTimeZone(TimeZone.getTimeZone(timezone));
        end.setTimeZone(TimeZone.getTimeZone(timezone));

        String timeHeader = DataService.BuildTimeWindowString(start,end);
        StringBuilder stages = new StringBuilder();

        stages.append("__**");
        stages.append(stage.xMatchSetting.vsRule.name);
        stages.append("**__");

        for(VsStage s : stage.xMatchSetting.vsStages) {
            if(stages.length() > 0) {
                stages.append(System.lineSeparator());
            }

            stages.append("• ");
            stages.append(s.name);
        }

        return new MessageEmbed.Field(timeHeader, stages.toString(), false);
    }

    public static MessageEmbed.Field ChallengeField(ScheduleNode stage, String timezone) {
        StringBuilder challengeText = new StringBuilder();

        challengeText.append("_");
        challengeText.append(stage.leagueMatchSetting.leagueMatchEvent.desc);
        challengeText.append("_");
        challengeText.append(System.lineSeparator());
        challengeText.append("**— ");
        challengeText.append(stage.leagueMatchSetting.vsRule.name);
        challengeText.append("— **");

        for(VsStage s : stage.leagueMatchSetting.vsStages) {
            challengeText.append(System.lineSeparator());
            challengeText.append("• ");
            challengeText.append(s.name);
        }

        for(TimePeriod time : stage.timePeriods) {
            Calendar start = Calendar.getInstance();
            start.setTime(DataService.ParseDate(time.startTime));

            Calendar end = Calendar.getInstance();
            end.setTime(DataService.ParseDate(time.endTime));

            start.setTimeZone(TimeZone.getTimeZone(timezone));
            end.setTimeZone(TimeZone.getTimeZone(timezone));

            String timeWindowString = DataService.BuildTimeWindowString(start,end);

            challengeText.append(System.lineSeparator());
            challengeText.append(timeWindowString);
        }

        challengeText.append(System.lineSeparator());
        challengeText.append("> ");

        String regulation = stage.leagueMatchSetting.leagueMatchEvent.regulation.replaceAll("(<br />)+[^\\w\\d]+", System.lineSeparator()+"> ");

        challengeText.append(regulation);

        return new MessageEmbed.Field("__"+stage.leagueMatchSetting.leagueMatchEvent.name+"__", challengeText.toString(), false);
    }

    public static MessageEmbed.Field SalmonRunField(ScheduleNode stage, String timezone) {
        Calendar start = Calendar.getInstance();
        start.setTime(DataService.ParseDate(stage.startTime));

        Calendar end = Calendar.getInstance();
        end.setTime(DataService.ParseDate(stage.endTime));

        start.setTimeZone(TimeZone.getTimeZone(timezone));
        end.setTimeZone(TimeZone.getTimeZone(timezone));

        String timeHeader = DataService.BuildTimeWindowString(start,end) + " — " + stage.setting.coopStage.name;
        StringBuilder weapons = new StringBuilder();

        for(SalmonRunWeapon w : stage.setting.weapons) {
            if(weapons.length() > 0) {
                weapons.append(System.lineSeparator());
            }

            weapons.append("• ");
            weapons.append(w.name);
        }

        return new MessageEmbed.Field(timeHeader, weapons.toString(), false);
    }

    public static MessageEmbed.Field SplatfestField(Festival fest, String timezone) {
        Calendar start = Calendar.getInstance();
        start.setTime(DataService.ParseDate(fest.startTime));

        Calendar end = Calendar.getInstance();
        end.setTime(DataService.ParseDate(fest.endTime));

        start.setTimeZone(TimeZone.getTimeZone(timezone));
        end.setTimeZone(TimeZone.getTimeZone(timezone));

        String timeHeader = DataService.BuildTimeWindowString(start,end);
        StringBuilder detail = new StringBuilder();

        detail.append("__**");
        detail.append(fest.title);
        detail.append("**__");
        detail.append(" (");
        detail.append(fest.state);
        detail.append(")");
        detail.append(System.lineSeparator());
        detail.append("**");
        detail.append("• ");
        detail.append(fest.teams.get(0).teamName);
        detail.append(System.lineSeparator());
        detail.append("• ");
        detail.append(fest.teams.get(1).teamName);
        detail.append(System.lineSeparator());
        detail.append("• ");
        detail.append(fest.teams.get(2).teamName);
        detail.append("**");

        StringBuilder weapons = new StringBuilder();

        detail.append(weapons);

        return new MessageEmbed.Field(timeHeader, detail.toString(), false);
    }

    public static MessageEmbed.Field SplatfestStageField(ScheduleNode stage, String timezone) {
        Calendar start = Calendar.getInstance();
        start.setTime(DataService.ParseDate(stage.startTime));

        Calendar end = Calendar.getInstance();
        end.setTime(DataService.ParseDate(stage.endTime));

        start.setTimeZone(TimeZone.getTimeZone(timezone));
        end.setTimeZone(TimeZone.getTimeZone(timezone));

        String timeHeader = DataService.BuildTimeWindowString(start,end);
        StringBuilder stages = new StringBuilder();

        for(VsStage s : stage.festMatchSettings.vsStages) {
            if(!stages.isEmpty()) {
                stages.append(System.lineSeparator());
            }

            stages.append("• ");
            stages.append(s.name);
        }

        return new MessageEmbed.Field(timeHeader, stages.toString(), false);
    }
}
