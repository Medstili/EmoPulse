package com.medstili.emopulse.Chat;

import com.google.firebase.database.PropertyName;

import java.util.Map;

public class Message {
    public String   reply;
    public boolean  isUser;
    public boolean  isAudio;
    public String   audioUrl;
    public long     duration;
    public Object   timestamp;

    // For likes/dislikes (optional)
    public boolean isLiked    = false;
    public boolean isDisliked = false;

    public Message() {} // required for Firebase

    public Message(String reply,
                   boolean isUser,
                   boolean isAudio,
                   String audioUrl,
                   long duration,
                   Object timestamp) {
        this.reply     = reply;
        this.isUser    = isUser;
        this.isAudio   = isAudio;
        this.audioUrl  = audioUrl;
        this.duration  = duration;
        this.timestamp = timestamp;
    }

    public String getReply() {
        return reply;
    }
    @PropertyName("isUser")
    public boolean isUser() {
        return isUser;
    }
    @PropertyName("isAudio")
    public boolean isAudio() {
        return isAudio;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public long getDuration() {
        return duration;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    @PropertyName("isLiked")
    public boolean isLiked() {
        return isLiked;
    }
    @PropertyName("isDisliked")
    public boolean isDisliked() {
        return isDisliked;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    @PropertyName("isUser")
    public void setUser(boolean user) {
        isUser = user;
    }
    @PropertyName("isAudio")
    public void setAudio(boolean audio) {
        isAudio = audio;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    @PropertyName("isLiked")
    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    @PropertyName("isDisliked")
    public void setDisliked(boolean disliked) {
        isDisliked = disliked;
    }
}
