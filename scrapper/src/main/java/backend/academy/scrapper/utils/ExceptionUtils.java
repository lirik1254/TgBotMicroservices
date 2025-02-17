package backend.academy.scrapper.utils;

import java.util.Arrays;
import java.util.List;

public class ExceptionUtils {
    public static List<String> getStacktrace(Exception ex) {
        return Arrays.stream(ex.getStackTrace())
            .map(StackTraceElement::toString)
            .toList();
    }
}
