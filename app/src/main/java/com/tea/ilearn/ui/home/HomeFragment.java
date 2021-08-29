package com.tea.ilearn.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.R;
import com.tea.ilearn.activity.SearchableActivity;
import com.tea.ilearn.databinding.FragmentHomeBinding;
import com.tea.ilearn.utils.ACAdapter;
import com.tea.ilearn.utils.EchartsView;

import java.util.ArrayList;
import java.util.Arrays;

import dev.bandb.graphview.AbstractGraphAdapter;
import dev.bandb.graphview.graph.Graph;
import dev.bandb.graphview.graph.Node;
import dev.bandb.graphview.layouts.layered.SugiyamaArrowEdgeDecoration;
import dev.bandb.graphview.layouts.layered.SugiyamaConfiguration;
import dev.bandb.graphview.layouts.layered.SugiyamaLayoutManager;
import per.goweii.actionbarex.common.ActionBarSearch;
import per.goweii.actionbarex.common.AutoComplTextView;

public class HomeFragment extends Fragment {
    private RecyclerView graphView;
    private AbstractGraphAdapter adapter;
    private FragmentHomeBinding binding;
    private ActionBarSearch searchBar;
    private AutoComplTextView acTextView;
    private View loadingBar;
    private View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        searchBar = binding.searchBar;
        loadingBar = searchBar.getForegroundLayer();
        acTextView = searchBar.getEditTextView();

        graphView = binding.graphView;

        SugiyamaConfiguration configuration = new SugiyamaConfiguration.Builder()
                .setNodeSeparation(100)
                .setLevelSeparation(100)
                .build();
        graphView.setLayoutManager(new SugiyamaLayoutManager(getContext(), configuration));

        graphView.addItemDecoration(new SugiyamaArrowEdgeDecoration());

        Graph graph = new Graph();
        Node node1 = new Node("key");
        Node node2 = new Node("Child 1");
        Node node3 = new Node("Child 2");
        Node node4 = new Node("Father");
        Node node5 = new Node("Mother");
        Node node6 = new Node("GrandPa");
        Node node7 = new Node("GrandMa");

        graph.addEdge(node1, node2);
        graph.addEdge(node1, node3);
        graph.addEdge(node4, node1);
        graph.addEdge(node5, node1);
        graph.addEdge(node6, node1);
        graph.addEdge(node7, node1);

        adapter = new AbstractGraphAdapter<NodeHolder>() {
            @Override
            public NodeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.node, parent, false);
                return new NodeHolder(view);
            }

            @Override
            public void onBindViewHolder(NodeHolder holder, int position) {
                holder.mText.setText(getNodeData(position).toString());
            }
        };
        adapter.submitGraph(graph);
        graphView.setAdapter(adapter);
        searchBar.setOnRightIconClickListener(view -> search());
        // bind enter key
        searchBar.getEditTextView().setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                search();
                return true;
            }
            return false;
        });
        // auto completion
        ArrayList<String> COUNTRIES = new ArrayList<>(Arrays.asList("Belgium", "France", "Italy", "Germany", "Spain", "Sp11", "Sp22"));
        ACAdapter<String> historyAdapter = new ACAdapter<>(
                getContext(), R.layout.autocompletion_item,
                R.id.ac_text, R.id.image_button_del, COUNTRIES,
                acTextView);
        acTextView.setAdapter(historyAdapter);
        acTextView.setDropDownAnchor(searchBar.getId());
        acTextView.setThreshold(1); // default 2, minimum 1
        acTextView.setOnFocusChangeListener((view, hasFocus) -> {
            AutoComplTextView acTView = (AutoComplTextView) view;
            if (hasFocus)
                acTView.showDropDown();
        });

        return root;
    }

    private void search() {
        Intent intent = new Intent (root.getContext(), SearchableActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra("query", searchBar.getEditTextView().getText().toString());
        root.getContext().startActivity(intent);
        loadingBar.setVisibility(View.VISIBLE);
        // TODO colin: invisible after expected num of msg are received
        // TODO colin: invisible after return to the home view
    }

    private class NodeHolder extends RecyclerView.ViewHolder {
        TextView mText;

        NodeHolder(View itemView) {
            super(itemView);

            mText = itemView.findViewById(R.id.text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    Log.v("MYDEBUG", "Clicked on " + adapter.getNodeData(pos).toString());
                    // TODO new intent here, nodes[pos] is clicked
                }
            });
        }
    }
}