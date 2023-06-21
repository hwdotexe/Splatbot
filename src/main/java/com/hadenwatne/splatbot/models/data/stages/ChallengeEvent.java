package com.hadenwatne.splatbot.models.data.stages;

import java.util.ArrayList;
import java.util.List;

public class ChallengeEvent {
    private List<ChallengeEventTime> times;
    private String name;
    private String description;
    private String regulation;
    private List<String> stages;
    private String gameMode;

    public ChallengeEvent(String name, String description, String regulation, String gameMode) {
        this.name = name;
        this.description = description;
        this.regulation = regulation;
        this.gameMode = gameMode;
        this.stages = new ArrayList<>();
        this.times = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRegulation() {
        return regulation;
    }

    public String getGameMode() {
        return gameMode;
    }

    public List<String> getStages() {
        return stages;
    }

    public List<ChallengeEventTime> getTimes() {
        return times;
    }
}
