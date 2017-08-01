package com.test.demo.service.classificationandtag.impl;

import com.test.demo.common.PageUtil;
import com.test.demo.common.PinyinTool;
import com.test.demo.dao.images.interfaces.IImagesDao;
import com.test.demo.dao.tags.interfaces.ITagsDao;
import com.test.demo.service.classificationandtag.interfaces.ITagService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
@Transactional
@Service
public class TagServiceImpl implements ITagService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagServiceImpl.class);

    @Autowired
    private ITagsDao tagsDao;

    @Autowired
    private IImagesDao imagesDao;

    @Autowired
    private PinyinTool pinyinTool;

    @Override
    public List<Map<String, Object>> queryTags(Map param) {
        Map pageInfo = PageUtil.getPage(param);
        return tagsDao.queryTags(param, pageInfo);
    }

    @Override
    public List<Map<String, Object>> fuzzyQueryTags(Map param) {
        Map pageInfo = PageUtil.getPage(param);
        return tagsDao.fuzzyQueryTags(MapUtils.getString(param, "condition"), MapUtils.getString(param, "user_id"), pageInfo);
    }

    @Override
    public long queryImageCountByTag(long tagId) {
        return tagsDao.queryImageCountByTag(tagId);
    }

    @Override
    public boolean updateTag(Map param) {
        //查找用户下同名Tag
        List<Map<String, Object>> list = findSameTagPerUser(param);
        if (list.size() == 0) {
            LOGGER.info("updateTag 不存在同名标签，直接修改。");
            try {
                param.put("tag", pinyinTool.toPinYin(param.get("alias").toString(), "", PinyinTool.Type.LOWERCASE));
            } catch (Exception e) {
            }
            return tagsDao.updateById(param) > 0;
        } else {
            LOGGER.info("updateTag 存在同名标签，合并。new_tag_id=" + list.get(0).get("id"));
            //更新图片所属标签
            Map tmp = new HashMap();
            tmp.put("tag_id", param.get("id"));
            tmp.put("new_tag_id", list.get(0).get("id"));
            imagesDao.updateByTag(tmp);
            //删除冗余分类
            tagsDao.delete(Long.parseLong(param.get("id").toString()));
            return true;
        }
    }

    @Override
    public List<Map<String, Object>> findSameTagPerUser(Map param) {
        Map tmp = new HashMap();
        tmp.put("alias", param.get("alias"));
        tmp.put("user_id", param.get("user_id"));
        if (param.containsKey("id")) {
            tmp.put("not_id", param.get("id"));
        }
        List<Map<String, Object>> list = tagsDao.queryTags(tmp, null);
        return list;
    }

    @Override
    public boolean checkSameTagPerUser(Map param) {
        if (MapUtils.getString(param, "alias").isEmpty()) {
            return true;
        }
        return tagsDao.queryTagsCount(param) > 0;
    }

    @Override
    public boolean deleteTag(long id) {
        if (0 == tagsDao.queryImageCountByTag(id)) {
            return tagsDao.delete(id) > 0;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(Map param) {
        tagsDao.save(param);
        return true;
    }

    @Override
    public boolean insertTags(String alias, String des, String userId) {
        Map tmp = new HashMap();
        tmp.put("alias", alias);
        tmp.put("user_id", userId);
        //查找用户下同名Tag是否存在
        if (!checkSameTagPerUser(tmp)) {
            //添加标签
            try {
                tmp.put("tag", pinyinTool.toPinYin(tmp.get("alias").toString(), "", PinyinTool.Type.LOWERCASE));
            } catch (Exception e) {
            }
            //描述信息无需检测
            tmp.put("des", des);
            addTag(tmp);
            return true;
        }
        return false;
    }
}