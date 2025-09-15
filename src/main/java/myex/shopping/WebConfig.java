package myex.shopping;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // "/img/**" 요청 ==> "uploads/" 폴더
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:../UploadFolder/");

    }
}
