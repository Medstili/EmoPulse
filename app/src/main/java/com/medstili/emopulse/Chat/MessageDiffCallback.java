package com.medstili.emopulse.Chat;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

class MessageDiffCallback extends DiffUtil.Callback {
    private final List<Message> oldList;
    private final List<Message> newList;

    public MessageDiffCallback(List<Message> oldList, List<Message> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Check if items represent the same logical entity (e.g., based on a unique ID)
        // If your Message class has a unique ID, use that.
        // For now, let's assume timestamp can be a unique identifier for simplicity,
        // but in a real app, a proper unique message ID is better.
        return oldList.get(oldItemPosition).getTimestamp() == newList.get(newItemPosition).getTimestamp();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Check if the content of the items is the same
        // This depends on what makes two Message objects visually identical
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        // Make sure your Message class has a well-defined equals() method.
    }

    // Optional: Implement getChangePayload if you want to pass specific change
    // information for more granular updates in onBindViewHolder.
    // @Nullable
    // @Override
    // public Object getChangePayload(int oldItemPosition, int newItemPosition) {
    //     // Implement method if you're going to use ItemAnimator
    //     return super.getChangePayload(oldItemPosition, newItemPosition);
    // }
}