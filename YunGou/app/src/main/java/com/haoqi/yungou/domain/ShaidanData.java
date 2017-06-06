package com.haoqi.yungou.domain;

import java.util.List;

/**
 * Created by Kentlee on 2016/10/31.
 */
public class ShaidanData {
    private int pageCount;
    private int firstIndex;
    private String isPageEnd;
    private List<Shaidan> shareOrder;


    public class Shaidan{
       private String img;
        private String createTime;
        private String id;
        private String detail;
        private String title;
        private String status;

        public String getImg() {
            return img;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getId() {
            return id;
        }

        public String getDetail() {
            return detail;
        }

        public String getTitle() {
            return title;
        }

        public String getStatus() {
            return status;
        }
    }

    public List<Shaidan> getShareOrder() {
        return shareOrder;
    }

    public String getIsPageEnd() {
        return isPageEnd;
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getFirstIndex() {
        return firstIndex;
    }
}
