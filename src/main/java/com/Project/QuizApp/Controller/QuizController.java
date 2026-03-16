package com.Project.QuizApp.Controller;

import com.Project.QuizApp.Model.*;
import com.Project.QuizApp.QuizAppException;
import com.Project.QuizApp.Service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/create")
    public QuizClass createQuiz(@RequestBody QuizCreationDto request) {
        // Pass the values from the JSON object down to your service
        return quizService.createQuiz(request.getCategory(), request.getNumQ(), request.getTitle());
    }

    @GetMapping("/getQuizBy/{id}")
    public ResponseEntity<QuizWrapper> getQuizQuestion(@PathVariable("id") Integer id) {
        try {
            QuizWrapper quizWrapper = quizService.getQuizQuestion(id);
            return ResponseEntity.status(200).body(quizWrapper);
        } catch (QuizAppException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/submit/{id}")
    public ResponseEntity<QuizResultWrapper> submitQuiz(
            @PathVariable Integer id,
            @RequestBody List<QuizSubmissionResponse> quizSubmission
    ) {
        try {
            QuizResultWrapper result = quizService.submitQuiz(id, quizSubmission);
            return ResponseEntity.ok(result);
        } catch (QuizAppException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}