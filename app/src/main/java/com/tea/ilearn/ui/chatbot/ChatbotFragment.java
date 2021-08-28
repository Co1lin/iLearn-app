package com.tea.ilearn.ui.chatbot;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.Constant;
import com.tea.ilearn.R;
import com.tea.ilearn.net.EduKG.Answer;
import com.tea.ilearn.net.EduKG.EduKG;

import java.util.ArrayList;

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
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessageRecycler = getView().findViewById(R.id.recycler_chat);
        mMessageAdapter = new MessageListAdapter(getContext(), new ArrayList<>());
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageRecycler.setAdapter(mMessageAdapter);

        editText = getView().findViewById(R.id.edit_chat_message);
        sendText = getView().findViewById(R.id.button_chat_send);

        sendText.setOnClickListener(mView -> {
            String msg = editText.getText().toString();
            if (msg.length() == 0) {
                Toast toast = Toast.makeText(getContext(), "请输入后再发送", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            else {
                mMessageAdapter.add(new ChatMessage(msg, 0));   // user sends a msg
                if (Constant.EduKG.SUBJECTS.contains(msg.substring(0, 2))) {
                    // QA with the specific subject
                    EduKG.getInst().qAWithSubject(msg.substring(0, 2), msg.substring(2), new StaticHandler(mMessageAdapter));
                }
                else {
                    EduKG.getInst().qAWithAllSubjects(msg, new StaticHandler(mMessageAdapter));
                }
                editText.setText("");
                mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount()-1);
            }
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
        });
    }

    static class StaticHandler extends Handler {

        private MessageListAdapter mMessageAdapter;
        private int answerReceived = 0;
        private int errorReceived = 0;

        public StaticHandler(MessageListAdapter _mMessageAdapter) {
            super();
            mMessageAdapter = _mMessageAdapter;
        }

        /**
         * Run on UI Thread!
         */
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i("Chatbot/handleMessage", msg.what + ", " + (msg.obj == null ? "" : msg.obj.toString()));
            answerReceived++;
            if (msg.what == 0 && msg.obj != null) {
                ArrayList<Answer> answerList = (ArrayList<Answer>) msg.obj;
                if (!answerList.isEmpty()) {
                    String answer = answerList.get(0).getAnswer().trim();
                    if (!answer.isEmpty()) {
                        mMessageAdapter.add(new ChatMessage(answer, 1));
                        answerReceived = 0;
                    }
                    else if (answerReceived == Constant.EduKG.SUBJECTS.size())
                        mMessageAdapter.add(new ChatMessage("小艾还在上幼儿园，这个问题还不会 ;(T_T);", 1));
                }
            }
            else {
                errorReceived++;
                if (errorReceived == Constant.EduKG.SUBJECTS.size())
                    mMessageAdapter.add(new ChatMessage("系统错误，请稍后重试或联系客服。", 1));
            }
        }
    }
}