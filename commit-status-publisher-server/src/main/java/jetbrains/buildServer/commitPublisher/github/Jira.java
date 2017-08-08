import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

import java.net.URI;

/**
 * Get all issues based on the search filter
 */
public class Jira {
    static JiraRestClient restClient;

    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.trustStore", "C:/Demo/myTrustStore");

        URI jiraServerUri = URI.create("https://jira.localhost.com/");

        AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

        AuthenticationHandler auth = new BasicHttpAuthenticationHandler("username", "password");
        restClient = factory.create(jiraServerUri, auth);

        // Any valid search filter(JQL)
        String jql = "project = SAMPLE and reporter = Purus";

        int maxPerQuery = 100;
        int startIndex = 0;

        try {
            SearchRestClient searchRestClient = restClient.getSearchClient();

            while (true) {
                Promise<SearchResult> searchResult = searchRestClient.searchJql(jql, maxPerQuery, startIndex, null);
                SearchResult results = searchResult.claim();

                for (Issue issue : results.getIssues()) {
                    System.out.println(issue.getKey());
                }

                if (startIndex >= results.getTotal()) {
                    break;
                }

                startIndex += maxPerQuery;

                System.out.println("Fetching from Index: " + startIndex);
            }

        } finally {
            restClient.close();
        }

    }
}