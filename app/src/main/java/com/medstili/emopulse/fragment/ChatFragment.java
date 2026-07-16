package com.medstili.emopulse.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.medstili.emopulse.AgentService.AgentService;
import com.medstili.emopulse.Chat.ChatAdapter;
import com.medstili.emopulse.Chat.Message;
import com.medstili.emopulse.Chat.ChatResponse;
import com.medstili.emopulse.DataBase.DataBase;
import com.medstili.emopulse.databinding.FragmentChatBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit; // ADDED IMPORT

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.medstili.emopulse.BuildConfig;

public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private ChatAdapter adapter;
    private final List<Message> messages = new ArrayList<>();
    private DatabaseReference messagesRef;
    private AgentService agentService;
    private String userId;
    private static final String TAG = "ChatFragment";
    private DataBase db ;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        db = DataBase.getInstance();

        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        messagesRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("chats")
                .child("messages");

        adapter = new ChatAdapter(messages, requireContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        // MODIFIED OkHttpClient with timeouts
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();

        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.AGENT_SERVICE_BASE_URL)
                    .client(okHttpClient) // Use the customized OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Log.d(TAG, "Base URL: " + BuildConfig.AGENT_SERVICE_BASE_URL);
            agentService = retrofit.create(AgentService.class);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving base URL", e);
        }



        messagesRef.orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        List<Message> newMessages = new ArrayList<>();
                        for (DataSnapshot msgSnap : snap.getChildren()) {
                            Message m = msgSnap.getValue(Message.class);
                            if (m != null) {
                                newMessages.add(m);
                            }
                        }
                        adapter.updateMessages(newMessages);
                        if (!newMessages.isEmpty()) {
                            binding.recyclerView.scrollToPosition(newMessages.size() - 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        Log.e(TAG, "Firebase onCancelled: ", e.toException());
                    }
                });

        binding.userInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                boolean has = !TextUtils.isEmpty(s);
                binding.audioButton.setVisibility(has ? View.GONE : View.VISIBLE);
                binding.sendButton.setVisibility(has ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.sendButton.setOnClickListener(v -> {
            String text = binding.userInputEditText.getText().toString().trim();
            if (text.isEmpty()) return;
            binding.userInputEditText.setText("");

            Message userMsg = new Message(text, true, false, null, 0, ServerValue.TIMESTAMP);
            Log.d(TAG, "Attempting to send user message: " + text);
            String userMsgId = messagesRef.push().getKey();
            Log.d(TAG, "User message ID: " + userMsgId + ", User ID: " + userId);

            try {
                agentService.chat(new AgentService.ChatRequest(userId, text))
                        .enqueue(new Callback<ChatResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<ChatResponse> call,
                                                   @NonNull Response<ChatResponse> resp) {
                                Log.d(TAG, "agentService.onResponse called.");
                                Log.d(TAG, "Response successful: " + resp.isSuccessful());
                                Log.d(TAG, "Response code: " + resp.code());
                                Log.d(TAG, "Response message: " + resp.message());

                                if (!resp.isSuccessful()) {
                                    if (resp.errorBody() != null) {
                                        try {
                                            Log.e(TAG, "Error body: " + resp.errorBody().string());
                                        } catch (IOException e) {
                                            Log.e(TAG, "Error parsing error body", e);
                                        }
                                    } else {
                                        Log.e(TAG, "Error body is null.");
                                    }
                                    Snackbar.make(binding.getRoot(), "Error: " + resp.code() + " " + resp.message(), 3000).show();
                                    return;
                                }

                                if (resp.body() == null) {
                                    Log.e(TAG, "Response body is null.");
                                    Snackbar.make(binding.getRoot(), "Error: Empty response from server", 3000).show();
                                    return;
                                }

                                ChatResponse cr = resp.body();
                                Log.d(TAG, "agentService received reply: " + cr.reply);
                                Message LellyMsg = new Message(
                                        cr.reply,
                                        false,
                                        cr.audio_url != null,
                                        cr.audio_url,
                                        0,
                                        ServerValue.TIMESTAMP
                                );
                                String LellyMsgId = messagesRef.push().getKey();

                                if (LellyMsgId != null && userMsgId != null) {
                                    messagesRef.child(userMsgId).setValue(userMsg);
                                    messagesRef.child(LellyMsgId).setValue(LellyMsg);
                                    db.incrementMessageCount();
                                    Log.d(TAG, "User and Lelly messages pushed to Firebase.");
                                } else {
                                    Log.e(TAG, "Failed to get push ID for Lelly message or user message ID was null.");
                                    Snackbar.make(binding.getRoot(), "Error! Try Again Later", 3000).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ChatResponse> call, @NonNull Throwable t) {
                                Log.e(TAG, "agentService.onFailure called.", t);
                                Snackbar.make(binding.getRoot(), "Network Error: " + t.getMessage(), 3000).show();
                            }
                        });
            }
            catch ( Exception e) {
                Log.e(TAG, "Exception while sending message", e);
                Snackbar.make(binding.getRoot(), "Error sending message please try again later: ", 3000)
                        .setBackgroundTint(getResources().getColor(android.R.color.holo_red_dark))
                        .setTextColor(getResources().getColor(android.R.color.white)
                ).show();
            }

        });

        return binding.getRoot();
    }
}
