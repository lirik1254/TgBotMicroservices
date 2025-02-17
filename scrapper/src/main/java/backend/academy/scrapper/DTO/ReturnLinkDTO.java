package backend.academy.scrapper.DTO;

import java.util.ArrayList;

public record ReturnLinkDTO(
    ArrayList<LinkDTO> links,
    int size
) {}
