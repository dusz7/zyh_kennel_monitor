package com.helper.dusz7.newkennelmonitor.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.helper.dusz7.newkennelmonitor.Kennel;
import com.helper.dusz7.newkennelmonitor.KennelInfoActivity;
import com.helper.dusz7.newkennelmonitor.KennelState;
import com.helper.dusz7.newkennelmonitor.R;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by dusz7 on 20180215.
 */

public class KennelAdapter extends RecyclerView.Adapter<KennelAdapter.ViewHolder> {

    private List<KennelState> mKennelList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View kennelView;
        TextView idTextView;
        TextView stateTextView;
        ImageView stateImageView;

        public ViewHolder(View view) {
            super(view);

            kennelView = view;
            idTextView = view.findViewById(R.id.id_textview);
            stateImageView = view.findViewById(R.id.state_imageview);
            stateTextView = view.findViewById(R.id.state_textview);
        }
    }

    public KennelAdapter(List<KennelState> kennelList) {
        mKennelList = kennelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kennel_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.kennelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                KennelState kennelState = mKennelList.get(position);
                Intent intent = new Intent(v.getContext(), KennelInfoActivity.class);
                intent.putExtra(KennelInfoActivity.KENNEL_ID, kennelState.getKennelId());
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        KennelState kennelState = mKennelList.get(position);
        List<Kennel> kennels = DataSupport.where("kennelId=?", kennelState.getKennelId()).find(Kennel.class);
        if (0 == kennels.size()) {
            Kennel kennel = new Kennel(kennelState.getKennelId());
            kennel.save();
        } else {
            Kennel kennel = kennels.get(0);
            if (kennel.getNickname() != null && !kennel.getNickname().equals("")) {
                holder.idTextView.setText(kennel.getNickname());
            } else {
                holder.idTextView.setText(kennel.getId());
            }
        }

        if (kennelState.getKennelState() == (KennelState.STATE_NORMAL)) {
            holder.stateImageView.setImageResource(R.drawable.ic_state_normal);
            holder.stateImageView.setColorFilter(Color.argb(255, 0, 128, 0));
            holder.stateTextView.setText("normal");
        } else if (kennelState.getKennelState() == ((KennelState.STATE_OPERATION))) {
            holder.stateImageView.setImageResource(R.drawable.ic_state_operation);
            holder.stateImageView.setColorFilter(Color.argb(255, 128, 128, 0));
            holder.stateTextView.setText("operating");
        } else if (kennelState.getKennelState() == (KennelState.STATE_ERROR)) {
            holder.stateImageView.setImageResource(R.drawable.ic_state_error);
            holder.stateImageView.setColorFilter(Color.argb(255, 128, 0, 0));
            holder.stateTextView.setText("warning");
        }
    }

    @Override
    public int getItemCount() {
        return mKennelList.size();
    }

}
