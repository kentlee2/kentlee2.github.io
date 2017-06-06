/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.haoqi.yungou.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.haoqi.yungou.Constant;

/**
 * 用户表
 */
public class DbOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 4;
	private static DbOpenHelper instance;

	private static  String USERNAME_TABLE_CREATE = "CREATE TABLE "
			+ Constant.TABLE_NAME + " ("
			+ Constant.COLUMN_NAME_NICK + " TEXT, "
			+ Constant.COLUMN_NAME_AVATAR + " TEXT, "
			+ Constant.PHONE + " TEXT, "
			+ Constant.SCORE + " TEXT, "
			+ Constant.AMOUNT + " TEXT, "
			+ Constant.BIRTHDAY + " TEXT, "
			+ Constant.HOMETOWN + " TEXT, "
			+ Constant.LIVEADDR + " TEXT, "
			+ Constant.MAIL + " TEXT, "
			+ Constant.QQ + " TEXT, "
			+ Constant.SEX + " TEXT, "
			+ Constant.SINGNATURE + " TEXT, "
			+ Constant.CART +" TEXT, "
			+ Constant.PRAISE +" TEXT, "
			+ Constant.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";


	private DbOpenHelper(Context context) {
		super(context, getUserDatabaseName(), null, DATABASE_VERSION);
	}

	public static DbOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DbOpenHelper(context.getApplicationContext());
		}
		return instance;
	}

	private static String getUserDatabaseName() {
        return "users" + "_yg.db";
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(USERNAME_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion < 2){
		    db.execSQL("ALTER TABLE "+ Constant.TABLE_NAME +" ADD COLUMN "+
					Constant.COLUMN_NAME_AVATAR + " TEXT ;");
		}
	}

	public void closeDB() {
	    if (instance != null) {
	        try {
	            SQLiteDatabase db = instance.getWritableDatabase();
	            db.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        instance = null;
	    }
	}
	public boolean deleteDatabase(Context context) {
		return context.deleteDatabase(Constant.TABLE_NAME);
	}
}
