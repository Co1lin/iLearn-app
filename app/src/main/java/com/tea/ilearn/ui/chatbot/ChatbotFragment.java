package com.tea.ilearn.ui.chatbot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import com.tea.ilearn.R;

public class ChatbotFragment extends Fragment {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private EditText editText;
    private Button sendText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessageRecycler = getView().findViewById(R.id.recycler_chat);
        mMessageAdapter = new MessageListAdapter(getContext(), new ArrayList<Message>());
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageRecycler.setAdapter(mMessageAdapter);

        editText = getView().findViewById(R.id.edit_chat_message);
        sendText = getView().findViewById(R.id.button_chat_send);

        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessageAdapter.add(new Message(editText.getText().toString(), 0));
                mMessageAdapter.add(new Message("小艾在读幼儿园，啥也不会呢", 1));
                editText.setText("");
                // TODO : hide keyboard here
            }
        });
    }
}