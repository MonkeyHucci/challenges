package model;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class PullRequest {

	private boolean merged;
	private String created_at;
	private String merged_at;

	public boolean isMerged() {
		return merged;
	}

	public long getMergedTime() {
		if (created_at != null && merged_at != null) {
			return new Period(DateTime.parse(created_at), DateTime.parse(merged_at)).toDurationFrom(new DateTime(0))
					.getMillis() / 1000;
		} else {
			return 0;
		}
	}

}
