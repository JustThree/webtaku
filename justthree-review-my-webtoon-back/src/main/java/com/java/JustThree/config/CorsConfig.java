package com.java.JustThree.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        System.out.println("CorsFilter 실행");
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://192.168.3.112:5173");//L3 시연용 컴퓨터 성수IP
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://192.168.0.85:5173");// L2 현덕 IP
        config.addAllowedHeader("*"); // 모든 header에 응답 허용
        config.addAllowedMethod("*"); // 모든 post, get, put, delete, patch 등 요청 허용
        config.addExposedHeader("Authorization"); // header에 Authorization 추가

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 위 CORS 설정 적용

        return new CorsFilter(source);
    }
}
