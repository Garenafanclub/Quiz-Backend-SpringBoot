package com.Project.QuizApp.Model;

import java.util.List;

public class QuizWrapper {
    private Integer id;
    private String title;
    private List<QuestionWrapper> questions;

    public QuizWrapper(Integer id, String title, List<QuestionWrapper> questions) {
        this.id = id;
        this.title = title;
        this.questions = questions;
    }

    // Default constructor for Jackson
    public QuizWrapper() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<QuestionWrapper> getQuestions() { return questions; }
    public void setQuestions(List<QuestionWrapper> questions) { this.questions = questions; }
}
