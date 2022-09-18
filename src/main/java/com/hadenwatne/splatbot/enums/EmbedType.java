package com.hadenwatne.splatbot.enums;

import java.awt.*;

public enum EmbedType {
    TURFWAR(new Color(61, 153, 60)),
    RANKED(new Color(170, 23, 92)),
    SALMONRUN(new Color(188, 101, 20)),
    ERROR(new Color(209, 5, 19)),
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
