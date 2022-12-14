package com.hadenwatne.splatbot.models.data.stages;

import java.util.ArrayList;
import java.util.List;

public class RankedStages {
    private String startTime;
    private String endTime;

    private List<RankedMode> modes;

    public RankedStages(String start, String end, List<RankedMode> modes) {
        this.startTime = start.replace("Z", "+0000");;
        this.endTime = end.replace("Z", "+0000");;
        this.modes = modes;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public List<RankedMode> getModes() {
        return modes;
    }
}
