package com.hadenwatne.splatbot.models.data.stages;

import java.util.ArrayList;
import java.util.List;

public class SalmonRunStages {
    private String startTime;
    private String endTime;
    private String stage;
    private List<String> weapons;

    public SalmonRunStages(String start, String end, String stage, List<String> weapons) {
        this.startTime = start.replace("Z", "+0000");;
        this.endTime = end.replace("Z", "+0000");;
        this.stage = stage;
        this.weapons = weapons;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStage() {
        return stage;
    }

    public List<String> getWeapons() {
        return weapons;
    }
}
