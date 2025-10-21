package com.cnpm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Lấy đường dẫn tuyệt đối đến thư mục 'uploads'
        Path uploadDir = Paths.get("./uploads/");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // Cấu hình resource handler
        // Mọi request có dạng /uploads/** sẽ được map đến thư mục uploads trên ổ đĩa
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}