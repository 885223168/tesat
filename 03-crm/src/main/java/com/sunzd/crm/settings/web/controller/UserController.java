package com.sunzd.crm.settings.web.controller;

import com.sunzd.crm.commons.contants.Contants;
import com.sunzd.crm.commons.domain.ReturnObject;
import com.sunzd.crm.commons.utils.DateUtils;
import com.sunzd.crm.commons.utils.MD5Util;
import com.sunzd.crm.settings.domain.User;
import com.sunzd.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * SZD
 * 2020/12/23
 * 16:10
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin(HttpServletRequest request){

        Cookie[] cookies = request.getCookies();
        String loginAct = null;
        String loginPwd = null;

        if (cookies != null){
            for (Cookie cookie:cookies) {
                String name = cookie.getName();
                if ("loginAct".equals(name)){
                    loginAct = cookie.getValue();
                    continue;
                }
                if ("loginPwd".equals(name)){
                    loginPwd = cookie.getValue();
                }
            }
        }
        if (loginAct != null && loginPwd != null){
            Map<String,Object> map = new HashMap<>();
            map.put("loginAct", loginAct);
            map.put("loginPwd", MD5Util.getMD5(loginPwd));
            User user = userService.queryUserByLoginActAndPwd(map);
            request.getSession().setAttribute(Contants.SESSION_USER, user);
            return "redirect:/workbench/index.do";
        }
        return "settings/qx/user/login";
    }

    @RequestMapping("/settings/qx/user/login.do")
    @ResponseBody
    public Object login(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request, HttpServletResponse response, HttpSession session){

        ReturnObject returnObject = new ReturnObject();
        Map<String,Object> map = new HashMap<>();

        map.put("loginAct", loginAct);
        map.put("loginPwd", MD5Util.getMD5(loginPwd));

        User user = userService.queryUserByLoginActAndPwd(map);

        if (user == null){
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("用户名或密码错误");
        }else {
            if ("0".equals(user.getLockState())){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("用户已锁定");
            }else if (DateUtils.formatDateTime(new Date()).compareTo(user.getExpireTime())>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("用户已过期");
            }else if (!user.getAllowIps().contains(request.getRemoteAddr())){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("IP受限");
            }else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
                session.setAttribute(Contants.SESSION_USER, user);

                if ("true".equals(isRemPwd)){
                    Cookie cookie1 = new Cookie("loginAct", loginAct);
                    cookie1.setMaxAge(10*24*60*60);
                    response.addCookie(cookie1);

                    Cookie cookie2 = new Cookie("loginPwd", loginPwd);
                    cookie2.setMaxAge(10*24*60*60);
                    response.addCookie(cookie2);

                }else {
                    Cookie cookie1 = new Cookie("loginAct", null);
                    cookie1.setMaxAge(0);
                    response.addCookie(cookie1);

                    Cookie cookie2 = new Cookie("loginPwd", null);
                    cookie2.setMaxAge(0);
                    response.addCookie(cookie2);
                }
            }
        }
        return returnObject;
    }

    @RequestMapping("/settings/qx/user/logout.do")
    public String logout(HttpServletResponse response,HttpSession session){

        Cookie cookie1 = new Cookie("loginAct", null);
        cookie1.setMaxAge(0);
        response.addCookie(cookie1);

        Cookie cookie2 = new Cookie("loginPwd", null);
        cookie2.setMaxAge(0);
        response.addCookie(cookie2);

        session.invalidate();

        return "redirect:/";
    }
}
