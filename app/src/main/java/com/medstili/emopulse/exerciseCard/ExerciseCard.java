package com.medstili.emopulse.exerciseCard;

public class ExerciseCard {

    private String title,bgColor,cardScore,titleColor;
    private int imageResId;

    public ExerciseCard(int imageResId, String title,String cardScore, String bgColor,String titleColor) {
        this.imageResId = imageResId;
        this.title = title;
        this.cardScore = cardScore;
        this.bgColor = bgColor;
        this.titleColor = titleColor;
    }

    public String getBgColor() {
        return bgColor;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleColor() {
        return titleColor;
    }

    public String getCardScore() {
        return cardScore;
    }

    public int getImageResId() {
        return imageResId;
    }
}
