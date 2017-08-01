package com.test.demo.service.classificationandtag.impl;

import com.test.demo.common.PageUtil;
import com.test.demo.common.PinyinTool;
import com.test.demo.dao.classifications.interfaces.IClassificationsDao;
import com.test.demo.dao.images.interfaces.IImagesDao;
import com.test.demo.service.classificationandtag.interfaces.IClassificationService;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
@Transactional
@Service
public class ClassificationServiceImpl implements IClassificationService {

	@Autowired
	IClassificationsDao classificationsDao;

	@Autowired
	private IImagesDao imagesDao;

	@Autowired
	private PinyinTool pinyinTool;

	@Override
	public List<Map<String, Object>> queryClassifications(Map param) {
		Map pageInfo = PageUtil.getPage(param);
		return classificationsDao.queryClassifications(param, pageInfo);
	}

	@Override
	public List<Map<String, Object>> fuzzyQueryClassification(Map param){
		Map pageInfo = PageUtil.getPage(param);
		return classificationsDao.fuzzyQueryClassifications(param, pageInfo);
	}

	@Override
	public long queryImageCountByClassification(long id) {
		return classificationsDao.queryImageCountByClassification(id);
	}

	/**
	 * 更新分类，
	 * @param param
	 * @return
	 */
	@Override
	public boolean updateClassification(Map param) {
		//查找模型下同名classification
		List<Map<String, Object>> list = findSameClassificationOnModel(param);
		if (list.size() == 0) {
			//TODO 如果已产生模型，不能修改classification。
			try {
				param.put("classification", pinyinTool.toPinYin(MapUtils.getString(param, "alias"), "", PinyinTool.Type.LOWERCASE));
			} catch (Exception e) {
			}
			return classificationsDao.updateById(param) > 0;
		} else {
			Map classificationInDb = list.get(0);
			//更新图片
			Map tmp = new HashMap();
			tmp.put("classification_id", param.get("id"));
			tmp.put("new_classification_id", classificationInDb.get("id"));
			imagesDao.updateByClassification(tmp);
			//删除冗余分类
			classificationsDao.delete(MapUtils.getLong(param,"id"));
			return true;
		}
	}

	@Override
	public List<Map<String,Object>> findSameClassificationOnModel(Map param) {
		Map tmp = new HashMap();
		tmp.put("alias", param.get("alias"));
		tmp.put("model_id", param.get("model_id"));
		tmp.put("not_id", param.get("id"));
		List<Map<String, Object>> list = classificationsDao.queryClassifications(tmp, null);
		return list;
	}

	@Override
	public boolean checkSameClassificationOnModel(Map param){
		//如果classification为空，则返回存在
		if (MapUtils.getString(param,"alias").isEmpty()){
			return true;
		}
		return classificationsDao.queryClassificationCount(param) > 0;
	}

	@Override
	public boolean deleteClassification(long id) {
		if(0 == classificationsDao.queryImageCountByClassification(id)) {
			return classificationsDao.delete(id) > 0;
		} else {
			return false;
		}
	}

	@Override
	public boolean addClassification(Map param) {
		//设置查询条件
		Map tmp = new HashMap<>();
		tmp.put("alias",param.get("alias"));
		tmp.put("model_id",param.get("model_id"));
		if(!checkSameClassificationOnModel(tmp)){
			try {
				param.put("classification", pinyinTool.toPinYin(MapUtils.getString(param, "alias"), "", PinyinTool.Type.LOWERCASE));
				//TODO 校验classification是否重复

			} catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
				badHanyuPinyinOutputFormatCombination.printStackTrace();
			}
			classificationsDao.save(param);
			return true;
		}
		return false;

	}
}
