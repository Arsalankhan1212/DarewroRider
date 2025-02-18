/**
 * 
 */
package com.darewro.rider.view.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author HZeb
 * 
 */
@SuppressLint("SimpleDateFormat")
public final class TimeUtil 
{
	public static final String DDMMMYYY = "dd-MM-yyyy";
	public static final String DATE_TIME_FORMAT_12 = "yyyy-MM-dd hh:mm a";
	public static final String DATE_TIME_FORMAT_13 = "yyyy-MM-dd hh:mm:ss a";

	public static final String YYYYMMDD = "yyyy-MM-dd";

	/**
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @return
	 */
	public static Date toDate(int year, int month, int dayOfMonth)
	{
		Calendar cal = Calendar.getInstance();
	
		cal.set(Calendar.YEAR, year);
		
		cal.set(Calendar.MONTH, month);
		
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		
		return cal.getTime();
	}
	
	/**
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @return
	 */
	public static String toStringDate(int year, int month, int dayOfMonth, String format)
	{
		Date date = TimeUtil.toDate(year, month, dayOfMonth);
	
		return TimeUtil.toString(date, format);
	}
	
	/**
	 * @param dateString
	 * @param format
	 * @return
	 */
	public static String toString(Date date, String format)
	{
		SimpleDateFormat df = new SimpleDateFormat(format);

		String str = null;
		
		try 
		{
			str= df.format(date);
		
			return str;
		} 
		catch (Exception e)
		{
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	/**
	 * @param dateString
	 * @param format
	 * @return
	 */
	public static Date toDate(String dateString, String format)
	{
		SimpleDateFormat df = new SimpleDateFormat(format);

		Date d = null;
	
		try 
		{
			d = df.parse(dateString);
		} 
		catch (java.text.ParseException e) 
		{
			throw new IllegalArgumentException(e.getMessage());
		}
		
		return d;
	}

	/**
	 * @param dateString
	 * @param format
	 * @return
	 */
	public static Calendar toCalendar(String dateString, String format)
	{
		Date date = TimeUtil.toDate(dateString, format);
	
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		
		return cal;		
	}

	/**
	 * @param dateString
	 * @param formats
	 * @return
	 */
	public static Calendar toCalendar(String dateString, String[] formats)
	{
		Date date = null;
	
		for (String format : formats)
		{ 
			try 
			{
				date = TimeUtil.toDate(dateString, format);
			
				break;
			}
			catch (Exception exception)
			{
				exception.printStackTrace();
			}
		}
		
		Calendar cal = Calendar.getInstance();
		
		if (date != null) 
		{
			cal.setTime(date);
		}
	
		return cal;		
	}
	
	/**
	 * @return
	 */
	public static long curentTimeMillies()
	{
		return new Date().getTime();
	}
}
