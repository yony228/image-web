package com.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by Administrator on 2017/5/16.
 */
//@EnableDiscoveryClient
//@EnableAdminServer
@EnableTransactionManagement
@SpringBootApplication
@ComponentScan(basePackages = {"com.web.image","edu.nudt.das.sansiro.login","edu.nudt.das.sansiro.core"})
public class ImageWebApplication {
        public static void main(String[] args) {
                SpringApplication.run(ImageWebApplication.class, args);
        }

//        @Bean(name = "sessionFilter")
//        public Filter sessionFilter() {
//                return new SessionFilter();
//        }

//        @Bean
//        public FilterRegistrationBean filter() {
//                FilterRegistrationBean registration = new FilterRegistrationBean();
//                registration.setFilter(sessionFilter());
//                registration.addUrlPatterns("/*");
//                registration.setName("sessionFilter");
//                return registration;
//        }
}