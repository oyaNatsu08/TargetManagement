package com.example.TargetManagement.dao;

import com.example.TargetManagement.entity.DetailRecord;
import com.example.TargetManagement.entity.TargetRecord;
import com.example.TargetManagement.entity.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class TargetDao implements ManagementDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

//    @Override
//    public UserRecord loginCheck(String loginId, String password) {
//        MapSqlParameterSource param = new MapSqlParameterSource();
//        param.addValue("id", loginId);
//        param.addValue("pass", password);
//        var list = jdbcTemplate.query("SELECT id, name, login_id, password FROM users WHERE login_id = :id AND password = :pass",
//                param, new DataClassRowMapper<>(UserRecord.class));
//        return list.isEmpty() ? null : list.get(0);
//    }

    @Override
    public int insertUser(String name, String loginId, String password) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("name", name);
        param.addValue("id", loginId);
        param.addValue("pass", password);
        return jdbcTemplate.update("INSERT INTO users(name, login_id, password) VALUES(:name, :id, :pass)", param);
    }

    @Override
    public UserRecord findUser(String loginId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("id", loginId);
        var list = jdbcTemplate.query("SELECT id, name, login_id, password FROM users WHERE login_id = :id",
                param, new DataClassRowMapper<>(UserRecord.class));
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<TargetRecord> allTarget() {
        var list = jdbcTemplate.query("SELECT id, user_id, title, created_date, shared_url, achivement_flag FROM targets",
                new DataClassRowMapper<>(TargetRecord.class));
        return list;
    }

    @Override
    public int insertTarget(TargetRecord targetRecord, DetailRecord detailRecord) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userId", targetRecord.user_id());
        param.addValue("title", targetRecord.title());
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update("INSERT INTO targets(user_id, title, created_date, achivement_flag) " +
                "VALUES(:userId, :title, now(), 'f')", param, keyHolder);

        //System.out.println(keyHolder.getKeyList());
        Integer generatedId = Integer.parseInt(keyHolder.getKeys().get("id").toString());

        var target = findTarget(generatedId);

        param.addValue("targetId", target.id());

        //リストから,区切りで詳細を作成
        String content = String.join(",", detailRecord.content());
        param.addValue("content", content);

        //現在の年を取得し、DATE型のフォーマットで年月日を作成
//        int currentYear = LocalDate.now().getYear();
//        String date1 = currentYear + "-" + detailRecord.date().get(0) + "-" + detailRecord.date().get(1);
//        String date2 = currentYear + "-" + detailRecord.date().get(2) + "-" + detailRecord.date().get(3);

        // 現在の年を取得
        int currentYear = LocalDate.now().getYear();

        System.out.println(detailRecord);

        // 月と日の値を取得
        Integer month1 = detailRecord.startTerm().get(0);
        Integer day1 = detailRecord.startTerm().get(1);
        Integer month2 = detailRecord.endTerm().get(0);
        Integer day2 = detailRecord.endTerm().get(1);

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

        param.addValue("sDate", date1);
        param.addValue("eDate", date2);

        //リストからweekを作成
        String week = String.join(",", detailRecord.week());
        param.addValue("week", week);

        //detailsテーブルにinsert
        return jdbcTemplate.update("INSERT INTO details(target_id, content, start_term, end_term, week) " +
                "VALUES(:targetId, :content, :sDate, :eDate, :week)", param);

    }

    @Override
    public TargetRecord findTarget(Integer targetId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("targetId", targetId);

        var list = jdbcTemplate.query("SELECT id, user_id, title, created_date, shared_url, achivement_flag FROM targets " +
                "WHERE id = :targetId", param, new DataClassRowMapper<>(TargetRecord.class));

        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<DetailRecord> findDetail(Integer targetId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("targetId", targetId);

        var list = jdbcTemplate.query("SELECT id, target_id, content, start_term, end_term, week FROM details " +
                "WHERE target_id = :targetId", param, new DataClassRowMapper<>(DetailRecord.class));

        return list;
    }

}
