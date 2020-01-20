package com.leyou.auth.web;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import io.jsonwebtoken.Jwt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private JwtProperties prop;
    @Autowired
    private AuthService authService;

    @Value("${ly.jwt.cookieName}")
    private String cookieName;

    /**
     * 登录授权
     * @param username
     * @param password
     * @return
     */
    @PostMapping("login")
    public ResponseEntity<Void> login(@RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      HttpServletResponse response, HttpServletRequest request){
        // 登录
        String token = authService.login(username, password);
        // 写入cookie
//        CookieUtils.setCookie(, , );
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        //设置域名的cookie
        cookie.setDomain(CookieUtils.getDomainName(request));
        response.addCookie(cookie);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 校验用户登录状态
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN") String token,
                                           HttpServletResponse response, HttpServletRequest request){
        try {
            // 解析token
            UserInfo info = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            // 刷新token，重新生成token
            String newToken = JwtUtils.generateToken(info, prop.getPrivateKey(), prop.getExpire());
            // 写入cookie
//            CookieUtils.setCookie(request,response,cookieName,newToken);
            Cookie cookie = new Cookie(cookieName, newToken);
            cookie.setHttpOnly(true);
            //设置域名的cookie
            cookie.setDomain(CookieUtils.getDomainName(request));
            response.addCookie(cookie);
            // 已登录，返回用户信息
            return ResponseEntity.ok(info);
        }catch ( Exception e){
            // token已过期，或者 token被篡改
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
    }
}
