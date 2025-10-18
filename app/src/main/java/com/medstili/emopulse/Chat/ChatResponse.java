package com.medstili.emopulse.Chat;

import java.util.List;
import java.util.Map;

public class ChatResponse {
    public String reply;
    public List<String> tools_called;
    public Map<String,Object> tool_inputs;
    public String audio_url;
}
