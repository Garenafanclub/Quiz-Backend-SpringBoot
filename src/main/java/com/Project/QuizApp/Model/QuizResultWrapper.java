package com.Project.QuizApp.Model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultWrapper {

    private Integer quizId;
    private String  quizTitle;
    private int totalQuestions;
    private int correctAnswers;
    private int scorePercentage;
    private String performanceLabel;
    private String aiFeedback;           // AI-generated feedback from Ollama
    private List<QuestionResultDetail> results;
    private LocalDateTime submittedAt;

    // ── Inner class: per-question result ─────────────────────────────

    public static class QuestionResultDetail {

        private Integer questionId;
        private String  questionTitle;
        private String  yourAnswer;       // what the user submitted
        private String  rightAnswer;      // correct answer
        private boolean correct;

        public QuestionResultDetail() {}

        public QuestionResultDetail(
                Integer questionId,
                String questionTitle,
                String yourAnswer,
                String rightAnswer,
                boolean correct
        ) {
            this.questionId    = questionId;
            this.questionTitle = questionTitle;
            this.yourAnswer    = yourAnswer;
            this.rightAnswer   = rightAnswer;
            this.correct       = correct;
        }

        public Integer getQuestionId()   { return questionId; }
        public String getQuestionTitle() { return questionTitle; }
        public String getYourAnswer()    { return yourAnswer; }
        public String getRightAnswer()   { return rightAnswer; }
        public boolean isCorrect()       { return correct; }

        public void setQuestionId(Integer questionId)       { this.questionId = questionId; }
        public void setQuestionTitle(String questionTitle)  { this.questionTitle = questionTitle; }
        public void setYourAnswer(String yourAnswer)        { this.yourAnswer = yourAnswer; }
        public void setRightAnswer(String rightAnswer)      { this.rightAnswer = rightAnswer; }
        public void setCorrect(boolean correct)             { this.correct = correct; }
    }
}