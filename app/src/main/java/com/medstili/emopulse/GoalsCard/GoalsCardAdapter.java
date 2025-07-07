package com.medstili.emopulse.GoalsCard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import com.medstili.emopulse.R;


import java.util.ArrayList;
import java.util.List;


public class GoalsCardAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<GoalsCard> goalsList;
    private final Context content ;

    public GoalsCardAdapter(List<GoalsCard> cardList, Context content) {
        this.goalsList = (cardList != null)? cardList : new ArrayList<>();
        this.content = content;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(content).inflate(R.layout.goal_card, parent, false);
        return new GoalsCardAdapter.GoalsCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        GoalsCard cardContainer = goalsList.get(position);
        ((GoalsCardAdapter.GoalsCardViewHolder) holder).bind(cardContainer);
    }

    @Override
    public int getItemCount() {
        return  goalsList.size();
    }

    public class GoalsCardViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        public GoalsCardViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.goal_title);
            description = itemView.findViewById(R.id.goals_description);
            itemView.setOnClickListener(v->{
                Bundle bundle = new Bundle();
                bundle.putString("title", title.getText().toString());
                bundle.putString("description", description.getText().toString());
                bundle.putString("goalId", goalsList.get(getAdapterPosition()).getGoalId());
                Navigation.findNavController(v).navigate(R.id.action_goalsFragment_to_goalDetails, bundle);
            });
        }
        public void bind(GoalsCard cardContainer){
            title.setText(cardContainer.getTitle());
            description.setText(cardContainer.getDescription());



        }
    }
}
