package com.hadenwatne.splatbot.models.gameData.schedules;

import java.util.List;

public class ScheduleNode {
    public String startTime;
    public String endTime;
    public MatchSetting regularMatchSetting;
    public List<MatchSetting> bankaraMatchSettings;
    public MatchSetting xMatchSetting;
    public SalmonRunSetting setting;
    public ChallengeSetting leagueMatchSetting;
    public List<TimePeriod> timePeriods;
}