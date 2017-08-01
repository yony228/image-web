package edu.nudt.das.sansiro.core;

import javax.servlet.http.HttpSession;

/**
 * Created by yony on 17-6-9.
 */
public interface IMultiUserAble {
	Object getUser(HttpSession session);
	Object getRole(HttpSession session);
	Object getAuth();
}
