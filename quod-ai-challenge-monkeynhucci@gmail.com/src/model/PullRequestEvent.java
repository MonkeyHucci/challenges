package model;

public class PullRequestEvent extends AbstractEvent implements IEvent {

	private Payload payload;

	public boolean isMerged() {
		return payload.isMerged();
	}

	public long getMergedTime() {
		return payload.getMergedTime();
	}

	@Override
	public EventType getType() {
		return EventType.PULL_REQUEST;
	}

}
