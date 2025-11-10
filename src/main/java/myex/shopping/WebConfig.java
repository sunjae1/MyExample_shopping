package myex.shopping;

import lombok.RequiredArgsConstructor;
import myex.shopping.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // "/img/**" 요청 ==> "uploads/" 폴더
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:../UploadFolder/");

                 registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }
    
    //인터셉터 등록
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**") //모든 요청에 적용
                .addPathPatterns("/posts/new")
                .excludePathPatterns(
                        "/", //로그인 페이지
                        "/register", //회원가입 페이지
                        "/main", //메인 쇼핑 페이지
                        "/login", //로그인 GET & POST 모두 제외
                        "/logout",//로그아웃
                        "/*.css",
                        "/*.js",
                        "/image/**",
                        "/api/**",
                        "/posts",
//                        "/*.ico",
                        "/error"
                );


    }
}
