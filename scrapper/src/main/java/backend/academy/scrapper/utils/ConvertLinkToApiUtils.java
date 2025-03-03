package backend.academy.scrapper.utils;

import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("MagicNumber")
public class ConvertLinkToApiUtils {
    public String convertGithubLinkToApi(String link) {
        String[] linkParts = link.split("/");

        String owner = linkParts[3];
        String repoName = linkParts[4];

        return String.format("https://api.github.com/repos/%s/%s", owner, repoName);
    }

    public String convertStackOverflowLinkToApi(String link) {
        String[] linkParts = link.split("/");

        String site = linkParts[2];
        String questionId = linkParts[4];

        return String.format("https://api.stackexchange.com/2.3/questions/%s/answers?site=%s", questionId, site);
    }
}
