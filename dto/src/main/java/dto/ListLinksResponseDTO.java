package dto;

import java.util.ArrayList;

public record ListLinksResponseDTO(ArrayList<LinkDTO> links, int size) {}
