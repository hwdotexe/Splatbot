package com.hadenwatne.splatbot.models.gameData.splatfests;

import com.hadenwatne.splatbot.models.gameData.StageImage;

import java.util.List;

public class Festival {
    public String id;
    public String state;
    public String startTime;
    public String endTime;
    public String title;
    public String lang;
    public StageImage image;
    public List<Team> teams;
}
