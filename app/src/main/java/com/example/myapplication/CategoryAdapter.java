package com.example.myapplication;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewholder>{
    private List<CategoryModel> categoryModelList;

    public CategoryAdapter(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
    holder.setData(categoryModelList.get(position).getUrl(),categoryModelList.get(position).getName(),categoryModelList.get(position).getSets());
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    class viewholder extends RecyclerView.ViewHolder{
        private CircleImageView imageviews;
        private TextView title;

    public viewholder(@NonNull View itemview){
        super(itemview);
        imageviews=itemview.findViewById(R.id.imageview);
        title=itemview.findViewById(R.id.title);

    }
    public void setData(String url, final String title,final int sets){
        Glide.with(itemView.getContext()).load(url).into(imageviews);
        this.title.setText(title);
        itemView.setOnClickListener(new View.OnClickListener() {
           @Override

            public void onClick(View v) {
                Intent setIntent = new Intent(itemView.getContext(), SetsActivity.class);
                setIntent.putExtra("title", title);
                setIntent.putExtra("sets",sets);

                itemView.getContext().startActivity(setIntent);
            }
        });
    }
}
}