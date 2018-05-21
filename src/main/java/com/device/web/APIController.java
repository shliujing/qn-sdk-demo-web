package com.device.web;

import com.device.common.Const;
import com.device.common.enums.UserTypeEnum;
import com.device.entity.BaseResult;
import com.device.entity.User;
import com.device.entity.vo.AccessTokenVoIn;
import com.device.entity.vo.AccessTokenVoOut;
import com.device.service.UserService;
import com.qiniu.util.Auth;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 数据处理
 */
@RequestMapping("api")
@Controller
public class APIController {
    @Resource
    UserService userService;


    @RequestMapping("/index")
    public String list(HttpServletRequest request, @ModelAttribute("errorMsg") String errorMsg, Model model) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !UserTypeEnum.isAdmin(user.getUserTypeName()) ) {
            return "redirect:/";
        }

        List<User> users = userService.getUserList();
        model.addAttribute("users", users);
        model.addAttribute("errorMsg", errorMsg);
        return "api/index";
    }

    @RequestMapping("/token")
    @ResponseBody
    public BaseResult<AccessTokenVoOut> token(AccessTokenVoIn tokenVoIn) {
        AccessTokenVoOut result = new AccessTokenVoOut();
        //设置好账号的ACCESS_KEY和SECRET_KEY
        String ACCESS_KEY = tokenVoIn.getAk();
        String SECRET_KEY = tokenVoIn.getSk();
        String url = tokenVoIn.getUrl();

        //密钥配置
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

        result.setToken(auth.sign(url));
        return new BaseResult<>(result, true);
    }
}
