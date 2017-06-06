package com.haoqi.yungou.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Kentlee on 2016/9/28.
 */
public class DatePickerFragment  extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    private PositiveButton positive;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Log.d("OnDateSet", "select year:"+year+";month:"+month+";day:"+day);
        String   date = year+"-"+(month+1)+"-"+day;
        positive.Onclick(date);
    }
   public interface PositiveButton{
       void Onclick(String date);
   }
    public void setPositiveButton(PositiveButton pos){
        this.positive = pos;
    }
}
