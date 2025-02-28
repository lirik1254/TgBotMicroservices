package backend.academy.bot.utils;

import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionUtils {
    public static List<String> getStacktrace(Exception ex) {
        return Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
    }
}
