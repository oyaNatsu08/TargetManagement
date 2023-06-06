package com.example.TargetManagement.controller;

import com.example.TargetManagement.entity.DetailRecord;
import com.example.TargetManagement.entity.TargetRecord;
import com.example.TargetManagement.entity.UserRecord;
import com.example.TargetManagement.form.LoginForm;
import com.example.TargetManagement.form.TargetForm;
import com.example.TargetManagement.form.UserForm;
import com.example.TargetManagement.service.ManagementService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TargetController {

    @Autowired
    private ManagementService targetService;

    @Autowired
    private HttpSession session;

    //ログインページへ
    @GetMapping("/login")
    public String login(@ModelAttribute("LoginForm")LoginForm loginForm ) {
        return "login";
    }

    //ログインチェック処理
    @PostMapping("/login")
    public String loginCheck(@Validated @ModelAttribute("LoginForm") LoginForm loginForm,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            //System.out.println(loginForm);
            return "login";
        } else {
            UserRecord user = targetService.findUser(loginForm.getLoginId());
            //System.out.println(user);

            //ログインチェック
            if (user == null || (!loginForm.getPassword().equals(user.password()))) {
                model.addAttribute("message", "ログインIDまたはパスワードが不正です");
                return "login";
            } else {
                session.setAttribute("name", user.name());
                session.setAttribute("loginId", user.loginId());
                return "today";
            }
        }
    }

    //新規登録ページへ
    @GetMapping("/addUser")
    public String addUser(@ModelAttribute("UserForm")UserForm addUserForm) {
        return "user-add";
    }

    @PostMapping("/addUserForm")
    public String insertUser(@Validated @ModelAttribute("UserForm")UserForm addUserForm,
                      @ModelAttribute("LoginForm")LoginForm loginForm,
                      BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "user-add";
        } else {

            //パスワードチェック
            if (!addUserForm.getPassword().equals(addUserForm.getPassword2())) {
                model.addAttribute("message", "パスワードが間違っています");
                return "user-add";
            } else {
                //重複ユーザーIDチェック
                var user = targetService.findUser(addUserForm.getLoginId());
                if (user != null) {
                    model.addAttribute("message", "既に同じログインIDのユーザーが存在します");
                    return "user-add";
                }

                //ユーザー登録
                targetService.insertUser(addUserForm.getName(), addUserForm.getLoginId(), addUserForm.getPassword());
                model.addAttribute("message", "ユーザーを登録しました");
                return "login";
            }
        }
    }

    @GetMapping("/logout")
    public String logout(@ModelAttribute("loginForm") LoginForm loginForm) {
        session.invalidate(); // セッションを無効化する
        return "/logout";
    }

    @GetMapping("/list")
    public String list(@ModelAttribute("LoginForm")LoginForm loginForm,
                       Model model) {
        if (session.getAttribute("name") == null) {
            return "login";
        } else {
            //目標テーブル全件検索
            var list = targetService.allTarget();
            //System.out.println(list);
            model.addAttribute("targets", list);
            return "target-list";
        }
    }

    @GetMapping("/today")
    public String today() {
        return "today";
    }

    @GetMapping("/addTarget")
    public String addTarget(@ModelAttribute("TargetAddForm")TargetForm targetForm) {
        return "target-add";
    }

    @PostMapping("/TargetAddForm")
    public String TargetAddForm(@RequestParam("title") String title,
                                @RequestParam(value = "detail", required = false) List<String> details,
                                @RequestParam(value = "sDate", required = false) List<Integer> sDate,
                                @RequestParam(value = "eDate", required = false) List<Integer> eDate,
                                @RequestParam(value = "week", required = false) List<String> week,
                                Model model) {

//        System.out.println("タイトル：" + title);
//        System.out.println("詳細；");
//        details.forEach(System.out::println);
//        System.out.println("期限；");
//        date.forEach(System.out::println);
//        System.out.println("やる日；");
//        week.forEach(System.out::println);

        //デフォルト値挿入
        if (week == null) {
            week = new ArrayList<>();
            week.add("毎日");
        }
        if (sDate == null) {
            sDate = new ArrayList<>();
            for(int i=0; i<2; i++) {
                sDate.add(null);
            }
        }
        if (eDate == null) {
            eDate = new ArrayList<>();
            for(int i=0; i<2; i++) {
                eDate.add(null);
            }
        }

        //ユーザーIDの割り出し
        var user = targetService.findUser(session.getAttribute("loginId").toString());

        //ターゲット作成
        var target = new TargetRecord(null, user.id(), title, null, null, "f");

        //ターゲット詳細作成
        var detail = new DetailRecord(null, null, details, sDate, eDate, week);

        //ターゲットインサート処理
        targetService.insertTarget(target, detail);

        var list = targetService.allTarget();
        model.addAttribute("targets", list);

        return "target-list";
    }

    @GetMapping("/TargetAddBack")
    public String addBack(Model model) {
        var list = targetService.allTarget();
        model.addAttribute("targets", list);

        return "target-list";
    }

    @GetMapping("detail")
    public String detail(@RequestParam(name="id") Integer targetId, Model model) {
        var target = targetService.findTarget(targetId);
        var detail = targetService.findDetail(targetId);

        System.out.println("目標：" + target);
        System.out.println("詳細：" + detail);

        model.addAttribute("target", target);
        model.addAttribute("detail", detail);

        return "detail";
    }

}
