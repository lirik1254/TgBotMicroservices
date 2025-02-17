package backend.academy.scrapper.DTO;

import java.util.ArrayList;
import java.util.List;

public record UpdateDTO(
    Long id,
    String url,
    String description,
    List<Long> tgChatIds
) {}
