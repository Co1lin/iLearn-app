package com.tea.ilearn.ui.chatbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.R;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_chat_message_other);
            nameText = itemView.findViewById(R.id.text_chat_user_other);
            profileImage = itemView.findViewById(R.id.image_chat_profile_other);
        }

        void bind(ChatMessage chatMessage) {
            messageText.setText(chatMessage.text);
            nameText.setText("小艾");
            // TODO text can not select inside the recycler view
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_chat_message_me);
        }

        void bind(ChatMessage chatMessage) {
            messageText.setText(chatMessage.text);
        }
    }

    private Context mContext;
    private List<ChatMessage> mChatMessageList;

    public MessageListAdapter(Context context, List<ChatMessage> chatMessageList) {
        mContext = context;
        mChatMessageList = chatMessageList;
    }

    public void add(ChatMessage m) {
        mChatMessageList.add(m);
        notifyItemInserted(mChatMessageList.size() - 1);
    }

    @Override
    public int getItemCount() {
        return mChatMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = mChatMessageList.get(position);
        return chatMessage.who;
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_me, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_other, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = mChatMessageList.get(position);

        switch (holder.getItemViewType()) {
            case 0:
                ((SentMessageHolder) holder).bind(chatMessage);
                break;
            case 1:
                ((ReceivedMessageHolder) holder).bind(chatMessage);
        }
    }
}
