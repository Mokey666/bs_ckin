package com.controller;

import com.common.Const;
import com.common.ServerResponse;
import com.pojo.Group;
import com.pojo.Sign;
import com.pojo.User;
import com.service.ITeacherService;
import com.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher/")
public class TeacherController {
    @Autowired
    ITeacherService iTeacherService;


    /**
     * 0 管理员 1 学生 2 老师
     * @param session
     * @param className
     * @param message
     * @return
     */
    //创建讨论组
    //url ： /teacher/creat_group.do
    //通过老师的id、课程名称className、课程信息message创建一个讨论组，返回一个组号
    @ResponseBody
    @RequestMapping(value = "creat_group.do", method = RequestMethod.POST)
    public ServerResponse<Integer> creatGroup(HttpSession session,String className,String message){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        System.out.println(className);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        if (user.getRole() != 2){
            return ServerResponse.creatByErrorMessage("没有权限");
        }
        return iTeacherService.creatGroup(user.getUid(),className,message);
    }

    //查看所有讨论组
    //url ： /teacher/get_all_group.do
    //根据老师的id，查看创建的所有讨论组。返回一个list的谈论组信息。
    @ResponseBody
    @RequestMapping(value = "get_all_group.do", method = RequestMethod.POST)
    public ServerResponse<List<Group>> getAllgroup(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        if (user.getRole() != 2){
            return ServerResponse.creatByErrorMessage("没有权限");
        }
        return iTeacherService.getAllgroupId(user.getUid());
    }

    //生成签到信息
    //url ： /teacher/publish_sign.do
    //通过老师输入签到信息（讨论组号、位置信息、签到时间）发布签到。
    @ResponseBody
    @RequestMapping(value = "publish_sign.do", method = RequestMethod.POST)
    public ServerResponse<Sign> publishSign(Sign sign, HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        if (user.getRole() != 2){
            return ServerResponse.creatByErrorMessage("没有权限");
        }
        return iTeacherService.publishSign(sign);
    }

    //查看签到记录
    //url ： /teacher/get_check_user.do
    //通过组号来查看
    @ResponseBody
    @RequestMapping(value = "get_check_user.do", method = RequestMethod.POST)
    public ServerResponse<Map<String,Integer>> getCheckUsers(HttpSession session, int groupId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        if (user.getRole() != 2){
            return ServerResponse.creatByErrorMessage("没有权限");
        }
        return iTeacherService.getCheckUsers(groupId);
    }
}
