package com.controller;

import com.common.BaiduFaceAPI;
import com.common.ServerResponse;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestMapping("/face/")
public class FaceController {
    public ServerResponse<String> identifyUser(HttpServletRequest request, HttpServletResponse response){


        //

        return null;
    }
}
