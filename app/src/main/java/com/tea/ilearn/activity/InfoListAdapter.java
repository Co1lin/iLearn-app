package com.tea.ilearn.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.databinding.EntityCardBinding;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.EntityDetail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfoListAdapter extends RecyclerView.Adapter {

    private class EntityHolder extends RecyclerView.ViewHolder {
        private EntityCardBinding binding;
        boolean mloaded;
        private RecyclerView mRelationRecycler, mPropertyRecycler;
        private RelationListAdapter mRelationAdapter, mPropertyAdapter;

        EntityHolder(EntityCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Info info) {
            binding.entityName.setText(info.name);
            binding.entityCategory.setText(info.category);
            binding.entitySubject.setText(info.subject);
            mloaded = info.loaded;
            if (mloaded) {
//                 StaticHandler handler = new StaticHandler(mRelationAdapter, mPropertyAdapter);
//                 Message.obtain(handler, 0, respObj).sendToTarget();
//                 TODO load in local memory
            }
            // TODO related exercise button event

            binding.getRoot().setOnClickListener(view -> {
                if (binding.collapseBox.getVisibility() == View.GONE) {
                    binding.collapseBox.setVisibility(View.VISIBLE);
                    if (!mloaded) {
                        StaticHandler handler = new StaticHandler(mRelationAdapter, mPropertyAdapter);
                        EduKG.getInst().getEntityDetails(info.subject, binding.entityName.getText().toString(), handler);
                        mloaded = true;
                        // TODO save to database
                    }
                }
                else if (binding.collapseBox.getVisibility() == View.VISIBLE) {
                    binding.collapseBox.setVisibility(View.GONE);
                }
            });

            mRelationRecycler = binding.relationRecycler;
            mRelationAdapter = new RelationListAdapter(binding.getRoot().getContext(), new ArrayList<Relation>());
            mRelationRecycler.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            mRelationRecycler.setAdapter(mRelationAdapter);
            mPropertyRecycler = binding.propertyRecycler;
            mPropertyAdapter = new RelationListAdapter(binding.getRoot().getContext(), new ArrayList<Relation>());
            mPropertyRecycler.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            mPropertyRecycler.setAdapter(mPropertyAdapter);

            binding.star.setChecked(info.star);
            binding.star.setOnCheckedChangeListener((btn, star) -> {
                // TODO save current "star" status in database
            });

            binding.share.setOnClickListener(view -> {
                // TODO share related sdk
            });

            binding.relatedExercise.setOnClickListener(view -> {
                // TODO related problem
            });
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

    public <U extends Comparable<? super U>> void applySortAndFilter(Function<? super Info, ? extends U> f, boolean reverse) {
        Stream s = mInfoList.stream();
        if (reverse) s = s.sorted(Comparator.comparing(f).reversed());
        else s = s.sorted(Comparator.comparing(f));
        // s.filter(info -> info.category.equals("组成细胞的分子"))
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

    // Inflates the apriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new EntityHolder(EntityCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return null;
    }

    // Passes the info object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Info info = mInfoList.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                ((EntityHolder) holder).bind(info);
                break;
        }
    }


    static class StaticHandler extends Handler {
        private RelationListAdapter mRelationAdapter;
        private RelationListAdapter mPropertyAdapter;

        StaticHandler(RelationListAdapter mRelationAdapter, RelationListAdapter mPropertyAdapter) {
            this.mPropertyAdapter = mPropertyAdapter;
            this.mRelationAdapter = mRelationAdapter;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            EntityDetail detail = (EntityDetail) msg.obj;
            if (detail != null) {
                if (detail.getRelations() != null) {
                    for (EntityDetail.Relation r : detail.getRelations()) {
                        mRelationAdapter.add(new Relation(r.getPredicateLabel(), r.getObjectLabel(), r.getDirection()));
                    }
                }
                if (detail.getProperties() != null) {
                    for (EntityDetail.Property p : detail.getProperties()) {
                        mPropertyAdapter.add(new Relation(p.getPredicateLabel(), p.getObject(), 2));
                    }
                }
            } else {
                // TODO empty content in entity detail
            }
        }
    }
}
