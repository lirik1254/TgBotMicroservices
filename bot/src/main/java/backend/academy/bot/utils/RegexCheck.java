package backend.academy.bot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RegexCheck {

    private final String stackOverflowLinkPattern = "^https://(ru\\.|)stackoverflow\\.com/questions/\\d{1,10}$";

    private final String filterPattern = "^(\\w+:\\w+)(\\s+\\w+:\\w+)*\\s*$";

    private final String githubLinkPattern = "^https://github\\.com/[\\w-]{1,39}/[\\w-]{1,39}$";

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
