package itstep.learning.androidpv211.orm;

import android.database.Cursor;
import android.icu.util.Calendar;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {
    public static final SimpleDateFormat dateFormat=
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
    public static final SimpleDateFormat sqliteFormat= //Tue Apr 15 15:22:09 GMT 2025
            new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.ENGLISH);

    private String id;
    private String author;
    private String text;
    private Date moment;
    public boolean isMine(String myName) {
        return this.author.equals(myName);
    }
    public ChatMessage() {}

    public ChatMessage(String author, String text) {
        this.author = author;
        this.text = text;
        moment=new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getMoment() {
        return moment;
    }

    public void setMoment(Date moment) {
        this.moment = moment;
    }
    public static ChatMessage fromCursor(Cursor cursor){
        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setId(cursor.getString(0));
        chatMessage.setAuthor(cursor.getString(1));
        chatMessage.setText(cursor.getString(2));
        try {
            chatMessage.setMoment(sqliteFormat.parse(cursor.getString(3)));
        }catch(Exception ex){
            chatMessage.setMoment(new Date());
            Log.e("fromCursor", ex.getClass().getName() + " " + ex.getMessage());
        }
        return chatMessage;
    }
    public static ChatMessage fromJsonObject(JSONObject jsonObject) throws JSONException {

        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setId(jsonObject.getString("id"));
        chatMessage.setAuthor(jsonObject.getString("author"));
        chatMessage.setText(jsonObject.getString("text"));
        try {
            chatMessage.setMoment(
                    dateFormat.parse(
                            jsonObject.getString("moment")) );
                            //  "2025-04-13 19:00:00"));//для тестирования - должно отображаться "Сегодня"
        }catch(ParseException ex){
            throw  new JSONException(ex.getMessage());
        }

        return chatMessage;
    }
    public String getCleverTime(){
        if(moment == null) return "";
        Calendar now=Calendar.getInstance();//время сейчас
        Calendar mesTime=Calendar.getInstance();//время сообщения
        mesTime.setTime(moment);//присваиаю момент

        long milliTime=now.getTimeInMillis()-mesTime.getTimeInMillis();
        long daysDifference= TimeUnit.MILLISECONDS.toDays(milliTime);

        //надо разделить показ слова+время и показ полной даты и времени
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault());

        if(now.get(Calendar.YEAR)==mesTime.get(Calendar.YEAR)&&
        now.get(Calendar.DAY_OF_YEAR)==mesTime.get(Calendar.DAY_OF_YEAR)) {
            return "today " + timeFormat.format(moment);
        }else if (now.get(Calendar.YEAR) == mesTime.get(Calendar.YEAR) &&
                    now.get(Calendar.DAY_OF_YEAR) - mesTime.get(Calendar.DAY_OF_YEAR) == 1) {
                return "yesterday " + timeFormat.format(moment);
        }else if (daysDifference > 1 && daysDifference < 4){
            return daysDifference+ " days ago "+timeFormat.format(moment);
        }
        else return fullDateFormat.format(moment);
    }
}
