package com.haoqi.yungou.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.GlobalApplication;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.domain.User;
import com.haoqi.yungou.fragment.DatePickerFragment;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.DBManager;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.AreaPopupWindow;
import com.haoqi.yungou.widget.CustomDialog;
import com.haoqi.yungou.widget.SelectPopupWindow;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 *  编辑个人资料
 */
public class EditInfoAcitivity extends FragmentActivity implements View.OnClickListener {
    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    private static final int CAMERA_REQUEST_CODE =3;
    private static final int ALTER_NICKNAME =4;
    private ImageView userAvatar;
    private String sdPath = Environment.getExternalStorageDirectory()+"/YunGou/img/";
    private File mPhotoFile;
    private SelectPopupWindow menuWindow;
    private TextView tv_nickname,tv_sex,tv_birthday,tv_qq,tv_current_live_name,tv_hometown_name;
    private AreaPopupWindow pop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        initView();
        getUserInfo();
    }

    private void initView() {
        userAvatar =(ImageView)findViewById(R.id.sriv_avatar);
        LinearLayout ll_nickname = (LinearLayout) findViewById(R.id.ll_nickname);
        LinearLayout ll_sex = (LinearLayout) findViewById(R.id.ll_sex);
        RelativeLayout rl_birthday = (RelativeLayout) findViewById(R.id.ll_birthday);
        RelativeLayout rl_qq = (RelativeLayout) findViewById(R.id.ll_qq);
        LinearLayout ll_current_live = (LinearLayout) findViewById(R.id.ll_current_live);
        LinearLayout ll_hometown = (LinearLayout) findViewById(R.id.ll_hometown);
        ll_nickname.setOnClickListener(this);
        ll_sex.setOnClickListener(this);
        rl_birthday.setOnClickListener(this);
        rl_qq.setOnClickListener(this);
        ll_current_live.setOnClickListener(this);
        ll_hometown.setOnClickListener(this);
        userAvatar.setOnClickListener(this);

        tv_nickname =(TextView)findViewById(R.id.tv_nickname);
        tv_sex =(TextView)findViewById(R.id.tv_sex);
        tv_birthday =(TextView)findViewById(R.id.tv_birthday);
        tv_qq =(TextView)findViewById(R.id.tv_qq);
        tv_current_live_name =(TextView)findViewById(R.id.tv_current_live_name);
        tv_hometown_name =(TextView)findViewById(R.id.tv_hometown_name);
        pop = new AreaPopupWindow(this);
    }

    private void getUserInfo() {


        User user = DBManager.getInstance().getUser();
        ImageLoader.getInstance().displayImage(user.getHeadImg(),userAvatar, CommonUtils.circleImageOptions);
        tv_nickname.setText(user.getUserName());
        tv_sex.setText(user.getSex());
        tv_birthday.setText(user.getBirthday());
        tv_qq.setText(user.getQq());
        tv_current_live_name.setText(user.getLiveAddr());
        tv_hometown_name.setText(user.getHomeTown());
    }

    @Override
    protected void onStart() {
        super.onStart();
        NavigationView title = (NavigationView)findViewById(R.id.title_bar);
        title.getRightView().setVisibility(View.GONE);
        title.setClickCallback(new NavigationView.ClickCallback() {
            @Override
            public void onBackClick() {
                finish();
            }
            @Override
            public void onRightClick() {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sriv_avatar:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.upload_avatar);
                builder.setItems(new String[] { getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload) },
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        String mPhotoPath = sdPath + CommonUtils.getPhotoFileName();
                                        mPhotoFile = new File(mPhotoPath);
                                        if(!mPhotoFile.exists()){
                                            try {
                                                mPhotoFile.createNewFile();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                                        startActivityForResult(intent,CAMERA_REQUEST_CODE);
                                        break;
                                    case 1:
                                        Intent pickIntent = new Intent(Intent.ACTION_PICK,null);
                                        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                        startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                builder.create().show();
                break;
            case R.id.ll_nickname:
                Intent intent = new Intent(this,AlterNicknameActivity.class);
                startActivityForResult(intent,ALTER_NICKNAME);
                break;
            case R.id.ll_sex:
                menuWindow = new SelectPopupWindow(this);
//                Button btn_exit = menuWindow.getBtn_exit();
//                Button btn_cancel = menuWindow.getBtn_cancel();
                menuWindow.showAtLocation(v, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                menuWindow.setPopClickListener(new SelectPopupWindow.PopClickListener() {
                    @Override
                    public void click(int id) {
                        switch (id){
                            case R.id.btn_female:
                                setProfile(3,getResources().getString(R.string.female));
                                break;
                            case R.id.btn_male:
                                setProfile(3,getResources().getString(R.string.male));
                                break;
                        }
                    }
                });
                break;
            case R.id.ll_birthday:
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.show(getFragmentManager(), "datePicker");
                datePicker.setPositiveButton(new DatePickerFragment.PositiveButton() {
                    @Override
                    public void Onclick(String date) {
                        setProfile(4,date);
                    }
                });
                break;
            case R.id.ll_qq:
                CustomDialog.Builder builders=new CustomDialog.Builder(this);
                View view = getLayoutInflater().inflate(R.layout.layout_alter_qq,null);
                final EditText et_qq = (EditText) view.findViewById(R.id.et_qq);
                builders.setView(view);
                builders.setTitle(R.string.set_qq);
                builders.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builders.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        setProfile(5,et_qq.getText().toString());
                    }
                });
                builders.create().show();
                break;
            case R.id.ll_current_live:
                pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                onPopClick(v.getId());
                break;
            case R.id.ll_hometown:
                pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                onPopClick(v.getId());
                break;
        }
    }

    private void onPopClick(final int id) {
        pop.getSelect_Ok().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id==R.id.ll_current_live){
                    setProfile(6,pop.getAdress());
                }else{
                    setProfile(7,pop.getAdress());
                }
                pop.dismiss();
            }
        });
        pop.getSelect_Cancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    if(resultCode==RESULT_OK) {
                        if (data != null) {
                            Bundle bundle = data.getExtras();
                            Bitmap bitmap = bundle.getParcelable("data");
                            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, null, null));
                            startPhotoZoom(uri);
                        } else {
                            Uri uri = Uri.fromFile(mPhotoFile);
                            startPhotoZoom(uri);
                        }
                    }
                    break;
                case REQUESTCODE_PICK:
                    if (data == null || data.getData() == null) {
                        return;
                    }
                    startPhotoZoom(data.getData());
                    break;
                case REQUESTCODE_CUTTING:
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
                case ALTER_NICKNAME:
                    getUserInfo();
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void startPhotoZoom(Uri uri) {
        Uri imageUri = Uri.fromFile(new File(sdPath+CommonUtils.getPhotoFileName()));
        // 目标Uri。裁剪后图片保存路径（SD卡），temp.jpg为一个临时文件，每次拍照后这个图片都会被替换
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }
    private void setPicToView(Intent picdata) {
        Uri selectedImage = picdata.getData();
        String picturePath = CommonUtils.getAbsolutePath(this, selectedImage);
        setPhoto(1,picturePath);
        setResult(RESULT_OK);
    }
    //设置个人资料
    private void setPhoto(int type, final String content) {
        RequestParams params = new RequestParams();
        params.put("editUserType",type+"");
        params.put("userId", UserUtils.getUserId());
        params.put("headImgFile", new File(content));
        VolleyRequest.post(this, Uriconfig.edit_info, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        String headImg = Uriconfig.baseUrl+inf.getString("headImg");
                        DBManager.getInstance().updateUserInfo(Constant.COLUMN_NAME_AVATAR,headImg);//更新本地数据库用户头像
                        ImageLoader.getInstance().displayImage(headImg,userAvatar,CommonUtils.circleImageOptions);
                    }else{
                        GlobalApplication.getInstance().toastShortMsg(R.string.upload_fail);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void requestError(VolleyError e) {
                GlobalApplication.getInstance().toastShortMsg(e.toString());
            }
        });
    }
    private void setProfile(final int type, final String content){
        String loadMsg =getResources().getString(R.string.is_setting);
        RequestParams params = new RequestParams();
        params.put("editUserType",type+"");
        params.put("userId", UserUtils.getUserId());
        params.put("content", content);
        VolleyRequest.post(this, Uriconfig.edit_info, params,loadMsg,new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        switch (type){
                            case 3://性别
                                if(menuWindow!=null){
                                    menuWindow.dismiss();
                                }
                                tv_sex.setText(content);
                                DBManager.getInstance().updateUserInfo(Constant.SEX,content);
                                break;
                            case 4://生日
                                tv_birthday.setText(content);
                                DBManager.getInstance().updateUserInfo(Constant.BIRTHDAY,content);
                                break;
                            case 5:
                                tv_qq.setText(content);
                                DBManager.getInstance().updateUserInfo(Constant.QQ,content);
                                break;
                            case 6://现居住
                                tv_current_live_name.setText(content);
                                DBManager.getInstance().updateUserInfo(Constant.LIVEADDR,content);
                                break;
                            case 7://家乡
                                tv_hometown_name.setText(content);
                                DBManager.getInstance().updateUserInfo(Constant.HOMETOWN,content);
                                break;
                        }
                        GlobalApplication.getInstance().toastShortMsg(R.string.set_success);
                    }else{
                        GlobalApplication.getInstance().toastShortMsg(R.string.set_fail);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void requestError(VolleyError e) {
                GlobalApplication.getInstance().toastShortMsg(e.toString());
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }
}
