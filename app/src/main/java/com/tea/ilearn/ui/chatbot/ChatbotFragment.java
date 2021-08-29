package com.tea.ilearn.ui.chatbot;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.Constant;
import com.tea.ilearn.databinding.FragmentChatbotBinding;
import com.tea.ilearn.net.edukg.Answer;
import com.tea.ilearn.net.edukg.EduKG;

import java.util.ArrayList;

import per.goweii.actionbarex.common.ActionBarSearch;
import per.goweii.actionbarex.common.AutoComplTextView;

public class ChatbotFragment extends Fragment {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private FragmentChatbotBinding binding;
    private View root;
    private ActionBarSearch bottomSendBar;
    private AutoComplTextView editText;
    private ImageView sendButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatbotBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        bottomSendBar = binding.sendBar;
        editText = bottomSendBar.getEditTextView();
        sendButton = bottomSendBar.getRightIconView();

        mMessageRecycler = binding.recyclerChat;
        mMessageAdapter = new MessageListAdapter(getContext(), new ArrayList<>());
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageRecycler.setAdapter(mMessageAdapter);

        bottomSendBar.setOnRightIconClickListener(view -> sendMessage(view));
        bottomSendBar.getEditTextView().setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                sendMessage(view);
                return true;
            }
            return false;
        });

        return root;
    }

    private void sendMessage(View view) {
        String msg = editText.getText().toString().trim();
        if (msg.length() == 0) {
            Toast toast = Toast.makeText(getContext(), "请输入后再发送", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else {
            mMessageAdapter.add(new ChatMessage(msg, 0));   // user sends a msg
            if (msg.length() >= 4 &&
                    (msg.substring(0, 1).equals("[") && msg.substring(3, 4).equals("]") ||
                            msg.substring(0, 1).equals("【") && msg.substring(3, 4).equals("】")) &&
                    Constant.EduKG.SUBJECTS.contains(msg.substring(1, 3))) {
                // QA with the specific subject when matches [**]
                EduKG.getInst().qAWithSubject(msg.substring(1, 3), msg.substring(4),
                        new StaticHandler(mMessageAdapter, 1, mMessageRecycler));
            }
            else {
                EduKG.getInst().qAWithAllSubjects(msg,
                        new StaticHandler(mMessageAdapter, Constant.EduKG.SUBJECTS.size(), mMessageRecycler));
            }
            editText.setText("");
            mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount() - 1);
        }
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    static class StaticHandler extends Handler {
        private RecyclerView mMessageRecycler;
        private MessageListAdapter mMessageAdapter;
        private int expectedNum;
        private int answerReceived = 0;
        private int errorReceived = 0;

        public StaticHandler(MessageListAdapter _messageAdapter, int _num, RecyclerView _messageRecycler) {
            super();
            mMessageAdapter = _messageAdapter;
            expectedNum = _num;
            mMessageRecycler = _messageRecycler;
        }

        /**
         * Run on UI Thread!
         */
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i("Chatbot/handleMessage", msg.what + ", " + (msg.obj == null ? "" : msg.obj.toString()));
            if (msg.what == 0 && msg.obj != null) {
                answerReceived++;
                ArrayList<Answer> answerList = (ArrayList<Answer>) msg.obj;
                if (!answerList.isEmpty()) { // assert answerList.isEmpty() == false
                    String answer = answerList.get(0).getAnswer().trim();
                    if (!answer.isEmpty()) {
                        mMessageAdapter.add(new ChatMessage(answer, 1));
                        answerReceived = 0;
                    }
                    else if (answerReceived == expectedNum - errorReceived)
                        mMessageAdapter.add(new ChatMessage("小艾还在上幼儿园，这个问题还不会 ;(T_T);", 1));
                    mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount() - 1);
                }
            }
            else {
                errorReceived++;
                if (errorReceived == expectedNum) {
                    mMessageAdapter.add(new ChatMessage(Constant.EduKG.ERROR_MSG, 1));
                    mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount() - 1);
                }
            }
        }
    }
}