package com.Project.QuizApp.Service;

import com.Project.QuizApp.DAO.QuestionRepo;
import com.Project.QuizApp.Model.QuestionClass;
import com.Project.QuizApp.QuizAppException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepo questionRepo;

    public QuestionService(QuestionRepo questionRepo) {
        this.questionRepo = questionRepo;
    }

    public List<QuestionClass> getAllQuestions() {
        try{
            List<QuestionClass> listOfQuestions = questionRepo.findAll();
            if(listOfQuestions.isEmpty()) {
                throw new QuizAppException("No Question is found on database.");
            }
            return listOfQuestions;
        } catch (Exception e)
        {
            throw new QuizAppException("Failed to retrieve Question: " + e.getMessage());
        }
    }

    public List<QuestionClass> getQuestionByCategory(String category) {
        return questionRepo.findByCategory(category);
    }

    public QuestionClass addQuestion(QuestionClass questionClass) {
       return questionRepo.save(questionClass);
    }
}
