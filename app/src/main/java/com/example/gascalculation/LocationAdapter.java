package com.example.gascalculation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gascalculation.databinding.ItemLocationBinding;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private final List<LocationItem> locationList;
    private LocationItem recentlyDeletedItem;
    private int recentlyDeletedItemPosition;

    public LocationAdapter(List<LocationItem> locationList) {
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemLocationBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_location, parent, false);
        return new LocationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LocationItem currentItem = locationList.get(position);
        holder.bind(currentItem);  // Bind the data to the view
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        private final ItemLocationBinding binding;

        public LocationViewHolder(@NonNull ItemLocationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(LocationItem locationItem) {
            binding.setLocationItem(locationItem);  // Bind locationItem to the layout
            binding.executePendingBindings();  // This ensures that the binding is executed immediately
        }
    }

    // Remove an item from the list
    public void deleteItem(int position) {
        recentlyDeletedItem = locationList.get(position);
        recentlyDeletedItemPosition = position;
        locationList.remove(position);
        notifyItemRemoved(position);
    }

    // Restore the recently deleted item
    public void restoreItem() {
        locationList.add(recentlyDeletedItemPosition, recentlyDeletedItem);
        notifyItemInserted(recentlyDeletedItemPosition);
    }
}

