package com.tea.ilearn.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.R;
import com.tea.ilearn.databinding.PropertyCardBinding;
import com.tea.ilearn.databinding.RelationCardBinding;

import java.util.List;

public class RelationListAdapter extends RecyclerView.Adapter {

    private class RelationHolder extends RecyclerView.ViewHolder {
        RelationCardBinding binding;

        RelationHolder(RelationCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Relation relation) {
            binding.type.setText(relation.type);
            binding.name.setText(relation.name);
            if (relation.dir == 0) {
                binding.direction.setBackgroundResource(R.drawable.ic_baseline_arrow_back_ios_24);
            }
            else {
                binding.direction.setBackgroundResource(R.drawable.ic_baseline_arrow_forward_ios_24);
            }

            binding.getRoot().setOnClickListener((view) -> {
                Intent intent = new Intent (binding.getRoot().getContext(), SearchableActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra("query", binding.name.getText().toString());
                binding.getRoot().getContext().startActivity(intent);
            });
        }
    }

    private class PropertyHolder extends RecyclerView.ViewHolder {
        PropertyCardBinding binding;
        PropertyHolder(PropertyCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Relation relation) {
            binding.type.setText(relation.type);
            binding.name.setText(relation.name);
        }
    }

    private Context mContext;
    private List<Relation> mRelationList;

    public RelationListAdapter(Context context, List<Relation> relationList) {
        mContext = context;
        mRelationList = relationList;
    }

    public void add(Relation relation) {
        mRelationList.add(relation);
        notifyItemInserted(mRelationList.size() - 1);
    }

    @Override
    public int getItemCount() {
        return mRelationList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Relation relation = mRelationList.get(position);
        if (relation.dir != 2) return 0;
        return 1;
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            RelationCardBinding binding = RelationCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new RelationHolder(binding);
        }
        else if (viewType == 1) {
            PropertyCardBinding binding = PropertyCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new PropertyHolder(binding);
        }
        return null;
    }

    // Passes the relation object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Relation relation = mRelationList.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                ((RelationHolder) holder).bind(relation);
                break;
            case 1:
                ((PropertyHolder) holder).bind(relation);
                break;
        }
    }
}
