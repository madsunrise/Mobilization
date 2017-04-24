package com.rv150.mobilization.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rv150.mobilization.R;
import com.rv150.mobilization.model.Translation;

import java.util.List;

/**
 * Created by ivan on 16.04.17.
 */

public class TranslationListAdapter extends RecyclerView.Adapter<TranslationListAdapter.MyViewHolder> {

    private final List<Translation> serviceList;

    public TranslationListAdapter(List<Translation> serviceList) {
        this.serviceList = serviceList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.translation_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Translation translation = serviceList.get(position);
        holder.from.setText(translation.getFrom());
        holder.to.setText(translation.getTo());
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView from;
        TextView to;

        MyViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.item_text_from);
            to = (TextView) view.findViewById(R.id.item_text_to);
        }
    }
}

