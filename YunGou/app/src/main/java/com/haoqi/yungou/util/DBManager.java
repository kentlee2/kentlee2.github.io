package com.haoqi.yungou.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.GlobalApplication;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.db.DbOpenHelper;
import com.haoqi.yungou.domain.User;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Kentlee on 2016/9/22.
 */
public class DBManager {
    static private DBManager dbMgr = new DBManager();
    private DbOpenHelper dbHelper =DbOpenHelper.getInstance(GlobalApplication.getContext());


    public static synchronized DBManager getInstance(){
        return dbMgr;
    }
    /**
     * 保存个人信息
     * @param user
     */
    synchronized public void saveContact(User user){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.COLUMN_NAME_ID, UserUtils.getUserId());
        if(user.getUserName() != null)
            values.put(Constant.COLUMN_NAME_NICK, user.getUserName());//昵称
        if(user.getHeadImg() != null) {
            values.put(Constant.COLUMN_NAME_AVATAR, Uriconfig.baseUrl + user.getHeadImg());//头像
        }
        if(user.getPhone() !=null)
            values.put(Constant.PHONE, user.getPhone());//手机
        if(user.getScore() !=null)
            values.put(Constant.SCORE, user.getScore());//积分
        if(user.getAmout() !=null)
            values.put(Constant.AMOUNT, user.getAmout());//余额
        if(user.getBirthday() !=null)
            values.put(Constant.BIRTHDAY, user.getBirthday());//生日
        if(user.getHomeTown() !=null)
            values.put(Constant.HOMETOWN, user.getHomeTown());//故乡
        if(user.getLiveAddr() !=null)
            values.put(Constant.LIVEADDR, user.getLiveAddr());//现居地
        if(user.getMail() !=null)
            values.put(Constant.MAIL, user.getMail());//邮箱
        if(user.getQq() !=null)
            values.put(Constant.QQ, user.getQq());//qq
        if(user.getSex() !=null) {
            if("0".equals(user.getSex())){
                values.put(Constant.SEX, "男");//性别
            }else{
                values.put(Constant.SEX, "女");//性别
            }

        }
        if(user.getSingnature() !=null)
            values.put(Constant.SINGNATURE, user.getSingnature());//签名
        values.put(Constant.CART, getGoodsCount());
        values.put(Constant.PRAISE, "");
        if(db.isOpen()){
            db.replace(Constant.TABLE_NAME, null, values);
        }
    }
    synchronized public void updateUserInfo(String columeName,String name){
        String userId = UserUtils.getUserId();
        ContentValues values = new ContentValues();
        values.put(columeName, name);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
            db.update(Constant.TABLE_NAME, values, Constant.COLUMN_NAME_ID + " = ?", new String[]{userId});
        }
    }
    synchronized public String getGoodsCount(){
        String userId = UserUtils.getUserId();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select " + Constant.CART + " from " + Constant.TABLE_NAME +" where "+Constant.COLUMN_NAME_ID+"=?",new String[]{userId});
            while (cursor.moveToNext()) {
                String count = cursor.getString(0);
                return count;
            }
            cursor.close();
        }
        return "0";
    }
      public boolean isPraise(String shaidanId){
        String userId = UserUtils.getUserId();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select " + Constant.PRAISE + " from " + Constant.TABLE_NAME +" where "+Constant.COLUMN_NAME_ID+"=?",new String[]{userId});
            while (cursor.moveToNext()) {
                String praisString = cursor.getString(0);
                Gson gson = new Gson();
                ArrayList<Map<String,Boolean>> datalist = gson.fromJson(praisString, new TypeToken<ArrayList<Map<String,Boolean>>>() {
                }.getType());
                if(datalist!=null) {
                    for(int i=0;i<datalist.size();i++){
                        Map<String,Boolean> map = datalist.get(i);
                        if(map.get(shaidanId)!=null) {
                            if (map.get(shaidanId) == true) {
                                return true;
                            }
                        }
                    }
                }
            }
            cursor.close();
        }
        return false;
    }
    /**
     * 获取好友list
     *
     * @return
     */
    synchronized public User getUser() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String id = UserUtils.getUserId();
        User user = new User();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME /* + " desc" */+" where "+Constant.COLUMN_NAME_ID+"=?", new String[]{id});
            while (cursor.moveToNext()) {
                String userId = cursor.getString(cursor.getColumnIndex(Constant.COLUMN_NAME_ID));
                String username = cursor.getString(cursor.getColumnIndex(Constant.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(Constant.COLUMN_NAME_AVATAR));
                String phone = cursor.getString(cursor.getColumnIndex(Constant.PHONE));
                String score = cursor.getString(cursor.getColumnIndex(Constant.SCORE));
                String amount = cursor.getString(cursor.getColumnIndex(Constant.AMOUNT));
                String birthday = cursor.getString(cursor.getColumnIndex(Constant.BIRTHDAY));
                String hometown = cursor.getString(cursor.getColumnIndex(Constant.HOMETOWN));
                String liveaddr = cursor.getString(cursor.getColumnIndex(Constant.LIVEADDR));
                String mail = cursor.getString(cursor.getColumnIndex(Constant.MAIL));
                String qq = cursor.getString(cursor.getColumnIndex(Constant.QQ));
                String sex = cursor.getString(cursor.getColumnIndex(Constant.SEX));
                String singnature = cursor.getString(cursor.getColumnIndex(Constant.SINGNATURE));
                user.setId(userId);
                user.setUserName(username);
                user.setHeadImg(avatar);
                user.setPhone(phone);
                user.setScore(score);
                user.setAmout(amount);
                user.setBirthday(birthday);
                user.setHomeTown(hometown);
                user.setLiveAddr(liveaddr);
                user.setMail(mail);
                user.setQq(qq);
                user.setSex(sex);
                user.setSingnature(singnature);
            }
            cursor.close();
        }
        return user;
    }
    synchronized public void saveGoodsCount(String userId,String count){

    }
}
