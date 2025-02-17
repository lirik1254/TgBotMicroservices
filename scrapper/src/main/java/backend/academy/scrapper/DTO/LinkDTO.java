package backend.academy.scrapper.DTO;

import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public record LinkDTO(
    @NotEmpty String link,
    List<String> tags,
    List<String> filters
) {}
