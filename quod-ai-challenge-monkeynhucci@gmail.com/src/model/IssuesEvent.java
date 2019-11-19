package model;

public class IssuesEvent extends AbstractEvent implements IEvent {

	private Payload payload;

	public String getAction() {
		return payload.getAction();
	}

	public long getOpeningTime() {
		return payload.getOpeningTime();
	}

	@Override
	public EventType getType() {
		return EventType.ISSUES;
	}

}
