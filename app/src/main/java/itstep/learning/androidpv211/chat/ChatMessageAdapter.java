package itstep.learning.androidpv211.chat;
import android.view.Gravity;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import itstep.learning.androidpv211.orm.ChatMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import itstep.learning.androidpv211.R;
import itstep.learning.androidpv211.nbu.NbuRateViewHolder;
import itstep.learning.androidpv211.orm.ChatMessage;
public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageViewHolder> {
    private final List<ChatMessage> messages;
    private String myName;

    public ChatMessageAdapter(List<ChatMessage> messages, String myName) {
        this.messages = messages;
        this.myName=myName;    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from( parent.getContext() )
                .inflate( R.layout.chat_msg_layout, parent, false );

        return new ChatMessageViewHolder( itemView );
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.setChatMessage(message);
       //holder.setChatMessage( messages.get( position ) );
        LinearLayout layout = (LinearLayout) holder.itemView;
if(message.getAuthor().equals(myName)){
    ((LinearLayout) holder.itemView).setGravity(Gravity.END);//сдвинуть смс вправо
}else{
    ((LinearLayout) holder.itemView).setGravity(Gravity.START);
}

    }
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        if (message.getAuthor().equals(myName)) {
            return 1; // смс от меня
        } else {
            return 0; // смс от других
        }
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }
}