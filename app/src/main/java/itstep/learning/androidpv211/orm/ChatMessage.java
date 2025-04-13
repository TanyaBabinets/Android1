package itstep.learning.androidpv211.orm;

import android.icu.util.Calendar;

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

    private String id;
    private String author;
    private String text;
    private Date moment;

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

    public static ChatMessage fromJsonObject(JSONObject jsonObject) throws JSONException {

        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setId(jsonObject.getString("id"));
        chatMessage.setAuthor(jsonObject.getString("author"));
        chatMessage.setText(jsonObject.getString("text"));
        try {
            chatMessage.setMoment(
                    dateFormat.parse(
                         //   jsonObject.getString("moment")) );
               "2025-04-13 19:00:00"));//для тестирования - должно отображаться "Сегодня"
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
        now.get(Calendar.DAY_OF_YEAR)==mesTime.get(Calendar.DAY_OF_YEAR)){
            return "today "+timeFormat.format(moment);
        }else if (daysDifference==1){
            return "yesterday "+timeFormat.format(moment);
        }else if (daysDifference<4){
            return daysDifference+ "ago "+timeFormat.format(moment);
        }
        else return fullDateFormat.format(moment);
    }
}
