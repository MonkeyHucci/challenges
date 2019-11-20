package processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

import org.joda.time.DateTime;
import org.json.JSONObject;

import com.google.gson.Gson;

import model.IEvent;
import model.IssuesEvent;
import model.Project;
import model.PullRequestEvent;
import model.PushEvent;

public class EventsDownloader {

	private DateTime startDatetime;
	private DateTime endDatetime;

	private volatile Map<String, Project> projects = new LinkedHashMap<>();

	public EventsDownloader(DateTime startDatetime, DateTime endDatetime) {
		this.startDatetime = startDatetime;
		this.endDatetime = endDatetime;
	}

	private void loadEvents(DateTime datetime) {
		try {
			System.setProperty("http.agent", "Chrome");
			String urlString = String.format("https://data.gharchive.org/%d-%02d-%02d-%d.json.gz", datetime.getYear(),
					datetime.getMonthOfYear(), datetime.getDayOfMonth(), datetime.getHourOfDay());
			URL downloadURL = new URL(urlString);
			InputStream downloadStream = downloadURL.openStream();
			GZIPInputStream gzStream = new GZIPInputStream(downloadStream);
			InputStreamReader streamReader = new InputStreamReader(gzStream);
			BufferedReader bufferReader = new BufferedReader(streamReader);

			Gson gson = new Gson();
			String readingLine;
			while ((readingLine = bufferReader.readLine()) != null) {
				JSONObject jsonObject = new JSONObject(readingLine);
				IEvent event = null;
				switch (jsonObject.getString("type")) {
				case "PushEvent":
					event = gson.fromJson(readingLine, PushEvent.class);
					break;
				case "IssuesEvent":
					event = gson.fromJson(readingLine, IssuesEvent.class);
					break;
				case "PullRequestEvent":
					event = gson.fromJson(readingLine, PullRequestEvent.class);
					break;
				}
				if (event != null) {
					String repoName = event.getRepoName();
					if (!projects.containsKey(repoName)) {
						synchronized (projects) {
							if (!projects.containsKey(repoName)) {
								projects.put(repoName, new Project(repoName));
							}
						}
					}
					projects.get(repoName).addEvent(event);
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Project> run() {
		ExecutorService executor = Executors.newFixedThreadPool(8);
		List<Future<?>> futures = new LinkedList<>();
		for (DateTime datetime = startDatetime; datetime.isBefore(endDatetime)
				|| datetime.isEqual(endDatetime); datetime = datetime.plusHours(1)) {
			DateTime clonedDatetime = datetime.toDateTime();
			futures.add(executor.submit(new Runnable() {

				@Override
				public void run() {
					loadEvents(clonedDatetime);
				}
			}));
		}
		for (Future<?> future : futures) {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
		return new LinkedList<>(projects.values());
	}
}
