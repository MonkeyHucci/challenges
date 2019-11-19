package model;

import java.util.LinkedHashSet;
import java.util.Set;

public class Project {

	private String org;
	private String repoName;
	private double healthScore;
	private long numOfCommits;
	private long issueOpeningTime;
	private long numOfClosedIssues;
	private long pullMergedTime;
	private long numOfMergedPull;
	private Set<String> developers;

	public Project(String repo) {
		String[] repoParts = repo.split("/");
		org = repoParts[0];
		repoName = repoParts[1];
		numOfCommits = 0;
		issueOpeningTime = 0;
		numOfClosedIssues = 0;
		pullMergedTime = 0;
		numOfMergedPull = 0;
		developers = new LinkedHashSet<>();
	}

	synchronized public void addEvent(IEvent event) {
		switch (event.getType()) {
		case PUSH:
			numOfCommits++;
			break;
		case ISSUES:
			if (((IssuesEvent) event).getAction().equals("closed")) {
				issueOpeningTime += ((IssuesEvent) event).getOpeningTime();
				numOfClosedIssues++;
			}
			break;
		case PULL_REQUEST:
			if (((PullRequestEvent) event).isMerged()) {
				pullMergedTime += ((PullRequestEvent) event).getMergedTime();
				numOfMergedPull++;
			}
			break;
		}
		developers.add(event.getActorId());
	}

	public String getOrg() {
		return org;
	}

	public String getRepoName() {
		return repoName;
	}

	public long getNumOfCommits() {
		return numOfCommits;
	}

	public double getAverageIssueOpeningTime() {
		return numOfClosedIssues == 0 || issueOpeningTime == 0 ? Double.POSITIVE_INFINITY
				: 1.0 * issueOpeningTime / numOfClosedIssues;
	}

	public double getAveragePullMergedTime() {
		return numOfMergedPull == 0 ? 0 : 1.0 * pullMergedTime / numOfMergedPull;
	}

	public double getRatioCommitPerDeveloper() {
		return developers.size() == 0 ? 0 : 1.0 * numOfCommits / developers.size();
	}

	public void setHealthScore(double healthScore) {
		this.healthScore = healthScore;
	}

	public double getHealthScore() {
		return healthScore;
	}

}
