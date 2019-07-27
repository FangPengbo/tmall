package com.fang.controller.fore;

import com.alibaba.fastjson.JSONObject;
import com.fang.controller.BaseController;
import com.fang.entity.User;
import com.fang.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用户登录Controller
 *              方鹏博
 */
@Controller
public class ForeLoginController extends BaseController {


    @Resource(name = "userService")
    private UserService userService;

    //退出当前账号
    @RequestMapping(value = "login/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        Object o = session.getAttribute("userId");
        if (o != null) {
            logger.info("获取安全管理器");
            Subject subject = SecurityUtils.getSubject();
            logger.info("logout");
            subject.logout();
            logger.info("登录信息已清除，返回用户登录页");
        }
        return "redirect:/login";
    }


    //转到前台天猫-登录页
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String goToPage(HttpSession session, Map<String, Object> map) {
        logger.info("转到前台天猫-登录页");
        return "fore/loginPage";
    }

    /**
     * 模态弹窗登录-ajax
     * @param session
     * @param username 账号
     * @param password  密码
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "login/doLogin" ,method = {RequestMethod.POST},produces = "application/json;charset=utf-8")
    public String checkLogin(HttpSession session, @RequestParam String username,@RequestParam String password){
        logger.info("用户验证登录");
        JSONObject jsonObject=new JSONObject();
        logger.info("获取安全管理器");
        Subject subject = SecurityUtils.getSubject();
        logger.info("判断subject是否认证");
        if (!subject.isAuthenticated()){
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);//实例化一个token
            try {
                subject.login(token);
                logger.info("登录验证成功,用户ID传入session");
                User user = userService.getByName(username);
                session.setAttribute("userId",user.getUser_id());
                jsonObject.put("success",true);
            } catch (AuthenticationException e) {
                System.out.println("认证失败:"+e.getMessage());
                logger.info("登录验证失败");
                jsonObject.put("success",false);
            }
        }
        return jsonObject.toJSONString();
    }


}
