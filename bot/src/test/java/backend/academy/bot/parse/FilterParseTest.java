package backend.academy.bot.parse;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.bot.utils.RegexCheck;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FilterParseTest {

    @Test
    @DisplayName("Тестирует парсинг фильтров")
    public void parseFilterTest() {
        String filterWithoutColon = "MyFilter";
        String filterWithSpaces = "   MyFilter:Filter  ";
        String moreThanOneFilterNoColon = "Myfilter:Filter filter&filter";
        String filtersWithComma = "filter:filter, filter:comma";

        String oneCorrectFilter = "filter:filter";
        String moreThanOneCorrectFilter = "filter:filter correctfilter:FILTER52";

        assertFalse(RegexCheck.checkFilter(filterWithoutColon));
        assertFalse(RegexCheck.checkFilter(filterWithSpaces));
        assertFalse(RegexCheck.checkFilter(moreThanOneFilterNoColon));
        assertFalse(RegexCheck.checkFilter(filtersWithComma));

        assertTrue(RegexCheck.checkFilter(oneCorrectFilter));
        assertTrue(RegexCheck.checkFilter(moreThanOneCorrectFilter));
    }
}
