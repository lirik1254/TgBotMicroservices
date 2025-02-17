package backend.academy.bot.utils;

import lombok.experimental.UtilityClass;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class RegexCheck {

    private final String stackOverflowLinkPattern = "^(https://|http://|)" +
            "api\\.stackexchange\\.com/2\\.[0-5]/questions/\\d{1,10}/answers\\?site=(ru\\.|)stackoverflow(\\.com|)$";

    private final String filterPattern = "^(\\w+:\\w+)(\\s+\\w+:\\w+)*\\s*$";

    private final String githubLinkPattern = "^(https://|http://|)api\\.github\\.com/repos/\\w{0,39}/\\w{0,39}";

    public boolean checkFilter(String filterText) {
        Pattern pattern = Pattern.compile(filterPattern);
        Matcher matcher = pattern.matcher(filterText);

        return matcher.find();
    }

    public boolean checkApi(String url) {
        Pattern stackOverflowPattern = Pattern.compile(stackOverflowLinkPattern);
        Pattern githubPattern = Pattern.compile(githubLinkPattern);

        Matcher stackOverflowMatcher = stackOverflowPattern.matcher(url);
        Matcher githubMatcher = githubPattern.matcher(url);

        return stackOverflowMatcher.find() || githubMatcher.find();
    }

}
