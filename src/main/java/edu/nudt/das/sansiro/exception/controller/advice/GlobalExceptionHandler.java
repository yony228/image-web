package edu.nudt.das.sansiro.exception.controller.advice;

import edu.nudt.das.sansiro.exception.define.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * email: yony228@163.com
 * Created by yony on 17-6-14.
 */
//@ControllerAdvice
public class GlobalExceptionHandler {

        private static final String DEFAULT_ERROR_VIEW = "error123";

        @ExceptionHandler(value = CustomException.class)
        @ResponseBody
        public ResponseEntity defaultErrorHandler(HttpServletRequest req, CustomException e) throws Exception {
                return ResponseEntity.ok("ok");
        }

        @ExceptionHandler(value = Exception.class)
        public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
                ModelAndView mav = new ModelAndView();
                mav.addObject("exception", e);
                mav.addObject("url", req.getRequestURL());
                mav.setViewName(DEFAULT_ERROR_VIEW);
                return mav;
        }
}