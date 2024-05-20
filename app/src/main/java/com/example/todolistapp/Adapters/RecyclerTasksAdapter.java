package com.example.todolistapp.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;


import com.example.todolistapp.DbHelper;
import com.example.todolistapp.HomeActivity;
import com.example.todolistapp.ModelClasses.TasksModel;
import com.example.todolistapp.R;

import java.util.ArrayList;

public class RecyclerTasksAdapter extends RecyclerView.Adapter<RecyclerTasksAdapter.ViewHolder> {
    Context context;
    ArrayList<TasksModel> arrTasks;
    DbHelper dbHelper;

    public ArrayList<TasksModel> getArrTasks() {
        return arrTasks;
    }

    public RecyclerTasksAdapter(Context context, ArrayList<TasksModel> arrTasks){
        this.context= context;
        this.arrTasks = arrTasks;
        dbHelper = new DbHelper(context);
    }

    @NonNull
    @Override
    public RecyclerTasksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tasks_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerTasksAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.taskTitle.setText(arrTasks.get(position).getTitle());

        if(dbHelper.getTaskStatus(arrTasks.get(position).getId()).equals("completed")){
            holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.checkBox.setChecked(true);
        } else{
            holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.checkBox.setChecked(false);
        }

        holder.taskDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
                deleteDialog.setIcon(R.drawable.baseline_delete_24);
                deleteDialog.setTitle("Delete Task?")
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.deleteTask(arrTasks.get(position).getId());
                                arrTasks.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, getItemCount() - position);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                deleteDialog.show();
            }
        });

        holder.taskEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog editTaskDialog = new Dialog(context);
                editTaskDialog.setContentView(R.layout.custom_dialog_layout);

                    Window window = editTaskDialog.getWindow();
                    if (window != null) {
                        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    }

                EditText editedTitle = editTaskDialog.findViewById(R.id.inputEditText);
                AppCompatButton editTaskBtn = editTaskDialog.findViewById(R.id.actionBtn);
                TextView dialogHeading = editTaskDialog.findViewById(R.id.dialogHeading);
                ImageView closeDialog = editTaskDialog.findViewById(R.id.closeDialog);
                dialogHeading.setText("Edit Task");
                editedTitle.setText(holder.taskTitle.getText().toString());
                editTaskBtn.setText("Edit Task");

                closeDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editTaskDialog.dismiss();
                    }
                });

                editTaskBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = editedTitle.getText().toString();
                        if(title.trim().equals("")){
                            Toast.makeText(context, "Enter some task", Toast.LENGTH_SHORT).show();
                        }else{
                            SharedPreferences sharedPreferences = context.getSharedPreferences("prefs", MODE_PRIVATE);
                            String category = sharedPreferences.getString("category", "All");
                            dbHelper.editTask(arrTasks.get(position).getId(),title);
                            notifyItemChanged(position);
                            arrTasks=dbHelper.fetchTasks(category);
                            editTaskDialog.dismiss();
                        }
                    }
                });
                editTaskDialog.show();
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    dbHelper.updateTaskStatus(arrTasks.get(position).getId(),"completed");
                } else {
                    holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    dbHelper.updateTaskStatus(arrTasks.get(position).getId(),"pending");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrTasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        ImageView taskDeleteBtn, taskEditBtn;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDeleteBtn = itemView.findViewById(R.id.taskDeleteBtn);
            taskEditBtn = itemView.findViewById(R.id.taskEditBtn);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
