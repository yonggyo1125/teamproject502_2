package com.jmt.global.configs;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableJpaAuditing
@EnableDiscoveryClient // 유레카에서 해당 인스턴스 주소 찾는 역할
public class MvcConfig implements WebMvcConfigurer {
}