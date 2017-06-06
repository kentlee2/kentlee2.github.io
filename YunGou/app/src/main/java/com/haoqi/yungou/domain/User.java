package com.haoqi.yungou.domain;

/**
 * Created by Kentlee on 2016/9/22.
 */
public class User {
    private String id;
    private String userName;
    private String birthday;
    private String score;
    private String amout;
    private String mail;
    private String phone;
    private String qq;
    private String singnature;
    private String homeTown;
    private String sex;
    private String liveAddr;
    private String HeadImg;
//    public User(JSONObject inf) {
//        try {
//            userName = inf.getString("userName");
//            birthday = inf.getString("birthday");
//            score =inf.getString("score");
//            amout =inf.getString("amout");
//            mail =inf.getString("mail");
//            phone =inf.getString("phone");
//            qq =inf.getString("qq");
//            singnature =inf.getString("singnature");
//            homeTown =inf.getString("homeTown");
//            sex =inf.getString("sex");
//            liveAddr =inf.getString("liveAddr");
//            HeadImg =inf.getString("HeadImg");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public User() {
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getAmout() {
        return amout;
    }

    public void setAmout(String amout) {
        this.amout = amout;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getSingnature() {
        return singnature;
    }

    public void setSingnature(String singnature) {
        this.singnature = singnature;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLiveAddr() {
        return liveAddr;
    }

    public void setLiveAddr(String liveAddr) {
        this.liveAddr = liveAddr;
    }

    public String getHeadImg() {
        return HeadImg;
    }

    public void setHeadImg(String headImg) {
        HeadImg = headImg;
    }
}
