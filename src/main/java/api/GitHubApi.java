package api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

public class GitHubApi {

    public String getLatestCommitSha(String username, String repoName, String accessToken) {
        Response response = RestAssured.given()
                .auth().oauth2(accessToken)
                .get("https://api.github.com/repos/" + username + "/" + repoName + "/git/refs/heads/main");

        return response.jsonPath().getString("object.sha");
    }

    public void commitFile(String username, String repoName, String accessToken, String filePath, String latestCommitSha) {
        // Define commit message
        String commitMessage = "Automated commit via GitHub API";

        // Define file content (base64 encoded)
        String fileContent = "This is a new line in the file".getBytes().toString();

        // Create the commit JSON object
        JSONObject commitObject = new JSONObject();
        commitObject.put("message", commitMessage);
        commitObject.put("parents", new String[]{latestCommitSha});
        commitObject.put("tree", new JSONObject().put("path", filePath)
                .put("mode", "100644")
                .put("type", "blob")
                .put("content", fileContent));

        // Post the commit to the GitHub API
        RestAssured.given()
                .auth().oauth2(accessToken)
                .body(commitObject.toString())
                .post("https://api.github.com/repos/" + username + "/" + repoName + "/git/commits");

        System.out.println("Commit created via API!");
    }
}
