package com.hadenwatne.splatbot.models.data.stages;

import java.util.ArrayList;
import java.util.List;

public class Splatfest {
    private String startTime;
    private String endTime;
    private String title;
    private String status;
    private String thumbnail;
    private List<String> teams;

    public Splatfest(String start, String end, String title, String status, String image) {
        this.startTime = start.replace("Z", "+0000");
        this.endTime = end.replace("Z", "+0000");
        this.title = title;
        this.status = status;
        this.thumbnail = image;
        this.teams = new ArrayList<>();
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public List<String> getTeams() {
        return teams;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
