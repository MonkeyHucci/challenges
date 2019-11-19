package model;

public class PushEvent extends AbstractEvent implements IEvent {

	@Override
	public EventType getType() {
		return EventType.PUSH;
	}
	
}
