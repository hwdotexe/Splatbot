package com.hadenwatne.splatbot.models.data.stages;

import java.util.ArrayList;
import java.util.List;

public class RankedMode {
    private String mode;
    private List<String> stages;

    public RankedMode(String mode) {
        this.mode = mode;
        this.stages = new ArrayList<>();
    }

    public String getMode() {
        return mode;
    }

    public List<String> getStages() {
        return stages;
    }
}
