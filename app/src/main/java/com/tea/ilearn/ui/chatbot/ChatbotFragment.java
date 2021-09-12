package com.tea.ilearn.ui.chatbot;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.Constant;
import com.tea.ilearn.R;
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

        TypedValue typedValue = new TypedValue();
        binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.colorSurface, typedValue, true);
        int color = ContextCompat.getColor(binding.getRoot().getContext(), typedValue.resourceId);
        editText.setBackgroundTintList(ColorStateList.valueOf(color));

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

    @Override
    public void onResume() {
        super.onResume();
        if (mMessageAdapter.getItemCount() == 0)
            mMessageAdapter.add(new ChatMessage("由于目前知识库的请求频率限制，暂不支持直接提问。请先输入学科的中文名称再输入问题，否则暂时无法得到答案。示例：\n语文，李白是谁？", 1));
    }

    private void sendMessage(View view) {
        String msg = editText.getText().toString().trim();
        if (msg.length() <= 3 ||
            !(msg.charAt(2) == '，' || msg.charAt(2) == ',') ||
            !Constant.EduKG.SUBJECTS_ZH.contains(msg.substring(0,2)) ||
            msg.substring(3).trim().isEmpty())
        {
            Toast toast = Toast.makeText(getContext(), "请按照提示正确输入问题", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else {
            mMessageAdapter.add(new ChatMessage(msg, 0));
            String subject = Constant.EduKG.ZH_EN.get(msg.substring(0,2));
            String question = msg.substring(3).trim();
            EduKG.getInst().qAWithSubject(subject, question, new StaticHandler(mMessageAdapter, 1, mMessageRecycler));
            editText.setText("");
            mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount() - 1);
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        //EduKG.getInst().qAWithAllSubjects(msg, new StaticHandler(mMessageAdapter, Constant.EduKG.SUBJECTS_EN.size(), mMessageRecycler));
    }

    static class StaticHandler extends Handler {
        private RecyclerView mMessageRecycler;
        private MessageListAdapter mMessageAdapter;
        private int expectedNum;
        private int answerReceived = 0;
        private int errorReceived = 0;
        private ArrayList<String> receivedAnswer;

        public StaticHandler(MessageListAdapter _messageAdapter, int _num, RecyclerView _messageRecycler) {
            super();
            mMessageAdapter = _messageAdapter;
            expectedNum = _num;
            mMessageRecycler = _messageRecycler;
            receivedAnswer = new ArrayList<>();
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
                        if (!receivedAnswer.contains(answer)) {
                            mMessageAdapter.add(new ChatMessage(answer, 1));
                            receivedAnswer.add(answer);
                        }
                        answerReceived = 0;
                    }
                    else if (answerReceived == expectedNum - errorReceived)
                        mMessageAdapter.add(new ChatMessage("小艾还在上幼儿园，这个问题还不会 ;(T_T);", 1));
                }
            }
            else {
                errorReceived++;
                if (errorReceived == expectedNum) {
                    mMessageAdapter.add(new ChatMessage(Constant.EduKG.ERROR_MSG, 1));
                }
            }
            mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount() - 1);
        }
    }
}