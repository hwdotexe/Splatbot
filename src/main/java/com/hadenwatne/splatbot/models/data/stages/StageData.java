package com.hadenwatne.splatbot.models.data.stages;

import java.util.ArrayList;
import java.util.List;

public class StageData {
    private List<TurfWarStages> turfWar;
    private List<RankedStages> ranked;
    private List<XStages> xRank;
    private List<SalmonRunStages> salmonRun;
    private List<SplatfestStages> splatfestStages;
    private List<Splatfest> splatFests;
    private List<ChallengeEvent> challengeEvents;

    public StageData() {
        this.turfWar = new ArrayList<>();
        this.ranked = new ArrayList<>();
        this.xRank = new ArrayList<>();
        this.salmonRun = new ArrayList<>();
        this.splatfestStages = new ArrayList<>();
        this.splatFests = new ArrayList<>();
        this.challengeEvents = new ArrayList<>();
    }

    public List<TurfWarStages> getTurfWar() {
        return turfWar;
    }

    public List<RankedStages> getRanked() {
        return ranked;
    }

    public List<XStages> getXRanked() {
        return xRank;
    }

    public List<SalmonRunStages> getSalmonRun() {
        return salmonRun;
    }

    public List<SplatfestStages> getSplatfestStages() {
        return splatfestStages;
    }

    public List<Splatfest> getSplatFests() {
        return splatFests;
    }

    public List<ChallengeEvent> getChallengeEvents() {
        return challengeEvents;
    }
}
