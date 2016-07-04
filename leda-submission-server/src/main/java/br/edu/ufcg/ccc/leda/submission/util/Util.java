package br.edu.ufcg.ccc.leda.submission.util;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	public static final Pattern PATTERN_DATE_TIME = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}");
	
	public static GregorianCalendar buildDate(Date dataHora){
		GregorianCalendar result = null;
		
		if(dataHora != null){
			result = new GregorianCalendar();
			result.setTime(dataHora);
		}
			
		return result;
	}
	
	/**
	 * dataHora precisa ter o formato DD/MM/YYYY HH:MM:SS
	 * @param dataHora
	 * @return
	 * @throws WrongDateHorFormatException 
	 */
	public static GregorianCalendar buildDate(String dataHora) throws WrongDateHorFormatException{
		GregorianCalendar result = new GregorianCalendar();
		//se estiver no formato errado retorna uma excecao
		//tem que fazer isso com string format provavelmente ou regex
		if(!PATTERN_DATE_TIME.matcher(dataHora).matches()){
			throw new WrongDateHorFormatException("Date " + dataHora + " does not respect the format DD/MM/YYYY HH:MM:SS");
		}
		result.set(Calendar.DATE, Integer.parseInt(dataHora.substring(0, 2)));
		result.set(Calendar.MONTH, Integer.parseInt(dataHora.substring(3,5)));
		result.set(Calendar.YEAR, Integer.parseInt(dataHora.substring(6, 10)));
		result.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dataHora.substring(11, 13)));
		result.set(Calendar.MINUTE, Integer.parseInt(dataHora.substring(14, 16)));
		result.set(Calendar.SECOND, Integer.parseInt(dataHora.substring(17)));
		
		return result;
	}

	public static void main(String[] args) throws WrongDateHorFormatException {
		Pattern pattern = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}");
		Matcher matcher = pattern.matcher("49/04/1970  14:00:00");
		System.out.println(matcher.matches());
		String[] elements = pattern.split("49/04/1970  14:00:00");
		System.out.println(elements.length);
		System.out.println(Arrays.toString(elements));
		System.out.println(matcher.groupCount());
		GregorianCalendar calendar = buildDate("20/07/2016 14:00:00");
		System.out.println("ERA: " + calendar.get(Calendar.ERA));
		 System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
		 System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
		 System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
		 System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
		 System.out.println("DATE: " + calendar.get(Calendar.DATE));
		 System.out.println("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
		 System.out.println("DAY_OF_YEAR: " + calendar.get(Calendar.DAY_OF_YEAR));
		 System.out.println("DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
		 System.out.println("DAY_OF_WEEK_IN_MONTH: "
		                    + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
		 System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
		 System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
		 System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
		 System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
		 System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
		 System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
		 System.out.println("ZONE_OFFSET: "
		                    + (calendar.get(Calendar.ZONE_OFFSET)/(60*60*1000)));
		 System.out.println("DST_OFFSET: "
		                    + (calendar.get(Calendar.DST_OFFSET)/(60*60*1000)));

		 System.out.println("Current Time, with hour reset to 3");
		 calendar.clear(Calendar.HOUR_OF_DAY); // so doesn't override
		 calendar.set(Calendar.HOUR, 3);
		 System.out.println("ERA: " + calendar.get(Calendar.ERA));
		 System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
		 System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
		 System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
		 System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
		 System.out.println("DATE: " + calendar.get(Calendar.DATE));
		 System.out.println("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
		 System.out.println("DAY_OF_YEAR: " + calendar.get(Calendar.DAY_OF_YEAR));
		 System.out.println("DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
		 System.out.println("DAY_OF_WEEK_IN_MONTH: "
		                    + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
		 System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
		 System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
		 System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
		 System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
		 System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
		 System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
		 System.out.println("ZONE_OFFSET: "
		        + (calendar.get(Calendar.ZONE_OFFSET)/(60*60*1000))); // in hours
		 System.out.println("DST_OFFSET: "
		        + (calendar.get(Calendar.DST_OFFSET)/(60*60*1000))); // in hours
	}
}
