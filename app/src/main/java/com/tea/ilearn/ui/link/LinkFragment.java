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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.tea.ilearn.Constant;
import com.tea.ilearn.R;
import com.tea.ilearn.databinding.FragmentLinkBinding;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.LinkResults;

import java.util.List;

public class LinkFragment extends Fragment {
    private FragmentLinkBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLinkBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.text.setOnKeyListener((view, keycode, event) -> {
            if (keycode == KeyEvent.KEYCODE_ENTER) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.text.getWindowToken(), 0);
                doNER();
                return true;
            }
            return false;
        });

        binding.clearButton.setOnClickListener(view -> {
            binding.text.setText("");
        });

        binding.courseSpinner.attachDataSource(Constant.EduKG.SUBJECTS);
        binding.courseSpinner.setOnSpinnerItemSelectedListener((parent, view, position, id) -> {
            doNER();
        });

        binding.refreshButton.setOnClickListener(view -> {
            doNER();
        });

        return root;
    }

    private void doNER() {
        String text = binding.text.getText().toString();
        StaticHandler handler = new StaticHandler(binding.nerResult, text);
        EduKG.getInst().getNamedEntities((String)binding.courseSpinner.getSelectedItem(), text, handler);
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
            super.handleMessage(msg);
            if (msg.what == 0 && msg.obj != null) {
                List<LinkResults.LinkedEntity> entities = ((LinkResults)(msg.obj)).getResults();
                int cursor = 0;
                chipGroup.removeAllViews();
                for (LinkResults.LinkedEntity e : entities) {
                    for (; cursor < e.getStartIndex(); cursor++) {
                        char c = origin.charAt(cursor);
                        TextView v = (TextView)LayoutInflater.from(chipGroup.getContext()).inflate(R.layout.ner_norm_example, null);
                        v.setText(Character.toString(c));
                        chipGroup.addView(v);
                    }
                    Chip ch = (Chip)LayoutInflater.from(chipGroup.getContext()).inflate(R.layout.ner_entity_example, null);
                    ch.setText(origin.substring(e.getStartIndex(), e.getEndIndex()+1));
                    // TODO ch onclick listener
                    chipGroup.addView(ch);
                    cursor = e.getEndIndex() + 1;
                }
                for (; cursor < origin.length(); cursor++) {
                    char c = origin.charAt(cursor);
                    TextView v = (TextView)LayoutInflater.from(chipGroup.getContext()).inflate(R.layout.ner_norm_example, null);
                    v.setText(Character.toString(c));
                    chipGroup.addView(v);
                }
            }
            else {
                Toast.makeText(chipGroup.getContext(), "API又炸啦哈哈哈哈哈真好用", Toast.LENGTH_LONG);
            }
        }
    }

}