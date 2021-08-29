package com.tea.ilearn.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.tea.ilearn.R;
import com.tea.ilearn.utils.EchartsView;

import dev.bandb.graphview.AbstractGraphAdapter;
import dev.bandb.graphview.graph.Graph;
import dev.bandb.graphview.graph.Node;
import dev.bandb.graphview.layouts.layered.SugiyamaArrowEdgeDecoration;
import dev.bandb.graphview.layouts.layered.SugiyamaConfiguration;
import dev.bandb.graphview.layouts.layered.SugiyamaLayoutManager;

public class HomeFragment extends Fragment {
    private EchartsView barChart;
    private RecyclerView graphView;
    private AbstractGraphAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChart = getView().findViewById(R.id.bar_chart);
        barChart.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                refreshBarChart();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        graphView = getView().findViewById(R.id.graph_view);

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
    }

    void refreshBarChart() {
        GsonOption option = new GsonOption();
        option.xAxis(new CategoryAxis().data("周一", "周二", "周三", "周四", "周五", "周六", "周日"));
        option.yAxis(new ValueAxis());
        option.title().text("本周学习点").x(X.center);

        Bar bar = new Bar();
        bar.itemStyle().normal().label().show(true).position("inside");
        bar.data(120, 200, 150, 80, 70, 110, 130);

        option.series(bar);

        barChart.refreshEchartsWithOption(option);
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