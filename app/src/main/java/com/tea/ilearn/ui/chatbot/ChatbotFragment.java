package com.tea.ilearn.ui.chatbot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

import com.tea.ilearn.R;

public class ChatbotFragment extends Fragment {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessageRecycler = getView().findViewById(R.id.recycler_chat);
        mMessageAdapter = new MessageListAdapter(getContext(), Arrays.asList());
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageRecycler.setAdapter(mMessageAdapter);

        initListener();
    }

    private void initListener() {
        final EditText editText = getView().findViewById(R.id.edit_chat_message);
    }
}