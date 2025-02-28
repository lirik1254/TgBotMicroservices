package dto;

import java.util.List;

public record LinkResponseDTO(int id, String url, List<String> tags, List<String> filters) {}
