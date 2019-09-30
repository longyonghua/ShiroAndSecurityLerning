package com.longge.shiro;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author longge
 * @create 2019-09-29 下午8:41
 */
public class HelloWorld {
    private static final transient Logger log = LoggerFactory.getLogger(HelloWorld.class);

    public static void main(String[] args) {
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        //获取当前的Subject SecurityUtils.getSubject()
        Subject currentUser = SecurityUtils.getSubject();

        //测试使用Session Subject.getSession()
        Session session = currentUser.getSession();
        session.setAttribute("key","myValue");
        String value = (String) session.getAttribute("key");
        if(value.equals("myValue")){
            log.info("重新获取了正确的值："+value);
        }

        //测试当前用户是否已经被认证（已登录）Subject.isAuthenticated()
        if(!currentUser.isAuthenticated()){
            //把用户名和密码封装成UsernamePasswordToken对象
            UsernamePasswordToken token = new UsernamePasswordToken("longge2","12345");
            //rememberme
            token.setRememberMe(true);
            try{
                //执行登录 Subject.login(xx)
                currentUser.login(token);
            }catch(UnknownAccountException e){ //用户名不存在时抛出
                log.info("该用户名不存在："+token.getPrincipal());
            }catch(IncorrectCredentialsException e){ //密码不正确时抛出
                log.info("该用户名："+token.getPrincipal()+"的密码不正确");
            }catch(LockedAccountException e){ //用户被锁定时抛出
                log.info("该用户名："+token.getPrincipal()+"被锁定了");
            }
            //.....
            catch(AuthenticationException e){ //所有认证异常的父类

            }
        }
        log.info("用户："+currentUser.getPrincipal()+"登录成功");

        //测试用户是否有某个角色 Subject.hasRole(xx)
        if(currentUser.hasRole("admin")){
            log.info("你有admin角色");
        }else{
            log.info("你没有admin角色");
        }

        //测试用户是否有某个权限 Subject.isPermitted(xx)
        if(currentUser.isPermitted("user:aaa")){
            log.info("you can do it1");
        }else{
            log.info("you can not do1");
        }
        if(currentUser.isPermitted("user:delete:zhangsan")){
            log.info("you can do it2");
        }else{
            log.info("you can not do2");
        }

        System.out.println(currentUser.isAuthenticated());
        //登出
        currentUser.logout();
        System.out.println(currentUser.isAuthenticated());

        System.exit(0);
    }
}
