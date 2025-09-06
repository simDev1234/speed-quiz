package com.example.ranking.interfaces.quiz;

import com.example.ranking.application.quiz.DailyQuizWriteService;
import com.example.ranking.domain.quiz.request.QuizCreateRequest.*;
import com.example.ranking.domain.quiz.request.QuizSubmitRequest.UserAnswerChoice;
import com.example.ranking.global.exception.HttpApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/quiz")
@RequiredArgsConstructor
public class QuizRestController {

    private final DailyQuizWriteService dailyQuizWriteService;

    @PostMapping
    public HttpApiResponse<Void> createQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody QuizCreate quizCreateRequest){

        dailyQuizWriteService.saveNewQuiz(userDetails.getUsername(), quizCreateRequest);

        return HttpApiResponse.success();
    }

    @PostMapping("/submit")
    public HttpApiResponse<Void> submitQuizAnswers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody List<UserAnswerChoice> userAnswerChoices)
    {

        dailyQuizWriteService.saveQuizAnswers(userDetails.getUsername(), userAnswerChoices);

        return HttpApiResponse.success();
    }

}
