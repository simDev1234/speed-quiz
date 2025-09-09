package com.example.ranking.interfaces.quiz;

import com.example.ranking.application.quiz.DailyQuizWriteService;
import com.example.ranking.domain.quiz.request.QuizCreateRequest.QuizCreate;
import com.example.ranking.domain.quiz.request.QuizEditRequest.QuizEdit;
import com.example.ranking.domain.quiz.request.QuizSubmitRequest.UserAnswerChoice;
import com.example.ranking.global.exception.HttpApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quiz")
@RequiredArgsConstructor
public class QuizRestController {

    private final DailyQuizWriteService dailyQuizWriteService;

    @PostMapping
    public HttpApiResponse<Void> createQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody QuizCreate quizCreate){

        dailyQuizWriteService.saveNewQuiz(userDetails.getUsername(), quizCreate);

        return HttpApiResponse.success();
    }

    @PutMapping("/{questionTitleId}")
    public HttpApiResponse<Void> editQuiz(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestBody QuizEdit quizEdit,
                                          @PathVariable Long questionTitleId){

        dailyQuizWriteService.editExistingQuiz(userDetails.getUsername(), questionTitleId, quizEdit);

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

    @DeleteMapping("/{questionTitleId}")
    public HttpApiResponse<Void> deleteQuiz(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long questionTitleId) {
        dailyQuizWriteService.deleteQuiz(questionTitleId, userDetails.getUsername());
        return HttpApiResponse.success();
    }

}
