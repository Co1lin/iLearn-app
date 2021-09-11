package com.tea.ilearn.activity.entity_detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.databinding.FragmentPropertyListBinding;
import com.tea.ilearn.net.edukg.EduKGProperty;

import java.util.ArrayList;

public class PropertyListFragment extends Fragment {
    private FragmentPropertyListBinding binding;
    private RecyclerView mPropertyRecycler;
    private RelationListAdapter mPropertyAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPropertyListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mPropertyRecycler = binding.propertyRecycler;
        mPropertyAdapter = new RelationListAdapter(binding.getRoot().getContext(), new ArrayList<>());
        mPropertyRecycler.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        mPropertyRecycler.setAdapter(mPropertyAdapter);
        mPropertyRecycler.setNestedScrollingEnabled(false);

        binding.entityDescription.setText("实体描述仍在标注中...");

        return root;
    }

    void set(ArrayList<EduKGProperty> properties) {
        for (EduKGProperty p : properties) {
            if (p.getPredicateLabel().equals("描述"))
                binding.entityDescription.setText("实体描述: " + p.getObject());
            else if (p.getPredicateLabel().equals("图片"))
                mPropertyAdapter.add(new Relation(p.getPredicateLabel(), p.getObject(), 3));
            else
                mPropertyAdapter.add(new Relation(p.getPredicateLabel(), p.getObject(), 2));
        }
    }
}