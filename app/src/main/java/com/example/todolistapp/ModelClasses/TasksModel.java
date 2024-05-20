package com.example.todolistapp.ModelClasses;

public class TasksModel {
    int id;
    String title, category, status;

    public TasksModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TasksModel(String title, String category, String status) {
        this.title = title;
        this.category = category;
        this.status = status;
    }
}
