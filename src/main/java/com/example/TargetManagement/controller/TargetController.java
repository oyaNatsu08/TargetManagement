package com.example.TargetManagement.controller;

import com.example.TargetManagement.entity.*;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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

                var list = targetService.todayTargets();
                model.addAttribute("targets", list);

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
    public String today(Model model) {

        var list = targetService.todayTargets();
        model.addAttribute("targets", list);

        return "today";
    }

    @GetMapping("/addTarget")
    public String addTarget(@ModelAttribute("TargetAddForm")TargetForm targetForm) {
        return "target-add";
    }

    //目標追加処理(旧)
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

        // 現在の年を取得
        int currentYear = LocalDate.now().getYear();

        // 月と日の値を取得
        Integer month1 = sDate.get(0);
        Integer day1 = sDate.get(1);
        Integer month2 = eDate.get(0);
        Integer day2 = eDate.get(1);

        LocalDate date1;
        LocalDate date2;


        if (month1 == null && day1 == null && month2 == null && day2 == null) {
            date1 = LocalDate.now();
            date2 = LocalDate.of(2123, 12, 30);
        } else if (!(month1 == null) && !(day1 == null) && !(month2 == null) && !(day2 == null)) {
            // LocalDateオブジェクトを作成
            date1 = LocalDate.of(currentYear, month1, day1);
            date2 = LocalDate.of(currentYear, month2, day2);
        } else {    //細かい条件(month1==null !day1==nullの場合とか)は今は省く代わり
            date1 = LocalDate.now();
            date2 = LocalDate.of(2123, 12, 30);
        }

        //List型のdetailとweekをString型に変換
        //リストから,区切りで詳細を作成
        String content = String.join(",", details);

        //リストからweekを作成
        String weeks = String.join(",", week);

        //ユーザーIDの割り出し
        var user = targetService.findUser(session.getAttribute("loginId").toString());

        //ターゲット作成
        var target = new TargetRecord(null, user.id(), title, null, null, "f");

        //ターゲット詳細作成
        var detail = new DetailRecord(null, null, content, date1, date2, weeks);

        //ターゲットインサート処理
        targetService.insertTarget(target, detail);

        var list = targetService.allTarget();
        model.addAttribute("targets", list);

        return "target-list";
    }

    @GetMapping("/TargetListBack")
    public String addBack(Model model) {
        var list = targetService.allTarget();
        model.addAttribute("targets", list);

        return "target-list";
    }

    @GetMapping("detail")
    public String detail(@RequestParam(name="id") Integer targetId, Model model) {
        var target = targetService.findTarget(targetId);
        var detail = targetService.findDetail(targetId);

        //detail.forEach(e -> e.content().forEach(System.out::println));

        //取得したcontentをリストに格納
        List<String> contents = new ArrayList<>();
        for (DetailRecord2 d : detail) {
            if (d.content().get(0) == null) {
                d.content().remove(0);
            }
            contents.add(d.content().get(0));
        }

        //取得したcontentのidをリストに格納
        List<Integer> detailsId = new ArrayList<>();
        for (DetailRecord2 di : detail) {
            detailsId.add(di.id());
        }

        //やる日を受け取る
        StringJoiner joiner = new StringJoiner("、");
        if (target.every()) {
            joiner.add("毎日");
        }
        if (target.mon()) {
            joiner.add("月曜");
        }
        if (target.tues()) {
            joiner.add("火曜");
        }
        if (target.wednes()) {
            joiner.add("水曜");
        }
        if (target.thurs()) {
            joiner.add("木曜");
        }
        if (target.fri()) {
            joiner.add("金曜");
        }
        if (target.satur()) {
            joiner.add("土曜");
        }
        if (target.sun()) {
            joiner.add("日曜");
        }

        model.addAttribute("target", target);
        model.addAttribute("target", target);
        model.addAttribute("contents", contents);
        model.addAttribute("detailsId", detailsId);
        model.addAttribute("date", target.startTerm() + "～" + target.endTerm());
        model.addAttribute("week", joiner.toString());
        model.addAttribute("id", target.id());

        return "detail";
    }

    //目標追加処理(新)
    @PostMapping("/TargetAddForm2")
    public String TargetAddForm2(@RequestParam("title") String title,
                                 @RequestParam(value = "content", required = false) List<String> contents,
                                 @RequestParam(value = "sDate", required = false) List<Integer> sDate,
                                 @RequestParam(value = "eDate", required = false) List<Integer> eDate,
                                 @RequestParam(value = "week", required = false) List<String> week,
                                 Model model) {

        //やる日を作成
        if (week == null) {
            week = new ArrayList<>();
            week.add("every");
        }
        Week weeks = new Week(week);

        //ユーザーIDの割り出し
        var user = targetService.findUser(session.getAttribute("loginId").toString());

        //日付を入力しなかった場合(今回は一部入力しないなどは考えない)
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

        // 現在の年を取得
        int currentYear = LocalDate.now().getYear();

        // 月と日の値を取得
        Integer month1 = sDate.get(0);
        Integer day1 = sDate.get(1);
        Integer month2 = eDate.get(0);
        Integer day2 = eDate.get(1);

        LocalDate date1;
        LocalDate date2;


        if (month1 == null && day1 == null && month2 == null && day2 == null) {
            date1 = LocalDate.now();
            date2 = LocalDate.of(2123, 12, 30);
        } else if (!(month1 == null) && !(day1 == null) && !(month2 == null) && !(day2 == null)) {
            // LocalDateオブジェクトを作成
            date1 = LocalDate.of(currentYear, month1, day1);
            date2 = LocalDate.of(currentYear, month2, day2);
        } else {    //細かい条件(month1==null !day1==nullの場合とか)は今は省く代わり
            date1 = LocalDate.now();
            date2 = LocalDate.of(2123, 12, 30);
        }

        //ターゲット作成
        TargetRecord2 targetRecord = new TargetRecord2(null, user.id(), title, null, null,
                                                        false, date1, date2, weeks.getEvery(), weeks.getMon(),
                                                        weeks.getTues(), weeks.getWednes(), weeks.getThurs(), weeks.getFri(),
                                                        weeks.getSatur(), weeks.getSun());

        //ターゲット詳細作成
        DetailRecord2 detailRecord = new DetailRecord2(null, null, contents);

        //インサート処理
        targetService.insertTarget2(targetRecord, detailRecord);

        var list = targetService.allTarget();

        model.addAttribute("targets", list);

        return "target-list";
    }

    @GetMapping("/update")
    public String updateView(@RequestParam("id") Integer targetId,
                             @RequestParam("detailId") List<Integer> detailsId,
                             Model model) {
        var target = targetService.findTarget(targetId);
        var detail = targetService.findDetail(targetId);

        //取得したcontentをリストに格納
        List<String> contents = new ArrayList<>();
        for (DetailRecord2 d : detail) {
            contents.add(d.content().get(0));
        }

        //日付をそれぞれ取得
        int month1 = target.startTerm().getMonthValue();
        int day1 = target.startTerm().getDayOfMonth();
        int month2 = target.endTerm().getMonthValue();
        int day2 = target.endTerm().getDayOfMonth();

        model.addAttribute("target", target);
        model.addAttribute("contents", contents);
        model.addAttribute("detailsId", detailsId);
        model.addAttribute("month1", month1);
        model.addAttribute("day1", day1);
        model.addAttribute("month2", month2);
        model.addAttribute("day2", day2);
        model.addAttribute("detail", detail);

        return "update";
    }

    @PostMapping("/TargetUpdate")
    public String update(@RequestParam("id") Integer id,
                         @RequestParam("detailId") List<Integer> detailsId,
                         @RequestParam("title") String title,
                         @RequestParam(value = "content", required = false) List<String> contents,
                         @RequestParam(value = "sDate", required = false) List<Integer> sDate,
                         @RequestParam(value = "eDate", required = false) List<Integer> eDate,
                         @RequestParam(value = "week", required = false) List<String> week,
                         Model model) {

        //やる日を作成
        if (week == null) {
            week = new ArrayList<>();
            week.add("every");
        }
        Week weeks = new Week(week);

        //ユーザーIDの割り出し
        var user = targetService.findUser(session.getAttribute("loginId").toString());

        //日付を入力しなかった場合(今回は一部入力しないなどは考えない)
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

        // 現在の年を取得
        int currentYear = LocalDate.now().getYear();

        // 月と日の値を取得
        Integer month1 = sDate.get(0);
        Integer day1 = sDate.get(1);
        Integer month2 = eDate.get(0);
        Integer day2 = eDate.get(1);

        LocalDate date1;
        LocalDate date2;


        if (month1 == null && day1 == null && month2 == null && day2 == null) {
            date1 = LocalDate.now();
            date2 = LocalDate.of(2123, 12, 31);
        } else if (!(month1 == null) && !(day1 == null) && !(month2 == null) && !(day2 == null)) {
            // LocalDateオブジェクトを作成
            date1 = LocalDate.of(currentYear, month1, day1);
            date2 = LocalDate.of(currentYear, month2, day2);
        } else {    //細かい条件(month1==null !day1==nullの場合とか)は今は省く代わり
            date1 = LocalDate.now();
            date2 = LocalDate.of(2123, 12, 31);
        }

        //ターゲット作成
        TargetRecord2 targetRecord = new TargetRecord2(id, user.id(), title, null, null,
                false, date1, date2, weeks.getEvery(), weeks.getMon(),
                weeks.getTues(), weeks.getWednes(), weeks.getThurs(), weeks.getFri(),
                weeks.getSatur(), weeks.getSun());

        //ターゲット詳細作成
        DetailRecord2 detailRecord = new DetailRecord2(null, id, contents);

        //更新処理
        targetService.update(targetRecord, detailRecord, detailsId);

        var list = targetService.allTarget();
        model.addAttribute("targets", list);

        return "target-list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Integer targetId, Model model) {

        //削除処理
        targetService.delete(targetId);

        var list = targetService.allTarget();
        model.addAttribute("targets", list);

        return "target-list";

    }

}
