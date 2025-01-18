package com.medstili.emopulse.Chat;


import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.medstili.emopulse.R;

import java.io.IOException;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TEXT_MESSAGE_USER = 0;
    private static final int TEXT_MESSAGE_BOT = 1;
    private static final int AUDIO_MESSAGE_USER = 2;
    private static final int AUDIO_MESSAGE_BOT = 3;
    private final List<Message> messageList;
    private final Context content;
//constructor
    public ChatAdapter(List<Message> messageList, Context content) {
        this.messageList = messageList;
        this.content = content;
    }

//    verifying which layout to inflate
    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.isAudio()) {
            return message.isUser() ? AUDIO_MESSAGE_USER : AUDIO_MESSAGE_BOT;
        } else {
            return message.isUser() ? TEXT_MESSAGE_USER : TEXT_MESSAGE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case  TEXT_MESSAGE_BOT:
                view = LayoutInflater.from(content).inflate(R.layout.bot_text_message, parent, false);
                return new TextMessageViewHolder(view);
            case TEXT_MESSAGE_USER:
                view = LayoutInflater.from(content).inflate(R.layout.user_text_message, parent, false);
                return new TextMessageViewHolder(view);
            case  AUDIO_MESSAGE_BOT:
                view = LayoutInflater.from(content).inflate(R.layout.bot_audio_message, parent, false);
                return new AudioMessageViewHolder(view);
            case AUDIO_MESSAGE_USER:
                view = LayoutInflater.from(content).inflate(R.layout.user_audio_message, parent, false);
                return new AudioMessageViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid message type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message = messageList.get(position);

        if(holder instanceof  TextMessageViewHolder){
            ((TextMessageViewHolder) holder).bind(message);
        }
        else if(holder instanceof AudioMessageViewHolder){
            ((AudioMessageViewHolder) holder).bind(message);
        }
//
//        if (message.isLiked()) {
//            holder.like.setImageResource(R.drawable.filled_hand_like); // Highlighted like
//        }
//        else {
//            holder.like.setImageResource(R.drawable.outline_hand_like); // Default like
//            holder.
//        }
//
//        // Update dislike button based on state
//        if ( message.isDisliked()) {
//            holder.dislike.setImageResource(R.drawable.filled_hand_dislike); // Highlighted dislike
//        }
//        else {
//            holder.dislike.setImageResource(R.drawable.outline_hand_dislike); // Default dislike
//        }
//
//        // Like button click
//        holder.like.setOnClickListener(v -> {
//            message.setLiked(!message.isLiked()); // Toggle like
//            message.setDisliked(false); // Reset dislike if liked
//            notifyItemChanged(position); // Update item
//        });
//
//        // Dislike button click
//        holder.dislike.setOnClickListener(v -> {
//            message.setDisliked(!message.isDisliked()); // Toggle dislike
//            message.setLiked(false); // Reset like if disliked
//            notifyItemChanged(position); // Update item
//        });

    };
    @Override
    public int getItemCount() {
        return messageList.size();
    };

    public class TextMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textview;
        ShapeableImageView like, dislike;
        public TextMessageViewHolder(View itemView ) {
            super(itemView);
            textview = itemView.findViewById(R.id.textMessage);
                like = itemView.findViewById(R.id.like);
                dislike = itemView.findViewById(R.id.dislike);
        }
        public void bind(Message message) {
            textview.setText(message.getContent());
            if(dislike!=null && like!=null){
                like.setImageResource(message.isLiked() ? R.drawable.filled_hand_like : R.drawable.outline_hand_like);
                dislike.setImageResource(message.isDisliked() ? R.drawable.filled_hand_dislike : R.drawable.outline_hand_dislike);

                like.setOnClickListener(v -> {
                    message.setLiked(!message.isLiked());
                    message.setDisliked(false);
                    notifyItemChanged(getAdapterPosition());
                });
                dislike.setOnClickListener(v -> {
                    message.setDisliked(!message.isDisliked());
                    message.setLiked(false);
                    notifyItemChanged(getAdapterPosition());
                });
            };
        }
        }

    public class AudioMessageViewHolder extends RecyclerView.ViewHolder {
        TextView audioDuration;
        ImageView audioPlayButton;
        ShapeableImageView like, dislike;
//        constructor
        public AudioMessageViewHolder(View itemView) {
            super(itemView);
            audioDuration = itemView.findViewById(R.id.audioDuration);
            audioPlayButton = itemView.findViewById(R.id.audioPlayButton);
            like = itemView.findViewById(R.id.like);
            dislike = itemView.findViewById(R.id.dislike);
        }
//        binding data to view
        public void bind(Message message) {
            audioDuration.setText(formatDuration(message.getDuration()));
            audioPlayButton.setOnClickListener(v -> playAudio(message.getAudioUrl()));
            if(dislike!=null && like!=null){
                like.setImageResource(message.isLiked() ? R.drawable.filled_hand_like : R.drawable.outline_hand_like);
                dislike.setImageResource(message.isDisliked() ? R.drawable.filled_hand_dislike : R.drawable.outline_hand_dislike);

                like.setOnClickListener(v -> {
                    message.setLiked(!message.isLiked());
                    message.setDisliked(false);
                    notifyItemChanged(getAdapterPosition());
                });
                dislike.setOnClickListener(v -> {
                    message.setDisliked(!message.isDisliked());
                    message.setLiked(false);
                    notifyItemChanged(getAdapterPosition());
                });
            };
        }

        }
//    converting milliseconds to minutes and seconds (M:S)
    private String formatDuration(long durationInMillis) {
        long minutes = (durationInMillis / 1000) / 60;
        long seconds = (durationInMillis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
//     playing the audio
    private void playAudio(String audioUrl) {
        // Implement audio playback using MediaPlayer
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}