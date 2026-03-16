package com.Project.QuizApp.Model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz")
public class QuizClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;

    @ManyToMany
    @JoinTable(
            name = "quiz_question",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<QuestionClass> question = new ArrayList<>();

    public QuizClass(){}

    public QuizClass(Integer id, String title, List<QuestionClass> questionList) {
        this.id = id;
        this.title = title;
        this.question = questionList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<QuestionClass> getQuestionList() {
        return question;
    }

    public void setQuestionList(List<QuestionClass> questionList) {
        this.question = questionList;
    }

    @Override
    public String toString() {
        return "QuizClass{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", questionList=" + question +
                '}';
    }
}
