package model;

public interface IEvent {

	public enum EventType {
		PUSH, ISSUES, PULL_REQUEST
	}

	public EventType getType();

	public String getActorId();

	public String getRepoName();

}
