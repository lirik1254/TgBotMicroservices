package backend.academy.bot.DTO;

import java.util.ArrayList;

public record UpdateDTO(
    Long id,
    String url,
    String description,
    ArrayList<Long> tgChatIds
) {}
