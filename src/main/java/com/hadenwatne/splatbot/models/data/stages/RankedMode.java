package com.hadenwatne.splatbot.models.data.stages;

import java.util.ArrayList;
import java.util.List;

public class RankedMode {
    private String game;
    private String mode;
    private List<String> stages;

    public RankedMode(String game, String mode) {
        this.game = game;
        this.mode = mode.equals("CHALLENGE") ? "Series" : "Open";
        this.stages = new ArrayList<>();
    }

    public String getGamemode() {
        return game;
    }

    public String getMode() {
        return mode;
    }

    public List<String> getStages() {
        return stages;
    }
}
