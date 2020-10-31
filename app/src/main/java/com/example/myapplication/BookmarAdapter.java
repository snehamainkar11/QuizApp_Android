package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class BookmarAdapter extends RecyclerView.Adapter<BookmarAdapter.viewholder> {
    private List<QuestionModel> questionModels;

    public BookmarAdapter(List<QuestionModel> questionModels) {
        this.questionModels = questionModels;
    }
    class viewholder extends RecyclerView.ViewHolder {
        TextView question, answer;
        ImageButton delete;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            delete=itemView.findViewById(R.id.delete);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
        }
        public void setData( final String questions, final String answers,final int position) {
            this.question.setText(questions);
            this.answer.setText("ANSWER:"+answers);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    questionModels.remove(position);
                    notifyItemRemoved(position);
                }
            });

        }
    }
    @NonNull
    @Override
    public BookmarAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
        return new BookmarAdapter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarAdapter.viewholder holder, int position) {
        holder.setData(questionModels.get(position).getQuestion(),questionModels.get(position).getCorrectAns(),position);

    }

    @Override
    public int getItemCount() {
        return questionModels.size();
    }


}
