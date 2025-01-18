package com.medstili.emopulse.exerciseCard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.medstili.emopulse.R;

import java.util.ArrayList;
import java.util.List;

public class ExerciseCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<ExerciseCard> cardList;
    private final Context content;

    public ExerciseCardAdapter(List<ExerciseCard> cardList, Context content) {
        this.cardList = (cardList != null) ? cardList : new ArrayList<>();
        this.content = content;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view = LayoutInflater.from(content).inflate(R.layout.exercise_card, parent, false);

       return new ExerciseCardAdapter.ExerciseCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ExerciseCard cardContainer = cardList.get(position);
        ((ExerciseCardAdapter.ExerciseCardViewHolder) holder).bind(cardContainer);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class ExerciseCardViewHolder extends RecyclerView.ViewHolder {

        TextView title,score;
        ImageView image;
        MaterialCardView card;

        public ExerciseCardViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.cardTitle);
            score = itemView.findViewById(R.id.cardScore);
            image = itemView.findViewById(R.id.cardImage);
            card = itemView.findViewById(R.id.cardContainer);
        }

        public void bind(ExerciseCard cardContainer){

             title.setText(cardContainer.getTitle());
             title.setTextColor(Color.parseColor(cardContainer.getTitleColor()));
             score.setText(cardContainer.getCardScore());
             image.setImageResource(cardContainer.getImageResId());
             card.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(cardContainer.getBgColorr())));
        }

    }
}
