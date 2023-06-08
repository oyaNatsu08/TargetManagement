package com.example.TargetManagement.dao;

import com.example.TargetManagement.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public List<TargetRecord2> allTarget() {
        var list = jdbcTemplate.query("SELECT id, user_id, title, created_date, shared_url, achivement_flag, " +
                        "start_term, end_term, every, mon, tues, wednes, thurs, fri, satur, sun FROM targets2",
                new DataClassRowMapper<>(TargetRecord2.class));
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

//        //リストから,区切りで詳細を作成
//        String content = String.join(",", detailRecord.content());
        param.addValue("content", detailRecord.content());


        param.addValue("sDate", detailRecord.startTerm());
        param.addValue("eDate", detailRecord.endTerm());

//        //リストからweekを作成
//        String week = String.join(",", detailRecord.week());
        param.addValue("week", detailRecord.week());

        //detailsテーブルにinsert
        return jdbcTemplate.update("INSERT INTO details(target_id, content, start_term, end_term, week) " +
                "VALUES(:targetId, :content, :sDate, :eDate, :week)", param);

    }

    @Override
    public TargetRecord2 findTarget(Integer targetId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("targetId", targetId);

        var list = jdbcTemplate.query("SELECT id, user_id, title, created_date, shared_url, achivement_flag, " +
                "start_term, end_term, every, mon, tues, wednes, thurs, fri, satur, sun FROM targets2 " +
                "WHERE id = :targetId", param, new DataClassRowMapper<>(TargetRecord2.class));

        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<DetailRecord2> findDetail(Integer targetId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("targetId", targetId);

        var list = jdbcTemplate.query("SELECT id, target_id, content FROM details2 " +
                "WHERE target_id = :targetId", param, new DataClassRowMapper<>(DetailRecord2.class));

        return list;
    }

    @Override
    public int insertTarget2(TargetRecord2 targetRecord, DetailRecord2 detailRecord) {
        MapSqlParameterSource param = new MapSqlParameterSource();

        //targets2テーブルにインサート処理
        param.addValue("userId", targetRecord.user_id());
        param.addValue("title", targetRecord.title());
        param.addValue("sTerm", targetRecord.startTerm());
        param.addValue("eTerm", targetRecord.endTerm());
        param.addValue("every", targetRecord.every());
        param.addValue("mon", targetRecord.mon());
        param.addValue("tues", targetRecord.tues());
        param.addValue("wednes", targetRecord.wednes());
        param.addValue("thurs", targetRecord.thurs());
        param.addValue("fri", targetRecord.fri());
        param.addValue("satur", targetRecord.satur());
        param.addValue("sun", targetRecord.sun());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update("INSERT INTO targets2(user_id, title, created_date, achivement_flag, start_term, end_term, " +
                "every, mon, tues, wednes, thurs, fri, satur, sun) VALUES(:userId, :title, now(), 'f', :sTerm, :eTerm, " +
                ":every, :mon, :tues, :wednes, :thurs, :fri, :satur, :sun)", param, keyHolder);

        //target2テーブルにインサート処理したidを取得
        Integer generatedId = Integer.parseInt(keyHolder.getKeys().get("id").toString());


        //details2テーブルにcontentの数だけインサート処理
        param.addValue("targetId", generatedId);

        for (String content : detailRecord.content()) {
            param.addValue("content", content);
            jdbcTemplate.update("INSERT INTO details2(target_id, content) VALUES(:targetId, :content)", param);
        }

        return 1;
    }

    public int update(TargetRecord2 targetRecord, DetailRecord2 detailRecord, List<Integer> detailsId) {

        System.out.println(targetRecord);
        System.out.println(detailRecord);
        System.out.println(detailsId);

        MapSqlParameterSource param = new MapSqlParameterSource();

        //targets2テーブルにインサート処理
        param.addValue("id", targetRecord.id());
        param.addValue("title", targetRecord.title());
        param.addValue("sTerm", targetRecord.startTerm());
        param.addValue("eTerm", targetRecord.endTerm());
        param.addValue("every", targetRecord.every());
        param.addValue("mon", targetRecord.mon());
        param.addValue("tues", targetRecord.tues());
        param.addValue("wednes", targetRecord.wednes());
        param.addValue("thurs", targetRecord.thurs());
        param.addValue("fri", targetRecord.fri());
        param.addValue("satur", targetRecord.satur());
        param.addValue("sun", targetRecord.sun());

        jdbcTemplate.update("UPDATE targets2 SET title = :title, start_term = :sTerm, end_term = :eTerm, " +
                "every = :every, mon = :mon, tues = :tues, wednes = :wednes, thurs = :thurs, fri = :fri, " +
                "satur = :satur, sun = :sun WHERE id = :id", param);

        if (detailsId != null) {
            //更新前からある分の詳細内容
            for (int i = 0; i < detailsId.size(); i++) {
                param.addValue("content", detailRecord.content().get(i));
                param.addValue("detailId", detailsId.get(i));

                //更新前に入力したものを消したなら
                if (detailRecord.content().get(i).equals("")) {
                    jdbcTemplate.update("DELETE FROM details2 WHERE id = :detailId", param);
                } else {
                    jdbcTemplate.update("UPDATE details2 SET content = :content WHERE id = :detailId", param);
                }

            }

//        System.out.println(detailsId.size());
//        System.out.println(detailRecord.content().size());
            //新しく追加された詳細内容
            for (int j = detailsId.size(); j < detailRecord.content().size(); j++) {
                param.addValue("targetId", targetRecord.id());
                param.addValue("content", detailRecord.content().get(j));
                jdbcTemplate.update("INSERT INTO details2(target_id, content) VALUES(:targetId, :content)", param);
            }

        }

        return 1;

    }

    @Override
    public int delete(Integer targetId) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("id", targetId);

        jdbcTemplate.update("DELETE FROM details2 WHERE target_id = :id", param);
        jdbcTemplate.update("DELETE FROM targets2 WHERE id = :id", param);

        return 1;

    }

    @Override
    public List<TargetRecord2> todayTargets() {

        var list = jdbcTemplate.query("SELECT id, user_id, title, created_date, shared_url, achivement_flag, " +
                "start_term, end_term, every, mon, tues, wednes, thurs, fri, satur, sun FROM targets2 " +
                "WHERE (now() BETWEEN start_term AND end_term) AND " +
                "(CASE date_part('dow', current_date)" +
                "WHEN 0 THEN sun WHEN 1 THEN mon WHEN 2 THEN tues WHEN 3 THEN wednes WHEN 4 THEN thurs " +
                "WHEN 5 THEN fri WHEN 6 THEN satur " +
                "END OR every)", new DataClassRowMapper<>(TargetRecord2.class));

        //System.out.println("今日やること：" + list);

        return list;
    }

    @Override
    public int complete(List<Integer> detailsId) {
        MapSqlParameterSource param = new MapSqlParameterSource();

        for (Integer detailId : detailsId) {

            param.addValue("id", detailId);
            jdbcTemplate.update("INSERT INTO completes(detail_id, flag, complete_date) VALUES(:id, 't', now())", param);

        }

        return 1;

    }

    @Override
    public boolean completeCheck(List<Integer> detailsId) {
        MapSqlParameterSource param = new MapSqlParameterSource();

        for (Integer detailId : detailsId) {
            param.addValue("detailId", detailId);
           // var list = jdbcTemplate.query("SELECT true FROM completes WHERE ")
        }
return true;
    }

}
