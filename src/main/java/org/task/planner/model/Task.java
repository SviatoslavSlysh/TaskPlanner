package org.task.planner.model;

import lombok.Data;

@Data
public class Task {
    private int id;
    private int userId;
    private String category;
    private String name;
    private String description;
    private boolean status;
    private String targetDate;

    public Task(String category, String name, String description, boolean status, String targetDate) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.status = status;
        this.targetDate = targetDate;
    }

    public Task() {

    }
}
