package net.questionbank.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/customExam")
public class TestController {

    /*
    문항 편집(문제 목록, 문제지요약)
    step1에서 받은 과목 id 값
    step2/sub03_01_01로 리턴
     */
    @GetMapping("/step2")
    public String getItemIds(Model model){
        String[] questionIds ={"494519", "494552"
                ,"487868"
                , "494553"
                , "493140"
                , "493137"
                , "493139"
                , "493141"
                , "487792"
                , "494581"
                , "494582"
                , "493122"
                , "493123"
                , "487816"
                , "494528"
                , "493138"
                ,"487866"
                ,"487867"
                ,"493179"};
        model.addAttribute("questionIds", questionIds);
        return "test/step2";
    }
}
