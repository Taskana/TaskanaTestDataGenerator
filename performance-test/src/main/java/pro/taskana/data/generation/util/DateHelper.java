package pro.taskana.data.generation.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class provides {@link Timestamp}.
 * 
 * @author fe
 *
 */
public class DateHelper {

	private static final int[] MAX_WORKBASKETS_PER_DAY_IN_WEEKY = new int[] { 30, 2, 2, 10, 100, 0, 0 };
	private static final int SUPPORTED_DAYS = MAX_WORKBASKETS_PER_DAY_IN_WEEKY.length;

	private static final int WORKING_DAY_START_HOUR = 7;
	private static final int WORKING_DAY_END_HOUR = 18;
	private static final int SECONDS_PER_WORKING_DAY = (WORKING_DAY_END_HOUR - WORKING_DAY_START_HOUR) * 3600;

	private LocalDateTime nextDate;
	private List<Integer> intervalPerDay = new ArrayList<>();

	public DateHelper() {
		nextDate = LocalDateTime.of(2010, 1, 4, 7, 0);
		initIntervalsPerDay();
	}

	/**
	 * Generates a new {@link Timestamp} according to the defined guidelines.
	 * 
	 * @return  next {@link Timestamp}
	 */
	public Instant getNextTimestampForWorkbasket() {
		Timestamp nextTimestamp = Timestamp.valueOf(nextDate);
		updateNextDate();
		return nextTimestamp.toInstant();
	}

	private void updateNextDate() {
		int currentDay = nextDate.getDayOfWeek().getValue();
		int intervalForCurrentDay = intervalPerDay.get(currentDay % SUPPORTED_DAYS);
		LocalDateTime tmpNextDate = nextDate.plusSeconds(intervalForCurrentDay);
		if (tmpNextDate.getHour() > WORKING_DAY_END_HOUR) {
			goToNextWorkingDay(currentDay, tmpNextDate);
		}
		nextDate = tmpNextDate;
	}
	
	private void goToNextWorkingDay(int currentDay, LocalDateTime nextDate) {
		int skippedDays = 1;
		while (intervalPerDay.get((currentDay + skippedDays) % SUPPORTED_DAYS) == 0) {
			skippedDays++;
		}
		nextDate.plusDays(skippedDays).withHour(WORKING_DAY_START_HOUR).withMinute(0).withSecond(0).withNano(0);
	}

	private void initIntervalsPerDay() {
		for (int numberOfWorkbaskets : MAX_WORKBASKETS_PER_DAY_IN_WEEKY) {
			if (numberOfWorkbaskets == 0) {
				intervalPerDay.add(SECONDS_PER_WORKING_DAY);
			} else {
				int interval = 1;
				if(SECONDS_PER_WORKING_DAY > numberOfWorkbaskets) {
					interval = SECONDS_PER_WORKING_DAY / numberOfWorkbaskets;
				}
				
				intervalPerDay.add(interval);
			}
		}
	}

}
