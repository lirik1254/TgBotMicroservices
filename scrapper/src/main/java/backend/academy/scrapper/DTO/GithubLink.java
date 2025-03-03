package backend.academy.scrapper.DTO;

import backend.academy.scrapper.utils.LinkType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GithubLink extends Link {
    private LocalDateTime lastUpdate;

    public GithubLink(Long userId, String url, List<String> tags, List<String> filters, LocalDateTime lastUpdate) {
        super(userId, url, tags, filters);
        this.lastUpdate = lastUpdate;
    }

    @Override
    public LinkType getType() {
        return LinkType.GITHUB;
    }
}
