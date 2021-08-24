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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.abel533.echarts.Label;
import com.github.abel533.echarts.code.Layout;
import com.github.abel533.echarts.code.Position;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.Graph;
import com.github.abel533.echarts.series.force.Category;
import com.github.abel533.echarts.series.force.Link;
import com.github.abel533.echarts.series.force.Node;
import com.github.abel533.echarts.series.other.Force;
import com.github.abel533.echarts.style.ItemStyle;
import com.github.abel533.echarts.style.itemstyle.Emphasis;
import com.tea.ilearn.R;
import com.tea.ilearn.databinding.FragmentHomeBinding;

import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Magic;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.json.GsonOption;
import com.tea.ilearn.utils.EchartsView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HomeFragment extends Fragment {
    private EchartsView barChart, graphChart;

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

        graphChart = getView().findViewById(R.id.graph_chart);
        graphChart.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                refreshGraphChart();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

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

    void refreshGraphChart() {
        GsonOption option = new GsonOption();
        option.title().text("今日推荐实体").x(X.center);

        int n = 10;
        List<Node> nodes = new ArrayList();
        for (int i = 0; i < n; ++i) {
            Node node = new Node();
            node.category(i);
            node.name("name"+i);
            node.symbolSize(40);
            nodes.add(node);
        }

        List<Link> links = new ArrayList();
        for  (int i = 1; i < n; ++i) {
            if (i % 2 == 1) {
                Link link = new Link("name"+0, "name"+i, 1);
                links.add(link);
            }
            else {
                Link link = new Link("name"+i, "name"+0, 1);
                links.add(link);
            }
        }

        List<Category> categories = new ArrayList();
        for (int i = 0; i < n; ++i) {
            Category category = new Category("N"+i);
            categories.add(category);
        }

        Graph graph = new Graph();
        graph.nodes(nodes);
        graph.links(links);
        graph.categories(categories);
        graph.layout(Layout.force);
        graph.itemStyle().normal().label().show(true).position(Position.inside).textStyle().fontSize(18);
        graph.itemStyle().normal().label().formatter("{b}");
        graph.itemStyle().normal().lineStyle().color("source").width(20);
        graph.itemStyle().emphasis().lineStyle().width(10);
        Force force = new Force(); force.repulsion(1000);
        graph.force(force);

        option.series(graph);

        graphChart.refreshEchartsWithOption(option);
    }
}