package net.questionbank.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.imageUri}")
    private String imageFileUri;
    @Value("${file.imageDir}")
    private String imageResourcePath;
    @Value("${file.pdfUri}")
    private String pdfFileUri;
    @Value("${file.pdfDir}")
    private String pdfResourcePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(imageFileUri + "**").addResourceLocations("file:///" + imageResourcePath);
        registry.addResourceHandler(pdfFileUri + "**").addResourceLocations("file:///" + pdfResourcePath);
    }
}
