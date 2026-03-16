package com.Project.QuizApp.Model;

public class QuizCreationDto {
    private String category;
    private Integer numQ;
    private String title;

    public QuizCreationDto() {}

    // Getters
    public String getCategory() { return category; }
    public Integer getNumQ() { return numQ; }
    public String getTitle() { return title; }

    // Setters
    public void setCategory(String category) { this.category = category; }
    public void setNumQ(Integer numQ) { this.numQ = numQ; }
    public void setTitle(String title) { this.title = title; }
}