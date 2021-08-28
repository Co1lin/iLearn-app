package com.tea.ilearn.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.R;

import java.util.List;

public class RelationListAdapter extends RecyclerView.Adapter {

    private class RelationHolder extends RecyclerView.ViewHolder {
        TextView typeText, nameText;
        ImageView direction;

        RelationHolder(View itemView) {
            super(itemView);
            typeText = itemView.findViewById(R.id.type);
            nameText = itemView.findViewById(R.id.name);
            direction = itemView.findViewById(R.id.direction);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent (itemView.getContext(), SearchableActivity.class);
                    intent.setAction(Intent.ACTION_SEARCH);
                    intent.putExtra("query", nameText.getText().toString());
                    itemView.getContext().startActivity(intent);
                }
            });
            // TODO related exercise button event
        }

        void bind(Relation relation) {
            nameText.setText(relation.name);
            typeText.setText(relation.type);
            if (relation.dir == 0) {
                direction.setBackgroundResource(R.drawable.ic_baseline_arrow_back_ios_24);
            }
            else {
                direction.setBackgroundResource(R.drawable.ic_baseline_arrow_forward_ios_24);
            }
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
        return 0;
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.relation_card, parent, false);
            return new RelationHolder(view);
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
        }
    }
}
