package com.web.image.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import edu.nudt.das.sansiro.login.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;

/**
 * Created by yony on 17-4-6.
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

        @Autowired
        private LoginInterceptor loginInterceptor;


        @Bean
        public FastJsonHttpMessageConverter4 fastJsonHttpMessageConverter() {
                FastJsonHttpMessageConverter4 fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter4();
                fastJsonHttpMessageConverter.setSupportedMediaTypes(new ArrayList<MediaType>() {
                        {
                                add(MediaType.TEXT_HTML);
                                add(MediaType.APPLICATION_JSON_UTF8);
                        }
                });
                return fastJsonHttpMessageConverter;
        }

        @Bean
        public RestTemplate restTemplate() {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.setMessageConverters(new ArrayList<HttpMessageConverter<?>>() {
                        {
                                add(new org.springframework.http.converter.FormHttpMessageConverter());
                                add(fastJsonHttpMessageConverter());
                        }
                });
                return restTemplate;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
                // 注册监控拦截器
                registry.addInterceptor(loginInterceptor)
                        .addPathPatterns("/**")
                        .excludePathPatterns("/", "/fileSearch/*");

        }

}