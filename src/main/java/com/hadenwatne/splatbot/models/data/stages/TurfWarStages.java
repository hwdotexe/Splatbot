package com.hadenwatne.splatbot.models.data.stages;

import java.util.ArrayList;
import java.util.List;

public class TurfWarStages {
    private String startTime;
    private String endTime;
    private List<String> stages;

    public TurfWarStages(String start, String end) {
        this.startTime = start.replace("Z", "+0000");;
        this.endTime = end.replace("Z", "+0000");;
        this.stages = new ArrayList<>();
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public List<String> getStages() {
        return stages;
    }
}
