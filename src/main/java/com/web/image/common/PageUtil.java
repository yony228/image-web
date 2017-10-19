package com.web.image.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 分页公用类
 * Created by Administrator on 2017/6/27.
 */
public class PageUtil {
    /**
     * @param param page_index  page_size
     * @return
     */
    public static Map getPage(Map param) {
        Map pageInfo = null;
        if (param.containsKey("page_index") && param.containsKey("page_size")) {
            pageInfo = new HashMap();
            int pageSize = Integer.parseInt(param.get("page_size").toString());
            int offset = (Integer.parseInt(param.get("page_index").toString()) - 1) * pageSize;

            pageInfo.put("offset", offset);
            pageInfo.put("limit", pageSize);
        }
        return pageInfo;
    }
}
