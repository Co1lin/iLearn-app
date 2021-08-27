package com.tea.ilearn.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.R;

import java.util.List;

public class InfoListAdapter extends RecyclerView.Adapter {

    private class EntityHolder extends RecyclerView.ViewHolder {
        TextView nameText, propertyText;
        LinearLayout collapseBox;

        EntityHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.entity_name);
            propertyText = itemView.findViewById(R.id.entity_property);
            collapseBox = itemView.findViewById(R.id.collapse_box);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (collapseBox.getVisibility() == View.GONE) {
                        collapseBox.setVisibility(View.VISIBLE);
                    }
                    else {
                        collapseBox.setVisibility(View.GONE);
                    }
                }
            });
            // TODO related exercise button event
        }

        void bind(Info info) {
            nameText.setText(info.name);
            propertyText.setText(info.property);
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
        Info message = mInfoList.get(position);
        return message.kd;
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entity_card, parent, false);
            return new EntityHolder(view);
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
}
