package edu.nudt.das.sansiro.core.service;

import edu.nudt.das.sansiro.core.ILoggerAble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yony on 17-6-8.
 */
public class AbsBaseService implements ILoggerAble{

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}
}
