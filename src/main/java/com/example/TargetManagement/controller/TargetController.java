package com.example.TargetManagement.controller;

import com.example.TargetManagement.entity.UserRecord;
import com.example.TargetManagement.form.LoginForm;
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

}
