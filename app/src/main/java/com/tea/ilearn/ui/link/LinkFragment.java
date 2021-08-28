package com.tea.ilearn.ui.link;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tea.ilearn.databinding.FragmentLinkBinding;

public class LinkFragment extends Fragment {

    private LinkViewModel LinkViewModel;
    private FragmentLinkBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LinkViewModel =
                new ViewModelProvider(this).get(LinkViewModel.class);

        binding = FragmentLinkBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textLink;
        LinkViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}