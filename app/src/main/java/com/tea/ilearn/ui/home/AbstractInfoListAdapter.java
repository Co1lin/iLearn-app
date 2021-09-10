package com.tea.ilearn.ui.home;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.Constant;
import com.tea.ilearn.R;
import com.tea.ilearn.activity.entity_detail.EntityDetailActivity;
import com.tea.ilearn.databinding.AbstractCardBinding;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbstractInfoListAdapter extends RecyclerView.Adapter {

    private class AbstractHolder extends RecyclerView.ViewHolder {
        private AbstractCardBinding binding;

        AbstractHolder(AbstractCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AbstractInfo abstractInfo) {
            binding.entityName.setText(abstractInfo.name);
            binding.entityCategory.setText(abstractInfo.getCategory());
            binding.entitySubject.setText(Constant.EduKG.EN_ZH.get(abstractInfo.subject));

            if (abstractInfo.loaded) {
                TypedValue typedValue = new TypedValue();
                binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.customColorValue, typedValue, true);
                int color = ContextCompat.getColor(binding.getRoot().getContext(), typedValue.resourceId);
                binding.getRoot().setCardBackgroundColor(color);
            }
            else {
                TypedValue typedValue = new TypedValue();
                binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.colorSurface, typedValue, true);
                int color = ContextCompat.getColor(binding.getRoot().getContext(), typedValue.resourceId);
                binding.getRoot().setCardBackgroundColor(color);
            }
            binding.getRoot().setOnClickListener(view -> {
                abstractInfo.loaded = true;
                TypedValue typedValue = new TypedValue();
                binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.customColorValue, typedValue, true);
                int color = ContextCompat.getColor(binding.getRoot().getContext(), typedValue.resourceId);
                binding.getRoot().setCardBackgroundColor(color);

                Intent intent = new Intent (binding.getRoot().getContext(), EntityDetailActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra("name", abstractInfo.name);
                intent.putExtra("id", abstractInfo.id);
                intent.putExtra("category", abstractInfo.getCategory());
                intent.putStringArrayListExtra("categories", abstractInfo.categories);
                intent.putExtra("subject", abstractInfo.subject);
                binding.getRoot().getContext().startActivity(intent);

                // loaded state already changed when step into entity detail page
            });
        }
    }

    private List<AbstractInfo> mAbstractInfoList;

    public AbstractInfoListAdapter(Context context, List<AbstractInfo> abstractInfoList) {
        mAbstractInfoList = abstractInfoList;
    }

    public void add(AbstractInfo abstractInfo) {
        mAbstractInfoList.add(abstractInfo);
        notifyItemInserted(mAbstractInfoList.size() - 1);
    }

    public void clear() {
        mAbstractInfoList.clear();
        notifyDataSetChanged();
    }

    public void modify(int position, boolean star, boolean loaded) {
        AbstractInfo abstractInfo = mAbstractInfoList.get(position);
        abstractInfo.star = star;
        abstractInfo.loaded = loaded;
        mAbstractInfoList.set(position, abstractInfo);
        notifyItemChanged(position);
    }

    public <U extends Comparable<? super U>> void applySortAndFilter(Function<? super AbstractInfo, ? extends U> f, boolean reverse) {
        Stream s = mAbstractInfoList.stream();
        if (reverse) s = s.sorted(Comparator.comparing(f).reversed());
        else s = s.sorted(Comparator.comparing(f));
        mAbstractInfoList = (List<AbstractInfo>) s.collect(Collectors.toList());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mAbstractInfoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        AbstractInfo abstractInfo = mAbstractInfoList.get(position);
        return abstractInfo.kd;
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
        AbstractInfo abstractInfo = mAbstractInfoList.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                ((AbstractHolder) holder).bind(abstractInfo);
                break;
        }
    }
}
