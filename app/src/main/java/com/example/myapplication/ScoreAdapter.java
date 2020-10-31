package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.viewholder> {
    private List<ScoreData> scoreDataList;

    public ScoreAdapter(List<ScoreData> scoreDataList) {
        this.scoreDataList = scoreDataList;
    }

    @NonNull
    @Override
    public ScoreAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item, parent, false);
        return new viewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ScoreAdapter.viewholder holder, int position) {
        holder.setData(scoreDataList.get(position).getCategory(),
                       String.valueOf(scoreDataList.get(position).getResult()),
                String.valueOf(scoreDataList.get(position).getTotal()));
    }

    @Override
    public int getItemCount() {
        return scoreDataList.size();
    }

    static class viewholder extends RecyclerView.ViewHolder {
        private TextView category, score, total;

        public viewholder(@NonNull View itemview) {
            super(itemview);
            category = itemview.findViewById(R.id.category);
            score = itemview.findViewById(R.id.score);
            total = itemview.findViewById(R.id.total);

        }

        public void setData(final String category, final String score, final String total) {
            this.category.setText(category);
            this.score.setText(score);
            this.total.setText(total);


        }

    }
}


