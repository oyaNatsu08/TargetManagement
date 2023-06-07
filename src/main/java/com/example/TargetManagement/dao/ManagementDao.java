package com.example.TargetManagement.dao;

import com.example.TargetManagement.entity.*;

import java.util.List;

public interface ManagementDao {

    //UserRecord loginCheck(String loginId, String password);

    int insertUser(String name, String loginId, String password);

    UserRecord findUser(String loginId);

    List<TargetRecord> allTarget();

    int insertTarget(TargetRecord targetRecord, DetailRecord detailRecord);

    TargetRecord2 findTarget(Integer targetId);

    List<DetailRecord2> findDetail(Integer targetId);

    int insertTarget2(TargetRecord2 targetRecord, DetailRecord2 detailRecord);

    int update(TargetRecord2 targetRecord, DetailRecord2 detailRecord, List<Integer> detailsId);

    int delete(Integer targetId);

}
