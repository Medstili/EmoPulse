package com.medstili.emopulse.Chat;

public class Message {

    private String content;
    private boolean isUser;
    private boolean isAudio;
    private String audioUrl;
    private long duration;
    private boolean isLiked = false;
    private boolean isDisliked = false;

    public Message(String content, boolean isUser, boolean isAudio, String audioUrl, long duration) {
        this.content = content;
        this.isUser = isUser;
        this.isAudio = isAudio;
        this.audioUrl = audioUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public long getDuration() {
        return duration;
    }

    public String getContent() {
        return content;
    }

    public boolean isUser() {
        return isUser;
    }

    public boolean isAudio() {
        return isAudio;
    }

    public boolean isLiked() {
        return isLiked;
    }
    public boolean isDisliked() {
        return isDisliked;
    }
    public void setLiked(boolean liked) {
        isLiked = liked;
    }
    public void setDisliked(boolean disliked) {
        isDisliked = disliked;
    }
}
