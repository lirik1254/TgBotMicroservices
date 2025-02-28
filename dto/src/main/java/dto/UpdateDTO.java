package dto;

import java.util.List;

public record UpdateDTO(Long id, String url, String description, List<Long> tgChatIds) {}
