package com.example.TargetManagement.service;

import com.example.TargetManagement.dao.TargetDao;
import com.example.TargetManagement.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TargetManagementService implements ManagementService {

    @Autowired
    private TargetDao targetDao;

//    @Override
//    public UserRecord loginCheck(String loginId, String password) {
//        return targetDao.loginCheck(loginId, password);
//    }

    @Override
    public int insertUser(String name, String loginId, String password) {
        return targetDao.insertUser(name, loginId, password);
    }

    @Override
    public UserRecord findUser(String loginId) {
        return targetDao.findUser(loginId);
    }

    @Override
    public List<TargetRecord2> allTarget() {
        return targetDao.allTarget();
    }

    @Override
    public int insertTarget(TargetRecord targetRecord, DetailRecord detailRecord) {
        return targetDao.insertTarget(targetRecord, detailRecord);
    }

    @Override
    public TargetRecord2 findTarget(Integer targetId) {
        return targetDao.findTarget(targetId);
    }

    @Override
    public List<DetailRecord2> findDetail(Integer targetId) {
        return targetDao.findDetail(targetId);
    }

    @Override
    public int insertTarget2(TargetRecord2 targetRecord, DetailRecord2 detailRecord) {
        return targetDao.insertTarget2(targetRecord, detailRecord);
    }

    @Override
    public int update(TargetRecord2 targetRecord, DetailRecord2 detailRecord, List<Integer> detailsId) {
        return targetDao.update(targetRecord, detailRecord, detailsId);
    }

    @Override
    public int delete(Integer targetId) {
        return targetDao.delete(targetId);
    }

    @Override
    public List<TargetRecord2> todayTargets() {
        return targetDao.todayTargets();
    }

}
