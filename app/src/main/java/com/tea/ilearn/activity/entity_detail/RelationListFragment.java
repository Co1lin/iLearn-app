package com.tea.ilearn.activity.entity_detail;

import android.content.Intent;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.R;
import com.tea.ilearn.databinding.FragmentRelationListBinding;
import com.tea.ilearn.databinding.NodeBinding;
import com.tea.ilearn.net.edukg.EduKGRelation;

import java.util.ArrayList;

import dev.bandb.graphview.AbstractGraphAdapter;
import dev.bandb.graphview.decoration.edge.ArrowEdgeDecoration;
import dev.bandb.graphview.graph.Graph;
import dev.bandb.graphview.graph.Node;
import dev.bandb.graphview.layouts.layered.SugiyamaConfiguration;
import dev.bandb.graphview.layouts.layered.SugiyamaLayoutManager;

public class RelationListFragment extends Fragment {
    static private class EntityInfo {
        public String name, category, subject, uri;

        public EntityInfo(String name, String category, String subject, String uri) {
            this.name = name;
            this.category = category;
            this.subject = subject;
            this.uri = uri;
        }
    };

    private FragmentRelationListBinding binding;
    private RecyclerView mRelationRecycler;
    private RelationListAdapter mRelationAdapter;
    private AbstractGraphAdapter graphAdapter;
    private String name, category, subject, uri;
    private ArrayList<String> categories;

    public RelationListFragment(String name, String category, String subject, String uri, ArrayList<String> categories) {
        this.name = name;
        this.category = category;
        this.subject = subject;
        this.uri = uri;
        this.categories = categories;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRelationListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mRelationRecycler = binding.relationRecycler;
        mRelationAdapter = new RelationListAdapter(binding.getRoot().getContext(), new ArrayList<>());
        mRelationRecycler.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        mRelationRecycler.setAdapter(mRelationAdapter);
        mRelationRecycler.setNestedScrollingEnabled(false);

        initGraph();

        return root;
    }

    void initGraph() {
        SugiyamaConfiguration configuration = new SugiyamaConfiguration.Builder()
                .setNodeSeparation(100)
                .setLevelSeparation(100)
                .build();
        binding.graphView.setLayoutManager(new SugiyamaLayoutManager(binding.getRoot().getContext(), configuration));
        Paint edgeStyle = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedValue typedValue = new TypedValue();
        binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
        int color = ContextCompat.getColor(binding.getRoot().getContext(), typedValue.resourceId);
        edgeStyle.setColor(color);
        edgeStyle.setStrokeWidth(5f);
        edgeStyle.setStyle(Paint.Style.STROKE);
        edgeStyle.setStrokeJoin(Paint.Join.ROUND);
        edgeStyle.setPathEffect(new CornerPathEffect(10f));
        binding.graphView.addItemDecoration(new ArrowEdgeDecoration(edgeStyle));

        graphAdapter = new AbstractGraphAdapter<NodeHolder>() {
            @Override
            public NodeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                NodeBinding binding = NodeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new NodeHolder(binding);
            }

            @Override
            public void onBindViewHolder(NodeHolder holder, int position) {
                holder.set((EntityInfo) getNodeData(position));
            }
        };
        binding.graphView.setAdapter(graphAdapter);
    }


    private class NodeHolder extends RecyclerView.ViewHolder {
        NodeBinding binding;
        EntityInfo info;

        NodeHolder(NodeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.text.setOnClickListener($ -> {
                Intent intent = new Intent (binding.getRoot().getContext(), EntityDetailActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra("name", info.name);
                intent.putExtra("subject", info.subject);
                intent.putExtra("category", info.category);
                intent.putExtra("id", info.uri);
                binding.getRoot().getContext().startActivity(intent);
            });
        }

        public void set(EntityInfo info) {
            this.info = info;
            binding.text.setText(info.name);
        }
    }

    void set(ArrayList<EduKGRelation> relations) {
        Graph graph = new Graph();
        Node center = new Node(new EntityInfo(name, category, subject, uri));
        int num_in = 0, num_out = 0;
        for (EduKGRelation r : relations) {
            Node other = new Node(new EntityInfo(r.getObjectLabel(), category, subject, r.getObject()));
            if (!r.getObjectLabel().equals(name)) {
                if (r.getDirection() == 1 && num_out < 5) {
                    num_out += 1;
                    graph.addEdge(center, other);
                }
                else if (r.getDirection() == 0 && num_in < 5) {
                    num_in += 1;
                    graph.addEdge(other, center);
                }
            }
            mRelationAdapter.add(new Relation(
                    r.getPredicateLabel(), r.getObjectLabel(), r.getDirection(),
                    subject, category, categories, r.getObject()));
        }
        graphAdapter.submitGraph(graph);
        graphAdapter.notifyDataSetChanged();
    }
}