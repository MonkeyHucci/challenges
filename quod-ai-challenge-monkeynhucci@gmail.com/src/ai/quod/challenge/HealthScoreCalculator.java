package ai.quod.challenge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import model.Project;
import processor.EventsDownloader;

public class HealthScoreCalculator {

	public static void main(String[] args) {
		DateTime startDatetime = DateTime.parse(args[0]);
		DateTime endDatetime = DateTime.parse(args[1]);

		EventsDownloader eventsDownloader = new EventsDownloader(startDatetime, endDatetime);
		List<Project> projects = eventsDownloader.run();

		long maxNumOfCommits = projects.stream().max(new Comparator<Project>() {

			@Override
			public int compare(Project p1, Project p2) {
				return Long.compare(p1.getNumOfCommits(), p2.getNumOfCommits());
			}
		}).get().getNumOfCommits();

		double minAverageIssueOpeningTime = projects.stream().min(new Comparator<Project>() {

			@Override
			public int compare(Project p1, Project p2) {
				return Double.compare(p1.getAverageIssueOpeningTime(), p2.getAverageIssueOpeningTime());
			}
		}).get().getAverageIssueOpeningTime();

		double maxAveragePullMergedTime = projects.stream().max(new Comparator<Project>() {

			@Override
			public int compare(Project p1, Project p2) {
				return Double.compare(p1.getAveragePullMergedTime(), p2.getAveragePullMergedTime());
			}
		}).get().getAveragePullMergedTime();

		double maxRatioCommitPerDeveloper = projects.stream().max(new Comparator<Project>() {

			@Override
			public int compare(Project p1, Project p2) {
				return Double.compare(p1.getRatioCommitPerDeveloper(), p2.getRatioCommitPerDeveloper());
			}
		}).get().getRatioCommitPerDeveloper();

		projects.stream()
				.forEach(p -> p.setHealthScore(p.getNumOfCommits() / maxNumOfCommits
						+ minAverageIssueOpeningTime / p.getAverageIssueOpeningTime()
						+ p.getAveragePullMergedTime() / maxAveragePullMergedTime
						+ p.getRatioCommitPerDeveloper() / maxRatioCommitPerDeveloper));

		List<Project> sortedProjects = projects.stream().sorted(new Comparator<Project>() {

			@Override
			public int compare(Project p1, Project p2) {
				return -Double.compare(p1.getHealthScore(), p2.getHealthScore());
			}
		}).collect(Collectors.toList());

		try (PrintWriter writer = new PrintWriter(new File("health_scores.csv"))) {
			StringBuilder sb = new StringBuilder();
			sb.append(
					"org,repo_name,health_score,num_commits,average_time_issue_opened,average_time_pull_request_get_merged,ratio_commit_per_developers\n");
			for (int i = 0; i < 1000; i++) {
				Project project = sortedProjects.get(i);
				sb.append(String.format("%s,%s,%f,%d,%f,%f,%f\n", project.getOrg(), project.getRepoName(),
						project.getHealthScore(), project.getNumOfCommits(), project.getAverageIssueOpeningTime(),
						project.getAveragePullMergedTime(), project.getRatioCommitPerDeveloper()));
			}

			writer.write(sb.toString());

			System.out.println("done!");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
