package com.tea.ilearn.ui.chatbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.databinding.ChatMeBinding;
import com.tea.ilearn.databinding.ChatOtherBinding;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        ChatOtherBinding binding;

        ReceivedMessageHolder(ChatOtherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatMessage chatMessage) {
            binding.textChatMessageOther.setText(chatMessage.text);
            // TODO text can not select inside the recycler view
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        ChatMeBinding binding;

        SentMessageHolder(ChatMeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatMessage chatMessage) {
            binding.textChatMessageMe.setText(chatMessage.text);
        }
    }

    private List<ChatMessage> mChatMessageList;

    public MessageListAdapter(Context context, List<ChatMessage> chatMessageList) {
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
            return new SentMessageHolder(ChatMeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == 1) {
            return new ReceivedMessageHolder(ChatOtherBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
                break;
        }
    }
}
