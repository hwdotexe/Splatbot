package com.hadenwatne.splatbot.enums;

import java.awt.*;

public enum EmbedType {
    TURFWAR(new Color(61, 163, 60)),
    RANKED(new Color(226, 88, 29)),
    SALMONRUN(new Color(216, 98, 62)),
    ERROR(new Color(195, 5, 19)),
    CHALLENGE(new Color(170, 23, 92)),
    SUMMARY(new Color(39, 78, 226)),
    INFO(Color.yellow),
    EXPIRED(Color.lightGray);

    private final Color color;

    EmbedType(Color color){
        this.color = color;
    }

    public Color getColor(){
        return this.color;
    }
}
