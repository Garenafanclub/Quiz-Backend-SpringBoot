package com.Project.QuizApp.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIFeedbackService {

    private final ChatClient chatClient;

    // Spring AI auto-configures ChatClient.Builder — just inject it
    public AIFeedbackService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String generateFeedback(
            String quizTitle,
            int correctAnswers,
            int totalQuestions,
            List<String> wrongQuestions
    ) {
        String prompt = buildPrompt(quizTitle, correctAnswers, totalQuestions, wrongQuestions);

        try {
            return chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .content();

        } catch (Exception e) {
            // Graceful fallback — app still works if Ollama is down
            return buildFallbackFeedback(correctAnswers, totalQuestions);
        }
    }


    // ─────────────────────────────────────────────────────────────────
    private String buildPrompt(String quizTitle, int correct, int total, List<String> wrongQuestions) {

        int percentage   = total > 0 ? (correct * 100) / total : 0;
        String perfLevel = resolvePerformanceLabel(percentage);

        StringBuilder sb = new StringBuilder();

        // 1. Role — tell the LLM who it is
        sb.append("You are a supportive and knowledgeable quiz coach. ")
                .append("Your job is to give a student honest, actionable, and encouraging feedback.\n\n");

        // 2. Data — what actually happened in the quiz
        sb.append("=== QUIZ SUBMISSION DETAILS ===\n");
        sb.append("Quiz Title  : ").append(quizTitle).append("\n");
        sb.append("Score       : ").append(correct).append(" out of ").append(total)
                .append(" (").append(percentage).append("%)\n");
        sb.append("Performance : ").append(perfLevel).append("\n\n");

        if (!wrongQuestions.isEmpty()) {
            sb.append("Questions answered incorrectly:\n");
            for (String q : wrongQuestions) {
                sb.append("  - ").append(q).append("\n");
            }
        } else {
            sb.append("The student answered ALL questions correctly!\n");
        }

        // 3. Instruction — exactly what we want back
        sb.append("\n=== YOUR TASK ===\n");
        sb.append("Write a feedback report with exactly these 4 sections:\n\n");
        sb.append("1. PERFORMANCE SUMMARY: 2-3 sentences on how the student did overall.\n");
        sb.append("2. AREAS TO IMPROVE: Based on the wrong questions, identify 2-3 specific concepts to revisit. If all correct, mention what to explore next.\n");
        sb.append("3. STUDY TIPS: Give 2-3 concrete and actionable study strategies.\n");
        sb.append("4. ENCOURAGEMENT: End with a short motivational message.\n\n");
        sb.append("Rules: Keep it under 200 words. Use plain text — no markdown symbols like ** or ##. Be warm and professional.");

        return sb.toString();
    }

    private String resolvePerformanceLabel(int percentage) {
        if (percentage >= 90) return "Outstanding";
        if (percentage >= 75) return "Good";
        if (percentage >= 50) return "Average";
        return "Needs Improvement";
    }

    // Used when Ollama is not running — app doesn't crash
    private String buildFallbackFeedback(int correct, int total) {
        int pct = total > 0 ? (correct * 100) / total : 0;
        return String.format(
                "You scored %d out of %d (%d%%). " +
                        "AI feedback is temporarily unavailable. " +
                        "Please review the questions you got wrong and revisit those concepts. Keep it up!",
                correct, total, pct
        );
    }
}