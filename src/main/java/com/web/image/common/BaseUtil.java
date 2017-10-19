package com.web.image.common;

import org.apache.commons.lang3.StringUtils;
import java.util.List;

/**
 * Created by Administrator on 2017/8/18.
 */
public class BaseUtil {
    /**
     * @param paramList
     * @return
     */
    public static String getSqlParams(List<String> paramList) {
        String sqlParams = "'";
        sqlParams += StringUtils.join(paramList, "','");
        sqlParams += "'";
        return sqlParams;
    }
}
