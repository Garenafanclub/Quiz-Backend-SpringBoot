package com.Project.QuizApp.Model;


public class QuizSubmissionResponse {
    private Integer id;
    private String response;

    public QuizSubmissionResponse(){}

    public QuizSubmissionResponse(Integer id, String response) {
        this.id = id;
        this.response = response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
