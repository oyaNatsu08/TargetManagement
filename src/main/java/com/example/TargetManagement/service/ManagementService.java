package com.example.TargetManagement.service;

import com.example.TargetManagement.entity.TargetRecord;
import com.example.TargetManagement.entity.UserRecord;

import java.util.List;

public interface ManagementService {

    //UserRecord loginCheck(String loginId, String password);

    int insertUser(String name, String loginId, String password);

    UserRecord findUser(String loginId);

    List<TargetRecord> allTarget();

}
