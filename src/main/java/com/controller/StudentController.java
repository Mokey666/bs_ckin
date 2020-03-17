package com.controller;

import com.common.ServerResponse;
import com.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student/")
public class StudentController {

    public ServerResponse<User> ClassCheckIn(String userId, String classId){
        return null;
    }
}
