package com.haoqi.yungou.domain;

import java.util.Map;

/**
 * Created by Kentlee on 2016/10/31.
 */
public class JsonResult<T> {
    private T inf;
    private Map<String,String> res;

    public T getInf() {
        return inf;
    }

    public Map<String, String> getRes() {
        return res;
    }
}
