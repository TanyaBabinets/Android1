package itstep.learning.androidpv211;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
    private final Handler handler=new Handler();
    private SwitchCompat scRemember; //save nikname
    private boolean isFirstSend;//1st message
    private final String authorFilename="author.name";
    private static final String appDatabase="chat_db";
    private static final String channelId="CHAT-CHANNEL";
    private int postGranted=-1;


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
        updateChat();
        etAuthor=findViewById((R.id.chat_et_author));
        etAuthor.setText( loadAuthor());//если есть имя то подставится
        etMessage=findViewById((R.id.chat_et_message));
        scRemember=findViewById(R.id.chat_switch_remember);
        scRemember.setChecked(true);
        isFirstSend=true;//для поворота экрана сохранить
        //sms Id
        List<ChatMessage>dbMes=restoreMessages();
        restoreFromDB(dbMes);

        rvContent=findViewById((R.id.chat_rv_content));
        String myName=etAuthor.getText().toString().trim();//для сдвига моего смс вправо
        chatMessageAdapter=new ChatMessageAdapter(messages, myName);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvContent.setLayoutManager(layoutManager);
        rvContent.setAdapter(chatMessageAdapter);

        findViewById(R.id.chat_btn_send).setOnClickListener(this::onSendClick);
        restoreMessages();
        registerChannel();
        Intent intent=this.getIntent();
        if(intent!=null){
            String messageId=intent.getStringExtra("message_id");
            if(messageId !=null){
                Log.i("chat", "Forwarded from notification"+messageId);
            }

        }
    }

    //2 метода для уведомлений
    private void registerChannel(){
        NotificationChannel channel=new NotificationChannel(
                channelId,
                "Chat notifications",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("Notification about new incoming messages");
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }

    private void makeNotification(){

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED) {
            postGranted = 0;// не дозволено. то спрашиваем разрешение на уведомл.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        234);
            }
            return;//если нет разрешения то ничего не будет отправляться = уведомления

        }
        postGranted=1;//дозволено
        Intent intent = new Intent(this, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
           intent.putExtra("message_id", "123");

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder=new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.btn_star_big_on)
                .setContentTitle("Chat")
                .setContentText("New incoming message")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat
                .from(this)
                .notify(123, builder.build());
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
   if(requestCode==234){ //запрос на POST_NOTIFICATION
       if(grantResults[0] !=PackageManager.PERMISSION_GRANTED){
           Toast.makeText(this, "You won't see messages", Toast.LENGTH_SHORT).show();
           //запомнить выбор и не слать уведомл. системе
           postGranted=0;//no
       }
       else{postGranted=1;}//yes you can send push
      }
    }
private void updateChat(){
    CompletableFuture
            .supplyAsync(()->Services.fetchUrl(chatUrl), pool)
            .thenApply(this::parseChatResponse)//принимает стринг возвр.List//оператор :: перетворення
            .thenAccept(this::processChatResponse);//принимает List
    //  Log.i("updateChat", "updated");
    handler.postDelayed(this::updateChat, 2000);
}
    private void onSendClick(View view){
        String alertMessage=null;
        String author=etAuthor.getText().toString();
        String message=etMessage.getText().toString();
        if(author.isBlank()){
            alertMessage=getString(R.string.chat_msg_no_author);
        }

        else if(message.isBlank()){
            alertMessage=getString(R.string.chat_msg_no_text);
        }
        if (alertMessage!=null){
            new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_DayNight)
                    .setTitle(R.string.chat_msg_no_send)
                    .setMessage(alertMessage)
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton(R.string.chat_msg_no_send_btn, (dlg, btn)->{})
                    .setCancelable(false)
                    .show();
            return;
        }
        //перед запуском проверка на перове смс и сохр. ника
        if(isFirstSend){
            isFirstSend=false;
            if(scRemember.isChecked()){
saveAuthor(author);
            }
            etAuthor.setEnabled(false);// блокировка имени
        }
        etMessage.setText("");// clear input
        //запуск отправки:
