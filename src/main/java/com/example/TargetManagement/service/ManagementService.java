package com.example.TargetManagement.service;

import com.example.TargetManagement.entity.*;

import java.util.List;

public interface ManagementService {

    //UserRecord loginCheck(String loginId, String password);

    int insertUser(String name, String loginId, String password);

    UserRecord findUser(String loginId);

    List<TargetRecord2> allTarget();

    int insertTarget(TargetRecord targetRecord, DetailRecord detailRecord);

    TargetRecord2 findTarget(Integer targetId);

    List<DetailRecord2> findDetail(Integer targetId);

    int insertTarget2(TargetRecord2 targetRecord, DetailRecord2 detailRecord);

    int update(TargetRecord2 targetRecord, DetailRecord2 detailRecord, List<Integer> detailsId);

    int delete(Integer targetId);

    List<TargetRecord2> todayTargets();

    int complete(List<Integer> detailsId);

    boolean completeCheck(List<Integer> detailsId);

}
