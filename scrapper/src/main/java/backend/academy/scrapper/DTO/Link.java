package backend.academy.scrapper.DTO;

import backend.academy.scrapper.utils.LinkType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Link {
    protected Long userId;
    protected String url;
    protected List<String> tags;
    protected List<String> filters;

    public abstract LinkType getType();
}
