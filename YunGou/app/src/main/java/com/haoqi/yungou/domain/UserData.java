package com.haoqi.yungou.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Kentlee on 2016/9/23.
 */
public class UserData implements Serializable {
    private User inf;
    private Map<String,String> res;
    public User getInf() {
        return inf;
    }

    public Map<String,String> getRes() {
        return res;
    }
}
