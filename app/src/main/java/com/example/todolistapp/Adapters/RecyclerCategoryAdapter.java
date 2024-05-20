package com.example.todolistapp.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.DbHelper;
import com.example.todolistapp.HomeActivity;
import com.example.todolistapp.R;

import java.util.ArrayList;

public class RecyclerCategoryAdapter extends RecyclerView.Adapter<RecyclerCategoryAdapter.ViewHolder> {
    Context context;
    ArrayList<String> arrCategories;
    DbHelper dbHelper;
    TextView previousSelectedCategory;
    TextView allCategory = null;

    public RecyclerCategoryAdapter(Context context, ArrayList<String> arrCategories) {
        this.context = context;
        this.arrCategories = arrCategories;
        dbHelper = new DbHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.categories_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.categoryName.setText(arrCategories.get(position));
        if (position == 0) {
            allCategory = holder.categoryName;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("prefs", MODE_PRIVATE);
        String selectedCategory = sharedPreferences.getString("category", "All");
        int selectedCategoryPosition = arrCategories.indexOf(selectedCategory);

        for (int i = 0; i < arrCategories.size(); i++) {
            if (position == selectedCategoryPosition) {
                holder.categoryName.setBackgroundResource(R.drawable.category_selected_bg);
            } else {
                holder.categoryName.setBackgroundResource(R.drawable.category_bg);
            }
        }

        if (HomeActivity.newCategoryAdded) {
            int lastPos = arrCategories.size() - 1;
            HomeActivity.categoryRecyclerView.scrollToPosition(lastPos);
            HomeActivity.newCategoryAdded = false;
        }

        holder.categoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousSelectedCategory != null) {
                    previousSelectedCategory.setBackgroundResource(R.drawable.category_bg);
                } else {
                    allCategory.setBackgroundResource(R.drawable.category_bg);
                }
                holder.categoryName.setBackgroundResource(R.drawable.category_selected_bg);
                String catName = holder.categoryName.getText().toString();
                SharedPreferences sharedPreferences = context.getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("category", catName);
                editor.apply();
                HomeActivity.notifyChangeToTasksRecyclerView();
                previousSelectedCategory = holder.categoryName;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrCategories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }
}
