package com.example.TargetManagement.service;

import com.example.TargetManagement.dao.TargetDao;
import com.example.TargetManagement.entity.DetailRecord;
import com.example.TargetManagement.entity.TargetRecord;
import com.example.TargetManagement.entity.UserRecord;
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
    public List<TargetRecord> allTarget() {
        return targetDao.allTarget();
    }

    @Override
    public int insertTarget(TargetRecord targetRecord, DetailRecord detailRecord) {
        return targetDao.insertTarget(targetRecord, detailRecord);
    }

    @Override
    public TargetRecord findTarget(Integer targetId) {
        return targetDao.findTarget(targetId);
    }

    @Override
    public List<DetailRecord> findDetail(Integer targetId) {
        return targetDao.findDetail(targetId);
    }

}
