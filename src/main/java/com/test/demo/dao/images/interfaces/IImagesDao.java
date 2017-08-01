package com.test.demo.dao.images.interfaces;

import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
public interface IImagesDao {
	int updateByTag(Map tmp);

	int updateByClassification(Map tmp);
}
