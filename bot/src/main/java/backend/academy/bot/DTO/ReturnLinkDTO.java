package backend.academy.bot.DTO;

import java.util.ArrayList;

public record ReturnLinkDTO(
    ArrayList<LinkDTO> links,
    int size
) {
    @Override
    public String toString() {
        StringBuilder templateMessage = new StringBuilder(String.format("Кол-во отслеживаемых ссылок: %d\n\n" +
            "Отслеживаемые ссылки:\n", size));
        for (int i = 0; i < links.size(); i++) {
            templateMessage.append("\n").append(i + 1).append(") ").append(links.get(i).toString()).append("\n");
        }
        return templateMessage.toString();
    }
}
