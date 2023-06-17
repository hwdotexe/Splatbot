package com.hadenwatne.splatbot.models.data;

import com.hadenwatne.splatbot.enums.PostType;

public class StickyPost {
    private long channelID;
    private long messageID;
    private PostType type;
    private String timezone;

    public StickyPost(long channel, long message, PostType type, String timezone) {
        this.channelID = channel;
        this.messageID = message;
        this.type = type;
        this.timezone = timezone;
    }

    public long getChannelID() {
        return channelID;
    }

    public long getMessageID() {
        return messageID;
    }

    public PostType getType() {
        return type;
    }

    public String getTimezone() {
        return timezone;
    }
}
