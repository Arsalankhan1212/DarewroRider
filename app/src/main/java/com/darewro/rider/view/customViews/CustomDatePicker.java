package com.darewro.rider.view.customViews;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.darewro.rider.view.listeners.DateUpdateListener;

import java.util.Calendar;
import java.util.TimeZone;


public class CustomDatePicker implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    TextView _editText;
    private int _day;
    private int _month;
    private int _birthYear;
    private Context _context;
    private DateUpdateListener dateUpdateListener;

    public CustomDatePicker(Context context, TextView editText) {
        Activity act = (Activity) context;
        this._editText = editText;
        this._editText.setOnClickListener(this);
        this._context = context;
    }

    public CustomDatePicker(Context context, TextView editText, DateUpdateListener dateUpdateListener) {
        Activity act = (Activity) context;
        this._editText = editText;
        this._editText.setOnClickListener(this);
        this._context = context;
        this.dateUpdateListener = dateUpdateListener;
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

        DatePickerDialog dialog = new DatePickerDialog(_context, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

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
        String d = _day < 10 ? "0" + _day : _day + "";
        int month = _month + 1;
        String m = month < 10 ? "0" + month : month + "";
        StringBuilder stringBuilder = new StringBuilder()
                // Month is 0 based so add 1
                .append(d).append("-").append(m).append("-").append(_birthYear).append(" ");

        _editText.setText(stringBuilder);

        if (dateUpdateListener != null) {
            dateUpdateListener.onDateUpdated(_editText, stringBuilder.toString());
        }
    }
}
