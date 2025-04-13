package itstep.learning.androidpv211;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import itstep.learning.androidpv211.chat.ChatMessageAdapter;
import itstep.learning.androidpv211.orm.ChatMessage;
import itstep.learning.androidpv211.orm.NbuRate;

public class ChatActivity extends AppCompatActivity {
    private static final String chatUrl="https://chat.momentfor.fun/";

    private ExecutorService pool;
    private final List<ChatMessage> messages=new ArrayList<>();
    private EditText etAuthor;//20 messages

    private EditText etMessage;
    private RecyclerView rvContent;
    private ChatMessageAdapter chatMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeBars = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, Math.max(systemBars.bottom, imeBars.bottom)
            );
            return insets;
        });
        pool= Executors.newFixedThreadPool(3);
        CompletableFuture
                .supplyAsync(()->Services.fetchUrl(chatUrl), pool)
                .thenApply(this::parseChatResponse)//принимает стринг возвр.List//оператор :: перетворення
                .thenAccept(this::processChatResponse);//принимает List
        etAuthor=findViewById((R.id.chat_et_author));
etMessage=findViewById((R.id.chat_et_message));
        rvContent=findViewById((R.id.chat_rv_content));
        chatMessageAdapter=new ChatMessageAdapter(messages);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvContent.setLayoutManager(layoutManager);
        rvContent.setAdapter(chatMessageAdapter);

        findViewById(R.id.chat_btn_send).setOnClickListener(this::onSendClick);
    }

    private void onSendClick(View view){
        String alertMessage=null;
        String author=etAuthor.getText().toString();
        String message=etMessage.getText().toString();
        if(author.isBlank()){
            alertMessage="Enter your nickname";
        }

        else if(message.isBlank()){
            alertMessage="Enter your message";
        }
        if (alertMessage!=null){
            new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_DayNight)
                    .setTitle("Sending is cancelled")
                    .setMessage(alertMessage)
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton("OK", (dlg, btn)->{})
                    .setCancelable(false)
                    .show();
            return;
        }

    }

private void processChatResponse(List<ChatMessage> parsedMessages){
        int oldSize=messages.size();
for(ChatMessage m:parsedMessages){
    if (messages.stream().noneMatch(cm -> cm.getId().equals(m.getId()))) {
    messages.add(m);
    }
    }
messages.sort(Comparator.comparing(ChatMessage::getMoment));
runOnUiThread(()-> {
    chatMessageAdapter.notifyItemRangeChanged(oldSize, messages.size());
});
//etAuthor.setText(messages.size()+""));
}



    private List<ChatMessage> parseChatResponse(String body) {
        List<ChatMessage> res=new ArrayList<>();
        try {
            JSONObject root =new JSONObject(body);
            //TODO:check root status
            JSONArray arr = root.getJSONArray("data");
            // nbuRates = new ArrayList<>(); new List написали вверху
            int len = arr.length();
            for (int i = 0; i < len; i++) {

                res.add(
                        ChatMessage.fromJsonObject(arr.getJSONObject(i))
                );
            }
        } catch (JSONException ex) {
            Log.d("parseCHATResponse", "JSONException " + ex.getMessage());
        }
        return res;
    }

    @Override
    protected void onDestroy(){
        pool.shutdownNow();
        super.onDestroy();//close thread
    }
}