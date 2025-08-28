package com.example.ranking.interfaces;

import com.example.ranking.application.quiz.DailyQuizReadService;
import com.example.ranking.domain.user.UserAuthDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class ViewController {

    private final DailyQuizReadService dailyQuizReadService;

    @GetMapping("/")
    public String getMainPage(@AuthenticationPrincipal UserDetails userDetails, Model model){

        model.addAttribute("isLoggedIn", true);
        model.addAttribute("nickname", ((UserAuthDetails) userDetails).getUser().nickname());

        return "index";
    }

    @GetMapping("/login")
    public String getLoginPage(@RequestParam(required = false, defaultValue = "false") boolean error, Model model){

        if (error) {
            model.addAttribute("errorMessage", "아이디 및 비밀번호를 확인해주세요.");
        }

        return "login";
    }

    @GetMapping("/quiz")
    public String getQuizPage(@AuthenticationPrincipal UserDetails userDetails, Model model){

        model.addAttribute("isLoggedIn", true);
        model.addAttribute("nickname", ((UserAuthDetails) userDetails).getUser().nickname());
        model.addAttribute("questions", dailyQuizReadService.findAllActiveQuestions());

        return "quiz";
    }

    @GetMapping("/quiz/result")
    public String getResultPage(@AuthenticationPrincipal UserDetails userDetails, Model model){

        model.addAttribute("isLoggedIn", true);
        model.addAttribute("nickname", ((UserAuthDetails) userDetails).getUser().nickname());
        model.addAttribute("results", dailyQuizReadService.findUserQuizAttemptHistories(userDetails.getUsername()));

        return "result";
    }

    @GetMapping("/ranking")
    public String getRankingPage(@AuthenticationPrincipal UserDetails userDetails, Model model){

        model.addAttribute("isLoggedIn", true);
        model.addAttribute("nickname", ((UserAuthDetails) userDetails).getUser().nickname());

        return "ranking";
    }

}