CompletableFuture.runAsync(
        ()-> {
            try {
                sendChatMessage(new ChatMessage(author, message));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        },
        pool
);
    }

    private void saveMessages(){
       try( SQLiteDatabase db=openOrCreateDatabase(
                appDatabase, Context.MODE_PRIVATE, null)){
            db.execSQL("CREATE TABLE IF NOT EXISTS chat_history(id ROWID, author VARCHAR(128), text VARCHAR(512), moment DATETIME)");
            for (ChatMessage chatMessage : messages) {
                db.execSQL("INSERT INTO chat_history VALUES(?, ?, ?, ?)",
                        new Object[]{
                                Integer.parseInt(chatMessage.getId()),
                                chatMessage.getAuthor(),
                                chatMessage.getText(),
                                chatMessage.getMoment()
                        });
            }
        }
        catch (Exception ex){
    Log.e("saveMessages", ex.getClass().getName() + " " + ex.getMessage());
        }
    }

    // все сообщения
    private List<ChatMessage> restoreMessages(){
        List<ChatMessage> restorMes = new ArrayList<>();
        try( SQLiteDatabase db=openOrCreateDatabase(
                appDatabase, Context.MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("SELECT * FROM chat_history", null)){
         if(cursor.moveToFirst()){
             do{
                 messages.add(ChatMessage.fromCursor(cursor));
             }while(cursor.moveToNext());
         }

        }
        catch (Exception ex){
            Log.e("restoreMessages", ex.getClass().getName() + " " + ex.getMessage());
        }
        return restorMes;
    }
    private void saveAuthor(String name){
        try(FileOutputStream fos=openFileOutput(authorFilename, Context.MODE_PRIVATE)) {
fos.write( name.getBytes(StandardCharsets.UTF_8));
            } catch(IOException ex){
               Log.e("saveAuthor", "IOException "+ ex.getMessage());
            }
        }
    private String loadAuthor(){
        try(FileInputStream fis=openFileInput(authorFilename)) {
            return Services.readAllText( fis );

        } catch(IOException ex){
            Log.e("loadAuthor", "IOException "+ ex.getMessage());
        }
        return "";
    }

    private void sendChatMessage(ChatMessage chatMessage) throws UnsupportedEncodingException {
//отправка данных на примере формы
//                метод имитирует отпраку формы с полями author & msg
//                метод POST {chatUrl}
//        Content-Type:application/x-www-form-urlencoded
//          Accept:application/json
   //     Connection:close
//                author={author}&msg={message}
//        author=The Author&msg=Hello, All! --- WRONG!!
//        author=The%20Author&msg=Hello,%20All! ---WRITE! ==encoded

    String charset= StandardCharsets.UTF_8.name();
    try {
        String body = String.format(Locale.ROOT,
                "author=%s&msg=%s",
                URLEncoder.encode( chatMessage.getAuthor(), charset ),
                URLEncoder.encode( chatMessage.getText(), charset )
        );
        URL url=new URL (chatUrl); //делаем запрос
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);//ждем ответ (можно прочитать)
        connection.setDoOutput(true); //можно писать - передача данных = тело
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");//что передаем
        connection.setRequestProperty("Accept","application/json");//что ожидаем в ответ
        connection.setRequestProperty("Connection","close");
       connection.setChunkedStreamingMode(0);//не делить на части

        OutputStream bodyStream=connection.getOutputStream();
        bodyStream.write(body.getBytes(charset));//перевели в байты и запиали в тело
        bodyStream.flush();//сбрасываем
        bodyStream.close();

        //ответ получаем:
        int statusCode=connection.getResponseCode();
        if(statusCode==201){
            //данный сервер не дает тело, если оно нужно то читем connection.getInputStream();
updateChat();
        } else{
            //ответ с ошибкой передается через connection.getErrorStream();
            InputStream errorStream=connection.getErrorStream();
            Log.e("sendChatMessage", Services.readAllText(errorStream));
            errorStream.close();
        }
connection.disconnect();
    }catch (UnsupportedEncodingException ex){
        Log.e("sendChatMessage", "UnsupportedEncodingException "+ex.getMessage());
    } catch (MalformedURLException ex) {
        Log.e("sendChatMessage", "MalformedURLException "+ex.getMessage());
    } catch (IOException ex) {
        Log.e("sendChatMessage", "IOException "+ex.getMessage());
    }

    }



    private void processChatResponse(List<ChatMessage> parsedMessages) {
        int oldSize = messages.size();
        for (ChatMessage m : parsedMessages) {
            if (messages.stream().noneMatch(cm -> cm.getId().equals(m.getId()))) {
                messages.add(m);
            }
        }
        int newSize = messages.size();//отдельная переменная теперь
        //если есть новые сообщения
        if (newSize > oldSize) {

            messages.sort(Comparator.comparing(ChatMessage::getMoment));
            runOnUiThread(() -> {
                chatMessageAdapter.notifyItemRangeChanged(oldSize, newSize);
                rvContent.scrollToPosition(newSize - 1);
                if(oldSize !=0 && postGranted !=0){
                    makeNotification();
                }
            });
//etAuthor.setText(messages.size()+""));
        }
    }


    // проверяем есть ли смс с таким же id - получаем из базы список смс
    private void restoreFromDB(List<ChatMessage> dbMes){

        int oldSize=messages.size();
        for(ChatMessage m:dbMes){
            if(messages.stream().noneMatch(cm->cm.getId().equals(m.getId()))){
                messages.add(m);//если нет то добавляем
            }
        }
        int newSize=messages.size();

        if (newSize > oldSize) {

            messages.sort(Comparator.comparing(ChatMessage::getMoment));
            runOnUiThread(() -> {
                chatMessageAdapter.notifyItemRangeChanged(oldSize, newSize);
                rvContent.scrollToPosition(newSize - 1);
            });
        }
        Log.i("chatDB", "Загружено из БД: " + dbMes.size());
        Log.i("chatDB", "До фильтрации было: " + oldSize + ", стало: " + messages.size());
    }
    private List<ChatMessage> parseChatResponse(String body) {
        Log.i("chat", "Вызван метод parseChatResponse");
        List<ChatMessage> res=new ArrayList<>();
        try {
            JSONObject root =new JSONObject(body);
            //TODO:check root status
            int status=root.getInt("status");
            Log.i("chat", "Статус ответа: " + status);
            if(status!=1){
                Log.w("chat", "status " + status);
                return res;//остановка метода
            }


            JSONArray arr = root.getJSONArray("data");

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
        handler.removeMessages(0);//delete ALL
        pool.shutdownNow();
        saveMessages();
        super.onDestroy();//close thread
    }
};