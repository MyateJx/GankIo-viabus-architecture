package com.gank.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gank.R;
import com.gank.data.network.response.Result;
import com.gank.util.TimeUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/4/3 0003.
 */

public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.ViewHolder> {
    private boolean fromCollection;
    private boolean fromSearch;
    private List<Result> list;
    private OnItemClickListener OnItemClickListener;

    public CommonAdapter() {
    }

    public void setData(List<Result> list) {
        this.list = list;
    }

    public void setData(List<Result> list, boolean fromCollection) {
        this.fromCollection = fromCollection;
        this.list = list;
    }

    public void setSearchData(List<Result> list, boolean fromSearch) {
        this.fromSearch = fromSearch;
        this.list = list;
    }

    public void notifyData(int startPosition, int count) {
        notifyItemRangeChanged(startPosition, count);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Result result = list.get(position);
        holder.image.setVisibility(View.GONE);
        if (fromSearch) {
            holder.image.setVisibility(View.GONE);
        } else if (fromCollection) {
            if (result.getImg() != null && result.getImg().size() > 0) {
                holder.image.setVisibility(View.VISIBLE);
                Glide.with(holder.itemView.getContext()).load(result.getImg().get(0).getImageUrl())
                        .asBitmap().centerCrop().into(holder.image);
            }
        } else {
            if (result.getImages() != null && result.getImages().size() > 0) {
                holder.image.setVisibility(View.VISIBLE);
                Glide.with(holder.itemView.getContext()).load(result.getImages().get(0))
                        .asBitmap().centerCrop().into(holder.image);
            }
        }

        holder.text.setText(result.getDesc());
        if (fromSearch) {
            holder.author.setText(result.getType() + " · " + result.getWho() + " · " + TimeUtils.friendlyTimeFormat(result.getPublishedAt()));
        } else {
            holder.author.setText(result.getWho() + " · " + TimeUtils.friendlyTimeFormat(result.getPublishedAt()));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v, position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView image;
        TextView author;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            image = (ImageView) itemView.findViewById(R.id.image);
            author = (TextView) itemView.findViewById(R.id.author);
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void OnItemClick(View v, int position);
    }


}
