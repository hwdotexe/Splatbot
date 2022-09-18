package com.hadenwatne.splatbot.models.data.stages;

import java.util.ArrayList;
import java.util.List;

public class StageData {
    private List<TurfWarStages> turfWar;
    private List<RankedStages> ranked;
    private List<SalmonRunStages> salmonRun;

    public StageData() {
        this.turfWar = new ArrayList<>();
        this.ranked = new ArrayList<>();
        this.salmonRun = new ArrayList<>();
    }

    public List<TurfWarStages> getTurfWar() {
        return turfWar;
    }

    public List<RankedStages> getRanked() {
        return ranked;
    }

    public List<SalmonRunStages> getSalmonRun() {
        return salmonRun;
    }
}
