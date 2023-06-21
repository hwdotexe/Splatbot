package com.hadenwatne.splatbot.models.data.stages;

public class ChallengeEventTime {
    private String startTime;
    private String endTime;

    public ChallengeEventTime(String start, String end) {
        this.startTime = start.replace("Z", "+0000");
        this.endTime = end.replace("Z", "+0000");
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
