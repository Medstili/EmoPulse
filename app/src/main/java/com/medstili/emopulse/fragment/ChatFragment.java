package com.medstili.emopulse.fragment;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.medstili.emopulse.Chat.ChatAdapter;
import com.medstili.emopulse.Chat.Message;
import com.medstili.emopulse.R;
import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {
    View view;

    MaterialButton audioButton, sendButton ;
    EditText userInputEditText;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chat, container, false);

//finding views by its id and assigning them to variables
        userInputEditText =view.findViewById(R.id.userInputEditText);
        audioButton = view.findViewById(R.id.audioButton);
        sendButton = view.findViewById(R.id.sendButton);
        recyclerView = view.findViewById(R.id.recyclerView);

//        hiding teh audio button while typing on edittext
        userInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                check if teh user is typing something
                if (charSequence.length() > 0) {
                    audioButton.setVisibility(View.GONE);//hide the audio button
                    sendButton.setVisibility(View.VISIBLE);//show the send button
                } else {
                    audioButton.setVisibility(View.VISIBLE);// show the audio button
                    sendButton.setVisibility(View.GONE);// hide the send button
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, getContext());
        recyclerView.setAdapter(chatAdapter);
//        data for testing the chat
        addMessage("Hello, how can I help you?", false, false);
        addMessage("Hi! I'd like to know more about your services.", true, false);
        addMessage("Hi, how can I help you?", false, false);
        addMessage("I need help with my account.", true, false);
        addMessage("Sure! What seems to be the problem with your account?", false, false);
        addMessage("I forgot my password.", true, false);
        addMessage("No worries, I can assist you with resetting your password.", false, false);
        addMessage("How can I reset it?", true, false);
        addMessage("I will guide you step-by-step. First, go to the 'Forgot Password' page.", false, false);
        addMessage("Okay, I'm on the page. What next?", true, false);
        addMessage("Great! Now, enter your registered email address and click 'Submit'.", false, false);
        addMessage("Done! I received the email. Thanks for your help!", true, false);
        addMessage("You're welcome! Let me know if you need further assistance.", false, false);
        addMessage("No, that's all for now. Have a great day!", true, false);
        addMessage("Thank you, have a wonderful day too!", false, false);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userInputEditText.getText().toString().trim().isEmpty()){
                    String input = userInputEditText.getText().toString().trim();
                    addMessage(input, true, false);
                    userInputEditText.setText("");
                    Log.d("MessageList", "Message List: " + messageList.toString());
                }

            }
        });
//
        return view;
    }


//    add message function
        private void addMessage(String s, boolean isUser, boolean isAduio) {
        Message message = new Message(s, isUser, isAduio, null, 0);
        messageList.add(message);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
}

}