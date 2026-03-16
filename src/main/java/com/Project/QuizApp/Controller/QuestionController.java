package com.Project.QuizApp.Controller;

import com.Project.QuizApp.Model.QuestionClass;
import com.Project.QuizApp.QuizAppException;
import com.Project.QuizApp.Service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/allQuestions")
    public ResponseEntity<List<QuestionClass>> getAllQuestions() {
        try {
            List<QuestionClass> listOfQuestions = questionService.getAllQuestions();
            return ResponseEntity.ok(listOfQuestions);
        }catch (QuizAppException e)
        {
            if(e.getMessage().contains("No Question Found"))
            {
                return ResponseEntity.status(404).body(null);
            }
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/category/{category}")
    public List<QuestionClass> getQuestionsByCategory(@PathVariable String category)
    {
        return questionService.getQuestionByCategory(category);
    }

    @PostMapping("/addQuestion")
    public ResponseEntity<?> addQuestion(@RequestBody QuestionClass questionClass)
    {
        try {
            QuestionClass savedQuestion = questionService.addQuestion(questionClass);
            return ResponseEntity.status(201).body(savedQuestion);
        }catch (QuizAppException e)
        {
            if(e.getMessage().contains("cannot be empty") || e.getMessage().contains("Right Answer."))
            {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            return ResponseEntity.status(500).body("SERVER ERROR: " + e.getMessage());
        }
    }
}