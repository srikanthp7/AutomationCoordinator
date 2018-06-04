/**
 * 
 */
package gov.ca.calpers.psr.automation.pojo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.hibernate.Query;
import org.hibernate.Session;

import gov.ca.calpers.psr.automation.ExecutionStatus;

/**
 * The Class TimeUtils.
 *
 * @author burban
 */
public class TimeUtils {

	/**
	 * Instantiates a new time utils.
	 */
	public TimeUtils()
	{
		
	}
	
	 /**
 	 * Gets the 3 std dev duration in minute.
 	 *
 	 * @param timeInMillis the time in millis
 	 * @return the 3 std dev duration in minute
 	 */
 	public synchronized long get3StdDevDurationInMinute(long timeInMillis) {
	        Session session = HibernateUtil.getSecondarySesssion();
	        Query query = session
	                .createQuery("select stddev(finalDuration) from TestResult where executionStatus = :executionStatus"
	                        + " and test_name_id = :test_name_id");
	        query.setParameter("executionStatus", ExecutionStatus.PASSED.getValue());
	        //query.setParameter("test_name_id", id);
	        @SuppressWarnings("rawtypes")
	        List list = query.list();
	        // given answer in 30 mins
	        if (list == null || list.isEmpty()) {
	            return 30;
	        }
	        
	        Double stddev = (Double) list.get(0);
	        // get the mean
	        if (stddev == null || stddev <= 0) {
	            return 30;
	        }
	        query = session
	                .createQuery("select avg(finalDuration) from TestResult where executionStatus = :executionStatus"
	                        + " and test_name_id = :test_name_id");
	        query.setParameter("executionStatus", ExecutionStatus.PASSED.getValue());
	        //query.setParameter("test_name_id", id);
	        @SuppressWarnings("rawtypes")
	        List avgList = query.list();
	        Double avg = (Double) avgList.get(0);
	        // always add 2 minutes since the convert method floor the resul while
	        // we needed it to be ceil. Adding an extra minutes for case where the
	        // value is low
	        return TimeUnit.MINUTES.convert((long) (Math.ceil(avg + 3.0 * stddev)), TimeUnit.MILLISECONDS) + 5;

	    } 
	 
	 	/**
	 	 * Calculate duration in hours minutes seconds.
	 	 *
	 	 * @param startTime the start time
	 	 * @param endTime the end time
	 	 * @return the string
	 	 */
	 	public static String calculateDurationInHoursMinutesSeconds(long startTime, long endTime)
	 	{
	 		String result = "";
	 		long secondsInMilli = 1000;
			long minutesInMilli = secondsInMilli * 60;
			long hoursInMilli = minutesInMilli * 60;
			//long daysInMilli = hoursInMilli * 24;
			long difference = endTime - startTime;
			long elapsedHours = difference / hoursInMilli;
			difference = difference % hoursInMilli;
			
			long elapsedMinutes = difference / minutesInMilli;
			difference = difference % minutesInMilli;
			
			long elapsedSeconds = difference / secondsInMilli;
			
			String hours = String.valueOf(elapsedHours);
			String minutes;
			String seconds;
			
			if(elapsedMinutes < 10)
			{
				minutes = "0"+ String.valueOf(elapsedMinutes);				
			}else
			{
				minutes = String.valueOf(elapsedMinutes);
			}
			
			if(elapsedSeconds < 10)
			{
				seconds = "0"+ String.valueOf(elapsedSeconds);				
			}else
			{
				seconds = String.valueOf(elapsedSeconds);
			}
	 		result =  hours + ":" + minutes + ":" + seconds; 
	 		return result;
	 	}
	 	
	 	/**
	 	 * Calculate duration in hours minutes seconds.
	 	 *
	 	 * @param finalDuration the final duration
	 	 * @return the string
	 	 */
	 	public static String calculateDurationInHoursMinutesSeconds(long finalDuration)
	 	{
	 		String result = "";
	 		long secondsInMilli = 1000;
			long minutesInMilli = secondsInMilli * 60;
			long hoursInMilli = minutesInMilli * 60;
			//long daysInMilli = hoursInMilli * 24;
			long difference = finalDuration;
			long elapsedHours = difference / hoursInMilli;
			difference = difference % hoursInMilli;
			
			long elapsedMinutes = difference / minutesInMilli;
			difference = difference % minutesInMilli;
			
			long elapsedSeconds = difference / secondsInMilli;
			
			String hours = String.valueOf(elapsedHours);
			String minutes;
			String seconds;
			
			if(elapsedMinutes < 10)
			{
				minutes = "0"+ String.valueOf(elapsedMinutes);				
			}else
			{
				minutes = String.valueOf(elapsedMinutes);
			}
			
			if(elapsedSeconds < 10)
			{
				seconds = "0"+ String.valueOf(elapsedSeconds);				
			}else
			{
				seconds = String.valueOf(elapsedSeconds);
			}
	 		result =  hours + ":" + minutes + ":" + seconds; 
	 		return result;
	 	}
	 
}
