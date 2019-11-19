package model;

public class Payload {

	private String action;
	private Issue issue;
	private PullRequest pull_request;

	public String getAction() {
		return action;
	}

	public long getOpeningTime() {
		return issue.getOpeningTime();
	}

	public boolean isMerged() {
		return pull_request.isMerged();
	}

	public long getMergedTime() {
		return pull_request.getMergedTime();
	}

}
