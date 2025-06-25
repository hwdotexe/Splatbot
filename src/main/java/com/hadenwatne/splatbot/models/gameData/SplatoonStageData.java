package com.hadenwatne.splatbot.models.gameData;

import com.hadenwatne.splatbot.models.gameData.schedules.GameData;
import com.hadenwatne.splatbot.models.gameData.splatfests.USFestival;

public class SplatoonStageData {
    private USFestival splatfests;
    private GameData regular;

    public SplatoonStageData(GameData regular, USFestival splatfests) {
        this.splatfests = splatfests;
        this.regular = regular;
    }

    public USFestival getSplatfests() {
        return splatfests;
    }

    public GameData getRegular() {
        return regular;
    }
}
