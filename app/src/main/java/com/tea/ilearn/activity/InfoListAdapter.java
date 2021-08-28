package com.tea.ilearn.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.R;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.EntityDetail;

import java.util.ArrayList;
import java.util.List;

public class InfoListAdapter extends RecyclerView.Adapter {

    private class EntityHolder extends RecyclerView.ViewHolder {
        TextView nameText, propertyText;
        ImageView star, share; boolean mstar;
        Context ctx;
        LinearLayout collapseBox;
        private RecyclerView mRelationRecycler;
        private RelationListAdapter mRelationAdapter;

        EntityHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.entity_name);
            propertyText = itemView.findViewById(R.id.entity_property);
            star = itemView.findViewById(R.id.star);
            share = itemView.findViewById(R.id.share);
            collapseBox = itemView.findViewById(R.id.collapse_box);
            ctx = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (collapseBox.getVisibility() == View.GONE) {
                        collapseBox.setVisibility(View.VISIBLE);
                        StaticHandler handler = new StaticHandler(mRelationAdapter);
                        EduKG.getInst().getEntityDetails("chinese", nameText.getText().toString(), handler); // TODO
                    }
                    else {
                        collapseBox.setVisibility(View.GONE);
                    }
                }
            });
            // TODO related exercise button event

            mRelationRecycler = itemView.findViewById(R.id.relation_recycler);
            mRelationAdapter = new RelationListAdapter(ctx, new ArrayList<Relation>());
            mRelationRecycler.setLayoutManager(new LinearLayoutManager(ctx));
            mRelationRecycler.setAdapter(mRelationAdapter);
        }

        void bind(Info info) {
            nameText.setText(info.name);
            mstar = info.star;

            Drawable drawable;
            if (!mstar) {
                drawable = DrawableCompat.wrap(ctx.getDrawable(R.drawable.ic_favorite_border_24));
            } else {
                drawable = DrawableCompat.wrap(ctx.getDrawable(R.drawable.ic_favorite_filled_24));
            }
            DrawableCompat.setTint(drawable, ContextCompat.getColor(ctx, R.color.teal_200));
            star.setBackground(drawable);
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mstar == false) {
                        Drawable drawable = DrawableCompat.wrap(ctx.getDrawable(R.drawable.ic_favorite_filled_24));
                        DrawableCompat.setTint(drawable, ContextCompat.getColor(ctx, R.color.teal_200));
                        star.setBackground(drawable);
                    }
                    else {
                        Drawable drawable = DrawableCompat.wrap(ctx.getDrawable(R.drawable.ic_favorite_border_24));
                        DrawableCompat.setTint(drawable, ContextCompat.getColor(ctx, R.color.teal_200));
                        star.setBackground(drawable);
                    }
                    mstar = !mstar;
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
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
