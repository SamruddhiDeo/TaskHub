package com.example.todolistapp;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolistapp.Adapters.RecyclerCategoryAdapter;
import com.example.todolistapp.Adapters.RecyclerTasksAdapter;
import com.example.todolistapp.ModelClasses.TasksModel;
import com.example.todolistapp.databinding.ActivityHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    public static RecyclerView tasksRecyclerView;
    public static RecyclerView categoryRecyclerView;
    public static SearchView searchView;
    public static RecyclerTasksAdapter recyclerTasksAdapter;
    public static RecyclerCategoryAdapter recyclerCategoryAdapter;
    public static DbHelper dbHelper;
    public static ArrayList<TasksModel> arrTasks = new ArrayList();
    public static ArrayList<String> arrCategories = new ArrayList();
    DrawerLayout drawerLayout;
    LinearLayout drawerHeaderLayout;
    LinearLayout.LayoutParams searchViewLayoutParams;
    Toolbar toolbar;
    TextView toolbarTitle;
    NavigationView navigationView;
    public static boolean newCategoryAdded = false;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("category", "All");
        editor.apply();

        //initializations
        searchView = findViewById(R.id.searchView);
        dbHelper = new DbHelper(this);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        FloatingActionButton addTaskFloatingBtn = findViewById(R.id.addTaskFloatingBtn);
        drawerHeaderLayout = findViewById(R.id.drawerHeaderLayout);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        searchViewLayoutParams = (LinearLayout.LayoutParams) searchView.getLayoutParams();

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.OpenDrawer, R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        notifyChangeToCategoryRecyclerView();
        notifyChangeToTasksRecyclerView();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.menuHome) {
                    editor.putString("category", "All");
                    editor.apply();
                    notifyChangeToCategoryRecyclerView();
                    notifyChangeToTasksRecyclerView();
                } else if (id == R.id.menuCategories) {

                } else if (id == R.id.menuAddCategory) {
                    Dialog addCategoryDialog = new Dialog(HomeActivity.this);
                    addCategoryDialog.setContentView(R.layout.custom_dialog_layout);

                    Window window = addCategoryDialog.getWindow();
                    if (window != null) {
                        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    }

                    EditText categoryName = addCategoryDialog.findViewById(R.id.inputEditText);
                    AppCompatButton addCategoryBtn = addCategoryDialog.findViewById(R.id.actionBtn);
                    TextView dialogHeading = addCategoryDialog.findViewById(R.id.dialogHeading);
                    ImageView closeDialog = addCategoryDialog.findViewById(R.id.closeDialog);
                    dialogHeading.setText("Add Category");
                    categoryName.setHint("Enter category name");
                    addCategoryBtn.setText("Done");

                    closeDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addCategoryDialog.dismiss();
                        }
                    });

                    addCategoryBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String title = categoryName.getText().toString();
                            if (title.trim().equals("")) {
                                Toast.makeText(HomeActivity.this, "Enter category name", Toast.LENGTH_SHORT).show();
                            } else {
                                dbHelper.addCategory(title);
                                editor.putString("category", title);
                                editor.apply();
                                newCategoryAdded = true;
                                notifyChangeToCategoryRecyclerView();

                                addCategoryDialog.dismiss();
                                addCategoriesInNavView();
                                notifyChangeToTasksRecyclerView();
                            }
                        }
                    });
                    addCategoryDialog.show();

                } else {
                    String clickedCategory = item.getTitle().toString();
                    editor.putString("category", clickedCategory);
                    editor.apply();
                    notifyChangeToTasksRecyclerView();
                    notifyChangeToCategoryRecyclerView();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        addCategoriesInNavView();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarTitle.setVisibility(View.GONE);
                searchViewLayoutParams.weight = 1;
                searchView.setLayoutParams(searchViewLayoutParams);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                closeSearchView();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                notifyChangeToTasksRecyclerView();
                performSearch(newText);
                return true;
            }
        });

        addTaskFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog addTaskDialog = new Dialog(HomeActivity.this);
                addTaskDialog.setContentView(R.layout.custom_dialog_layout);

                Window window = addTaskDialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                }

                EditText taskTitle = addTaskDialog.findViewById(R.id.inputEditText);
                AppCompatButton addTaskBtn = addTaskDialog.findViewById(R.id.actionBtn);
                ImageView closeDialog = addTaskDialog.findViewById(R.id.closeDialog);

                closeDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addTaskDialog.dismiss();
                    }
                });

                addTaskBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = taskTitle.getText().toString();
                        if (title.trim().equals("")) {
                            Toast.makeText(HomeActivity.this, "Enter some task", Toast.LENGTH_SHORT).show();
                        } else {
                            String category = sharedPreferences.getString("category", "All");
                            dbHelper.addTask(new TasksModel(title, category, "pending"));
                            HomeActivity.notifyChangeToTasksRecyclerView();
                            HomeActivity.tasksRecyclerView.smoothScrollToPosition(HomeActivity.arrTasks.size() - 1);
                            addTaskDialog.dismiss();
                        }

                    }
                });
                addTaskDialog.show();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (toolbarTitle.getVisibility() == View.GONE) {
                    closeSearchView();
                } else {
                    AlertDialog.Builder exitApp = new AlertDialog.Builder(HomeActivity.this);
                    exitApp.setTitle("Exit?")
                            .setMessage("Are you sure you want to leave the app?")
                            .setIcon(R.drawable.baseline_exit_to_app_24)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finishAffinity();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    exitApp.show();
                }
            }
        });
    }

    public static void notifyChangeToTasksRecyclerView() {
        SharedPreferences sharedPreferences = tasksRecyclerView.getContext().getSharedPreferences("prefs", MODE_PRIVATE);
        String category = sharedPreferences.getString("category", "All");
        arrTasks = dbHelper.fetchTasks(category);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(tasksRecyclerView.getContext()));
        recyclerTasksAdapter = new RecyclerTasksAdapter(tasksRecyclerView.getContext(), arrTasks);
        tasksRecyclerView.setAdapter(recyclerTasksAdapter);
    }

    public static void notifyChangeToCategoryRecyclerView() {
        arrCategories = dbHelper.fetchCategories();
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(tasksRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerCategoryAdapter = new RecyclerCategoryAdapter(tasksRecyclerView.getContext(), arrCategories);
        categoryRecyclerView.setAdapter(recyclerCategoryAdapter);
    }

    public static void searchTasksRecyclerView(ArrayList<TasksModel> filteredTasks) {
        arrTasks = filteredTasks;
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(tasksRecyclerView.getContext()));
        recyclerTasksAdapter = new RecyclerTasksAdapter(tasksRecyclerView.getContext(), arrTasks);
        tasksRecyclerView.setAdapter(recyclerTasksAdapter);
    }

    public static void performSearch(String query) {
        ArrayList<TasksModel> filteredNotes = new ArrayList<>();
        for (TasksModel tasksModel : arrTasks) {
            if (tasksModel.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredNotes.add(tasksModel);
            }
        }
        searchTasksRecyclerView(filteredNotes);
    }

    private void closeSearchView() {
        searchViewLayoutParams.weight = 0;
        searchView.setLayoutParams(searchViewLayoutParams);
        searchView.onActionViewCollapsed();
        toolbar.collapseActionView();
        toolbarTitle.setVisibility(View.VISIBLE);
        searchView.setIconifiedByDefault(true);
    }

    public void addCategoriesInNavView() {
        arrCategories = dbHelper.fetchCategories();
        String newCategory;
        navigationView.getMenu().findItem(R.id.menuCategories).getSubMenu().clear();
        for (int i = 0; i < arrCategories.size(); i++) {
            newCategory = arrCategories.get(i);
            if (newCategory == "All") {
                continue;
            }
            navigationView.getMenu().findItem(R.id.menuCategories).getSubMenu().add(newCategory).setIcon(R.drawable.baseline_arrow_forward_ios_24);
        }
        navigationView.invalidate();
    }

}