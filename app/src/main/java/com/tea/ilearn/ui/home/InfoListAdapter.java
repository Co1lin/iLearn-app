package com.tea.ilearn.ui.home;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.R;
import com.tea.ilearn.activity.entity_detail.EntityDetailActivity;
import com.tea.ilearn.databinding.AbstractCardBinding;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfoListAdapter extends RecyclerView.Adapter {

    private class AbstractHolder extends RecyclerView.ViewHolder {
        private AbstractCardBinding binding;

        AbstractHolder(AbstractCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Info info) {
            binding.entityName.setText(info.name);
            binding.entityCategory.setText(info.category);
            binding.entitySubject.setText(info.subject);

            if (info.loaded) {
                TypedValue typedValue = new TypedValue();
                binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.customColorValue, typedValue, true);
                int color = ContextCompat.getColor(binding.getRoot().getContext(), typedValue.resourceId);
                binding.getRoot().setCardBackgroundColor(color);
            }
            binding.getRoot().setOnClickListener(view -> {
                TypedValue typedValue = new TypedValue();
                binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.customColorValue, typedValue, true);
                int color = ContextCompat.getColor(binding.getRoot().getContext(), typedValue.resourceId);
                binding.getRoot().setCardBackgroundColor(color);

                Intent intent = new Intent (binding.getRoot().getContext(), EntityDetailActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra("name", info.name);
                intent.putExtra("id", info.id);
                intent.putExtra("category", info.category);
                intent.putExtra("subject", info.subject);
                binding.getRoot().getContext().startActivity(intent);
            });

            // binding.star.setChecked(info.star); // TODO resume may change this
        }
    }

    private List<Info> mInfoList;

    public InfoListAdapter(Context context, List<Info> infoList) {
        mInfoList = infoList;
    }

    public void add(Info info) {
        mInfoList.add(info);
        notifyItemInserted(mInfoList.size() - 1);
    }

    public void clear() {
        mInfoList.clear();
        notifyDataSetChanged();
    }

    public <U extends Comparable<? super U>> void applySortAndFilter(Function<? super Info, ? extends U> f, boolean reverse) {
        Stream s = mInfoList.stream();
        if (reverse) s = s.sorted(Comparator.comparing(f).reversed());
        else s = s.sorted(Comparator.comparing(f));
        mInfoList = (List<Info>) s.collect(Collectors.toList());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Info info = mInfoList.get(position);
        return info.kd;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new AbstractHolder(AbstractCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Info info = mInfoList.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                ((AbstractHolder) holder).bind(info);
                break;
        }
    }
}
