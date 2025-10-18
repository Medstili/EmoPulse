package com.medstili.emopulse.AgentService;
import com.medstili.emopulse.Chat.ChatResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AgentService {
    class ChatRequest {
        public String userId;
        public String message;
        public ChatRequest(String userId, String message) {
            this.userId = userId;
            this.message = message;
        }
    }

    @Headers("Content-Type: application/json")
    @POST("/chat")
    Call<ChatResponse> chat(@Body ChatRequest req);
}
