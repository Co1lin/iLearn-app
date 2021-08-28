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
import com.tea.ilearn.net.EduKG.EduKG;
import com.tea.ilearn.net.EduKG.EntityDetail;

import java.util.ArrayList;
import java.util.List;

public class InfoListAdapter extends RecyclerView.Adapter {

    private class EntityHolder extends RecyclerView.ViewHolder {
        private EntityCardBinding binding;
        boolean mloaded;
        private RecyclerView mRelationRecycler;
        private RelationListAdapter mRelationAdapter;

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
//                 StaticHandler handler = new StaticHandler(mRelationAdapter);
//                 Message.obtain(handler, 0, respObj).sendToTarget();
//                 TODO load in local memory
            }
            // TODO related exercise button event

            binding.getRoot().setOnClickListener(view -> {
                if (binding.collapseBox.getVisibility() == View.GONE) {
                    binding.collapseBox.setVisibility(View.VISIBLE);
                    if (!mloaded) {
                        StaticHandler handler = new StaticHandler(mRelationAdapter);
                        EduKG.getInst().getEntityDetails(info.subject, binding.entityName.getText().toString(), handler); // TODO
                        mloaded = true;
                        // TODO save to database
                    }
                }
            });
            binding.getRoot().setOnLongClickListener(view -> {
                if (binding.collapseBox.getVisibility() == View.VISIBLE) {
                    binding.collapseBox.setVisibility(View.GONE);
                }
                return true;
            });

            mRelationRecycler = binding.relationRecycler;
            mRelationAdapter = new RelationListAdapter(binding.getRoot().getContext(), new ArrayList<Relation>());
            mRelationRecycler.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            mRelationRecycler.setAdapter(mRelationAdapter);

            binding.star.setChecked(info.star);
            binding.star.setOnCheckedChangeListener((btn, star) -> {
                // TODO save current "star" status in database
            });

            binding.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO share related sdk
                }
            });
        }
    }

    private Context mContext;
    private List<Info> mInfoList;

    public InfoListAdapter(Context context, List<Info> infoList) {
        mContext = context;
        mInfoList = infoList;
    }

    public void add(Info info) {
        mInfoList.add(info);
        notifyItemInserted(mInfoList.size() - 1);
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

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == 0) {
            EntityCardBinding binding = EntityCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new EntityHolder(binding);
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

        StaticHandler(RelationListAdapter mRelationAdapter) {
            this.mRelationAdapter = mRelationAdapter;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            EntityDetail detail = (EntityDetail) msg.obj;
            if (detail != null) {
                if (detail.getRelations() != null) {
                    for (EntityDetail.Relation r : detail.getRelations()) {
                        mRelationAdapter.add(new Relation(r.getDirection(), r.getPredicateLabel(), r.getObjectLabel()));
                    }
                }
                else {
                    // TODO
                }
            } else {
                // TODO
            }
        }
    }
}
