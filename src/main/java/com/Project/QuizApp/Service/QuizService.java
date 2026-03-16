package com.Project.QuizApp.Service;

import com.Project.QuizApp.DAO.QuestionRepo;
import com.Project.QuizApp.DAO.QuizDao;
import com.Project.QuizApp.Model.*;
import com.Project.QuizApp.QuizAppException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {

    private final QuizDao quizDao;
    private final QuestionRepo questionRepo;
    private final AIFeedbackService  aiFeedbackService;   // ← NEW

    public QuizService(QuizDao quizDao,
                       QuestionRepo questionRepo,
                       AIFeedbackService aiFeedbackService) {  // ← NEW
        this.quizDao           = quizDao;
        this.questionRepo      = questionRepo;
        this.aiFeedbackService = aiFeedbackService;
    }

    public QuizClass createQuiz(String category, Integer numQ, String title) {
        if (numQ <= 0) {
            throw new QuizAppException("Number of question should be positive.");
        }
        try {
            List<QuestionClass> availableQuestion = questionRepo.findByCategory(category);
            if (availableQuestion.isEmpty()) {
                throw new QuizAppException("No question found of this category: " + category);
            }
            if (availableQuestion.size() < numQ) {
                throw new QuizAppException("Not enough question in category:" + category + ". Found" + availableQuestion.size());
            }

            Collections.shuffle(availableQuestion);

            QuizClass quizClass = new QuizClass();
            quizClass.setTitle(title);
            List<QuestionClass> selectedQuestion = availableQuestion.subList(0, numQ);
            quizClass.setQuestionList(selectedQuestion);
            return quizDao.save(quizClass);

        } catch (QuizAppException e) {
            throw new QuizAppException("Failed to create Quiz: " + e.getMessage());
        }
    }


    public QuizWrapper getQuizQuestion(Integer id) {
        QuizClass quiz = quizDao.findById(id)
                .orElseThrow(() -> new QuizAppException("Quiz with id " + id + "not found"));

        List<QuestionClass> questionsListFromDB = quiz.getQuestionList();
        List<QuestionWrapper> questionListForUser = new ArrayList<>();

        for (QuestionClass q : questionsListFromDB) {
            QuestionWrapper qw = new QuestionWrapper(
                    q.getId(),
                    q.getQuestionTitle(),
                    q.getOption1(),
                    q.getOption2(),
                    q.getOption3(),
                    q.getOption4()
            );
            questionListForUser.add(qw);
        }
        return new QuizWrapper(quiz.getId(), quiz.getTitle(), questionListForUser);
    }

    // ── submitQuiz — UPDATED: returns QuizResultWrapper instead of Integer ──

    public QuizResultWrapper submitQuiz(Integer id, List<QuizSubmissionResponse> quizSubmissionResponseList) {

        // 1. Fetch the quiz
        QuizClass quizClass = quizDao.findById(id)
                .orElseThrow(() -> new QuizAppException("Quiz with id " + id + " not found"));

        // 2. Build answer key: questionId → rightAnswer
        Map<Integer, String> answerKey = buildAnswerKey(quizSubmissionResponseList, quizClass);

        // 3. Score answers and build per-question detail
        int score = 0;
        List<QuizResultWrapper.QuestionResultDetail> resultDetails = new ArrayList<>();
        List<String> wrongQuestionTexts = new ArrayList<>(); // sent to AI

        // Build a map of questionId → questionTitle for feedback prompt
        Map<Integer, String> questionTitleMap = new HashMap<>();
        for (QuestionClass qc : quizClass.getQuestionList()) {
            questionTitleMap.put(qc.getId(), qc.getQuestionTitle());
        }

        for (QuizSubmissionResponse response : quizSubmissionResponseList) {

            String rightAnswer = answerKey.get(response.getId());
            if (rightAnswer == null) {
                throw new QuizAppException("Question ID " + response.getId() + " not found in quiz");
            }

            boolean isCorrect = rightAnswer.equals(response.getResponse());
            if (isCorrect) {
                score++;
            } else {
                // Collect wrong question titles for the AI prompt
                String title = questionTitleMap.getOrDefault(response.getId(), "Unknown question");
                wrongQuestionTexts.add(title);
            }

            resultDetails.add(new QuizResultWrapper.QuestionResultDetail(
                    response.getId(),
                    questionTitleMap.getOrDefault(response.getId(), ""),
                    response.getResponse(),
                    rightAnswer,
                    isCorrect
            ));
        }

        int total          = quizSubmissionResponseList.size();
        int scorePct       = total > 0 ? (score * 100) / total : 0;
        String perfLabel   = resolvePerformanceLabel(scorePct);

        // 4. Call Ollama via AIFeedbackService ← THE AI CALL
        String aiFeedback = aiFeedbackService.generateFeedback(
                quizClass.getTitle(),
                score,
                total,
                wrongQuestionTexts
        );

        // 5. Return the rich response
        return new QuizResultWrapper(
                quizClass.getId(), quizClass.getTitle(), total, score,
                scorePct, perfLabel, aiFeedback, resultDetails, java.time.LocalDateTime.now()
        );
    }

    // ── Private helpers ───────────────────────────────────────────────

    private Map<Integer, String> buildAnswerKey(
            List<QuizSubmissionResponse> submissions,
            QuizClass quizClass
    ) {
        List<QuestionClass> questionsFromDB = quizClass.getQuestionList();

        if (submissions == null || submissions.size() != questionsFromDB.size()) {
            throw new QuizAppException(
                    "Invalid submission: Number of answers must match number of questions (" + questionsFromDB.size() + ")"
            );
        }

        Map<Integer, String> map = new HashMap<>();
        for (QuestionClass q : questionsFromDB) {
            map.put(q.getId(), q.getRightAnswer());
        }
        return map;
    }

    private String resolvePerformanceLabel(int percentage) {
        if (percentage >= 90) return "Outstanding";
        if (percentage >= 75) return "Good";
        if (percentage >= 50) return "Average";
        return "Needs Improvement";
    }
}