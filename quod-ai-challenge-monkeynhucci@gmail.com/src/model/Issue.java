package model;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class Issue {

	private String created_at;
	private String closed_at;

	public long getOpeningTime() {
		return new Period(DateTime.parse(created_at), DateTime.parse(closed_at)).toDurationFrom(new DateTime(0))
				.getMillis() / 1000;
	}
}
