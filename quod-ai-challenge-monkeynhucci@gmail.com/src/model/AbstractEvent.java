package model;

public abstract class AbstractEvent implements IEvent {

	private Repo repo;
	private Actor actor;

	public String getActorId() {
		return actor.getId();
	}

	@Override
	public String getRepoName() {
		return repo.getName();
	}

}
