package com.darewro.rider.view.customViews;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.TimeZone;

public class CustomDatePickerWithRange implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public static int YYYY_MM_DD = 1;
    public static int DD_MM_YYYY = 2;
    TextView _editText;
    private int _day;
    private int _month;
    private int _birthYear;
    private Context _context;
    private int format;
    private int yearsMaxLimit = 0;
    private int yearsMinLimit = 0;

    public CustomDatePickerWithRange(Context context, TextView editText) {
        Activity act = (Activity) context;
        this._editText = editText;
        this._editText.setOnClickListener(this);
        this._context = context;
        this.format = YYYY_MM_DD;
    }

    public CustomDatePickerWithRange(Context context, TextView editText, int format) {
        Activity act = (Activity) context;
        this._editText = editText;
        this._editText.setOnClickListener(this);
        this._context = context;
        this.format = format;
    }

    public CustomDatePickerWithRange(Context context, TextView editText, int yearsMaxLimit, int yearsMinLimit, int format) {
        Activity act = (Activity) context;
        this._editText = editText;
        this._editText.setOnClickListener(this);
        this._context = context;
        this.format = format;
        this.yearsMinLimit = yearsMinLimit;
        this.yearsMaxLimit = yearsMaxLimit;
    }

    public CustomDatePickerWithRange(Context _context, TextView _editText, int yearsMaxLimit, int yearsMinLimit) {
        this._editText = _editText;
        this._editText.setOnClickListener(this);
        this._context = _context;
        this.yearsMaxLimit = yearsMaxLimit;
        this.yearsMinLimit = yearsMinLimit;
        this.format = YYYY_MM_DD;
    }

    public int getFormat() {
        return format;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _birthYear = year;
        _month = monthOfYear;
        _day = dayOfMonth;
        updateDisplay();
    }

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(_context, AlertDialog.THEME_HOLO_LIGHT, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

//        dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
/*

        dialog.getDatePicker().setMaxDate(AppUtils.getTimeInMilliSecondsInYearsAfter(yearsMaxLimit));
        dialog.getDatePicker().setMinDate(AppUtils.getTimeInMilliSecondsInYearsBefore(yearsMaxLimit));
*/

        if (yearsMaxLimit != 0) {
            Calendar c = Calendar.getInstance();
            c.set(calendar.get(Calendar.YEAR) + yearsMaxLimit, 11, 31);//Year,Mounth -1,Day
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        } else {
            dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        }

        if (yearsMinLimit != 0) {
            Calendar c = Calendar.getInstance();
            c.set(calendar.get(Calendar.YEAR) - yearsMinLimit, 11, 31);//Year,Mounth -1,Day
            dialog.getDatePicker().setMinDate(c.getTimeInMillis());
        } else {
            Calendar c = Calendar.getInstance();
            c.set(1947, 8, 14);//Year,Mounth -1,Day
            dialog.getDatePicker().setMinDate(c.getTimeInMillis());
        }

        if (_editText.getText().toString() != null && (!_editText.getText().toString().equals("")) && _editText.getText().toString().contains("/")) {
            String[] date = _editText.getText().toString().trim().split("/");
            if (date.length == 3)
                calendar.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]));

            dialog = new DatePickerDialog(_context, this,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
        }

        dialog.show();

    }

    // updates the date in the birth date EditText
    private void updateDisplay() {
        int year = _birthYear;
        int month = _month + 1;
        int day = _day;

        if (format == YYYY_MM_DD) {
            // Month is 0 based so add 1
            _editText.setText(new StringBuilder().append(year).append("-").append(month < 10 ? "0" + month : month).append("-").append(day < 10 ? "0" + day : day));
            _editText.setError(null);
        }
        if (format == DD_MM_YYYY) {
            // Month is 0 based so add 1
            _editText.setText(new StringBuilder().append(day < 10 ? "0" + day : day).append("-").append(month < 10 ? "0" + month : month).append("-").append(year));
            _editText.setError(null);
        }
    }
}
