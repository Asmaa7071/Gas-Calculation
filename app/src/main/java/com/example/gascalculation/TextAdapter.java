package com.example.gascalculation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.TextViewHolder> {

    private List<LocationItem> textList;

    // Constructor
    public TextAdapter() {
    }

    public void SetData(List<LocationItem> textList){
        this.textList = textList;
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_text layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        // Bind the data (text) to the TextView
        String text = textList.get(position).getLocationName();
        holder.textViewItem.setText(text);
    }

    @Override
    public int getItemCount() {
        // Return the size of the list
        return textList.size();
    }

    // ViewHolder class to hold the views for each item
    public static class TextViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewItem;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.address_name);
        }
    }
}

