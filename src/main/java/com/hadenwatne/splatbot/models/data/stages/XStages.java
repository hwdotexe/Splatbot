package com.hadenwatne.splatbot.models.data.stages;

import java.util.ArrayList;
import java.util.List;

public class XStages {
    private String startTime;
    private String endTime;
    private String gameMode;
    private List<String> stages;

    public XStages(String start, String end, String gameMode) {
        this.startTime = start.replace("Z", "+0000");;
        this.endTime = end.replace("Z", "+0000");;
        this.gameMode = gameMode;
        this.stages = new ArrayList<>();
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getGameMode() {
        return gameMode;
    }

    public List<String> getStages() {
        return stages;
    }
}
