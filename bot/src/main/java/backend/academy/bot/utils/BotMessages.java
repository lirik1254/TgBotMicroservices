package backend.academy.bot.utils;

public class BotMessages {
    public final static String startMessage = """
        Приветствую в боте, ты успешно зарегистрирован!

        Этот бот поможет тебе отслеживать контент.
        Чтобы ознакомиться с функционалом бота, введи введи /help""";

    public final static String helpMessage = """
        Доступные команды:
        /start - Регистрация
        /help - подробно поясняет что делает каждая команда
        /track - начинает уведомления по какой-то ссылке
        /untrack - прекращает отслеживание какой-либо ссылки
        /list - показывает список отслеживаемых ссылок (список ссылок, полученных при /track""";

    public final static String wrongCommand = "Неизвестная команда";

    public final static String wrongMessageType = "Можно вводить только текст!";
}
