package backend.academy.scrapper.DTO;

import backend.academy.scrapper.utils.LinkType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StackOverflowLink extends Link {
    private Integer answerCount;

    public StackOverflowLink(Long userId, String url, List<String> tags, List<String> filters, Integer answerCount) {
        super(userId, url, tags, filters);
        this.answerCount = answerCount;
    }

    @Override
    public LinkType getType() {
        return LinkType.STACKOVERFLOW;
    }
}
