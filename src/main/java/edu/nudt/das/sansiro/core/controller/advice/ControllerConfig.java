package edu.nudt.das.sansiro.core.controller.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

/**
 * email: yony228@163.com
 * Created by yony on 17-6-13.
 */
@ControllerAdvice
public class ControllerConfig {

        @Autowired
        ResourceUrlProvider resourceUrlProvider;

        @ModelAttribute("urls")
        public ResourceUrlProvider urls() {
                return this.resourceUrlProvider;
        }
}