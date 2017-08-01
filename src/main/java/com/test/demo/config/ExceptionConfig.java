package com.test.demo.config;

import edu.nudt.das.sansiro.exception.controller.MyBasicErrorController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * email: yony228@163.com
 * Created by yony on 17-6-14.
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties(ResourceProperties.class)
public class ExceptionConfig {

//        @Bean
//        public ErrorViewResolver get(){
//                return new ErrorViewResolver() {
//                        @Override
//                        public ModelAndView resolveErrorView(HttpServletRequest httpServletRequest, HttpStatus httpStatus, Map<String, Object> map) {
//                                return null;
//                        }
//                };
//        }

        @Autowired(required = false)
        private List<ErrorViewResolver> errorViewResolvers;
        private final ServerProperties serverProperties;

        public ExceptionConfig(
                ServerProperties serverProperties) {
                this.serverProperties = serverProperties;
        }

        @Bean
        public MyBasicErrorController basicErrorController(ErrorAttributes errorAttributes) {
                return new MyBasicErrorController(errorAttributes, this.serverProperties.getError(),
                        this.errorViewResolvers);
        }


}
