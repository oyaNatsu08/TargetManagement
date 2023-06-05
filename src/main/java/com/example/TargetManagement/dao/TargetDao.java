package com.example.TargetManagement.dao;

import com.example.TargetManagement.entity.TargetRecord;
import com.example.TargetManagement.entity.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
        var list = jdbcTemplate.query("SELECT id, user_id, title, created_date, shared_url FROM targets",
                new DataClassRowMapper<>(TargetRecord.class));
        return list;
    }

}
