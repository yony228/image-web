package com.test.demo.controller;

import org.apache.commons.lang3.ObjectUtils;

//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;

import edu.nudt.das.sansiro.core.controller.AbsBaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yony on 17-4-26.
 */
public class BaseController extends AbsBaseController {

//	@Autowired

	protected final String getUsername() {
		String username = null;
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (principal instanceof UserDetails) {
//            username = ((UserDetails) principal).getUsername();
//        } else {
//            username = principal.toString();
//        }
		return username;
	}

	protected final List<String> getAuthority() {
		List<String> authorities = new ArrayList<>();
		return authorities;
	}

	@Override
	public Map getUser(HttpSession session) {
		Map<String, Object> user = null;
		Map<String, Object> userInfo = (Map)  (session.getAttribute("userInfo"));
		if(ObjectUtils.allNotNull(userInfo)) {
			user = (Map<String, Object>) userInfo.get("user");
		}
		return user;
	}

	@Override
	public Object getRole(HttpSession session) {
		Map<String, Object> role = null;
		Map<String, Object> userInfo = (Map)  (session.getAttribute("userInfo"));
		if(ObjectUtils.allNotNull(userInfo)) {
			role = (Map<String, Object>) userInfo.get("userRole");
		}
		return role;
	}

	@Override
	public Object getAuth() {
		return null;
	}


	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}

	public boolean nextPage(int nowPage, int countNum) {
		int maxPage = countNum / 10;
		if (countNum % 10 > 0) {
			maxPage += 1;
		}
		if (maxPage == nowPage) {
			return false;
		}
		return true;
	}
}