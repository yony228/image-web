package com.web.image.service.impl;

import com.web.image.common.PageUtil;
import com.web.image.common.PinyinTool;
import com.web.image.dao.IImagesDao;
import com.web.image.dao.ITagsDao;
import com.web.image.service.ITagService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public boolean updateTag(Map<String, String> param) {
        //查找用户下同名Tag
        List<Map<String, Object>> list;
        try {
            list = this.findSameTag(param.get("id").toString(), param.get("user_id").toString(), param.get("alias").toString(), null);
        } catch (Exception e) {
            return false;
        }

        if (list.size() == 0) {
            LOGGER.info("updateTag 不存在同名标签，直接修改。");
            String tag = this.getTag(param.get("id").toString(), param.get("user_id").toString(), param.get("alias").toString());
            param.put("tag", tag);
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
    public boolean insertTags(String alias, String des, String userId, String isPublic) {
        //查找用户下同名Tag是否存在
        List<Map<String, Object>> sameTag;
        try {
            sameTag = this.findSameTag(null, userId, alias, null);
        } catch (Exception e) {
            return false;
        }

        if (sameTag.size() == 0) {
            //添加标签
            Map tmp = new HashMap();
            tmp.put("alias", alias);
            tmp.put("user_id", userId);
            tmp.put("des", des);
            tmp.put("is_public", isPublic);

            String tag = this.getTag(null, userId, alias);
            tmp.put("tag", tag);
            tagsDao.save(tmp);
            return true;
        }
        return false;
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
    public List<Map<String, Object>> findSameTag(String id, String userId, String alias, String tag) throws Exception {
        if (StringUtils.isEmpty(alias) && StringUtils.isEmpty(tag)) {
            throw new Exception("标签名不能为空");
        }
        Map tmp = new HashMap();
        tmp.put("alias", alias);
        tmp.put("user_id", userId);
        tmp.put("not_id", id);
        tmp.put("tag", tag);
        List<Map<String, Object>> list = tagsDao.queryTags(tmp, null);
        return list;
    }

    /**
     * 生成标签别名
     *
     * @param id
     * @param userId
     * @param alias
     * @return
     */
    public String getTag(String id, String userId, String alias) {
        List<Map<String, Object>> sameTag;
        String tag = "";
        try {
            tag = pinyinTool.toPinYin(alias, "", PinyinTool.Type.LOWERCASE);

            //tag,如果拼音重复，在后面添加随机数
            sameTag = this.findSameTag(id, userId.toString(), null, tag);

            if (sameTag.size() != 0) {
                int x = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;
                tag = tag + String.valueOf(x);
            }
        } catch (Exception e) {
            LOGGER.info(e.toString());
            return "";
        }
        return tag;
    }
}