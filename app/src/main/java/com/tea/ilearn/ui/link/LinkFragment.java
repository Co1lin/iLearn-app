package com.tea.ilearn.ui.link;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.tea.ilearn.R;
import com.tea.ilearn.databinding.FragmentLinkBinding;

public class LinkFragment extends Fragment {
    private FragmentLinkBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLinkBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.text.setOnKeyListener((view, keycode, event) -> {
            if (keycode == KeyEvent.KEYCODE_ENTER) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.text.getWindowToken(), 0);
                doNER(binding.text.getText().toString());
                return true;
            }
            return false;
        });

        binding.clearButton.setOnClickListener(view -> {
            binding.text.setText("");
        });

        return root;
    }

    private void doNER(String text) {
        StaticHandler handler = new StaticHandler(binding.nerResult, text);
        Message.obtain(handler, 0, null).sendToTarget();
    }

    static class StaticHandler extends Handler {
        private ChipGroup chipGroup;
        private String origin;

        public StaticHandler(ChipGroup chipGroup, String origin) {
            super();
            this.chipGroup = chipGroup;
            this.origin = origin;
        }

        /**
         * Run on UI Thread!
         */
        @Override
        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg); // TODO uncomment
            chipGroup.removeAllViews();
            for (int i = 0; i < origin.length(); i++){
                char c = origin.charAt(i);
                TextView v = (TextView)LayoutInflater.from(chipGroup.getContext()).inflate(R.layout.ner_norm_example, null);
                v.setText(Character.toString(c));
                chipGroup.addView(v);
                if (i % 5 == 0) {
                    Chip ch = (Chip)LayoutInflater.from(chipGroup.getContext()).inflate(R.layout.ner_entity_example, null);
                    ch.setText("北京天安门");
                    chipGroup.addView(ch);
                }
            }
//            if (msg.what == 0 && msg.obj != null) {
//                // TODO
//            }
//            else {
//                // TODO
//            }
        }
    }

}