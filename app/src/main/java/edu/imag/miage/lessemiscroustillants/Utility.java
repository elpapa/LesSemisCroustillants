package edu.imag.miage.lessemiscroustillants;

import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by boris on 19/04/2016.
 */
public class Utility {

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }
}
