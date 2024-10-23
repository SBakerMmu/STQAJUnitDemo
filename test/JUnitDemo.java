import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

//Does not need a class attribute to indicate contains tests, but can Tag at class level
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("JUnitDemo")
class JUnitDemo {

    //Methods annotated with @BeforeAll is run once
    @BeforeAll
    static void initAll() {

    }

    @BeforeEach
    void init(TestReporter testReporter, TestInfo info) {
        testReporter.publishEntry("Methods annotated with @BeforeEach run before each test");
        testReporter.publishEntry(String.format("Method %s DisplayName %s",  info.getTestMethod(), info.getDisplayName()));
    }

    @Test
    void succeedingTest() {
    }
    @Test
    @DisplayName("This is a failing test")
    void failingTest() {
        fail("a failing test");
    }
    @Test
    @Disabled("for demonstration purposes")
    void skippedTest() {
        // not executed
    }
    @Test
    void abortedTest() {
        assumeTrue("abc".contains("Z"));
        fail("test should have been aborted");
    }

    @RepeatedTest(3)
    @DisplayName("This is a repeating test")
    void repeatingTest(TestReporter testReporter, TestInfo info, RepetitionInfo rpt) {
        testReporter.publishEntry(String.format("Method %s DisplayName %s Repeat %d",  info.getTestMethod(), info.getDisplayName(), rpt.getCurrentRepetition()));
    }

    @Test
        //As we have specified a display name generator, we can create readable names from _ separated method names. Note the USE acronym, Unit, Scenario, Expected Result
    void unit_scenario_expected() {
    }

    //Parameterised test with simplest argument source, which is a @ValueSource, which supports Java primitives
    @ParameterizedTest
    @ValueSource(strings = { "racecar", "radar", "able was I ere I saw elba" })
    void parameterizedTest_valueSource(String candidate) {
        StringBuilder reversed = new StringBuilder(candidate).reverse();
        assertEquals(reversed.toString(), candidate);
    }

    //Parameterised test with custom method source
    @ParameterizedTest
    @MethodSource("methodSource")
    void parameterizedTest_methodSource(String candidate) {
        StringBuilder reversed = new StringBuilder(candidate).reverse();
        assertEquals(reversed.toString(), candidate);
    }

    static Stream<String> methodSource()
    {
        return Stream.<String>builder()
                .add("racecar")
                .add("radar")
                .add("able was I ere I saw elba")
                .build();
    }

    //Parameterized test with custom methods source
    @ParameterizedTest
    @MethodSource("stringIntAndListProvider")
    void parameterizedTest_methodSource(String str, int num, List<String> list) {
        assertEquals(5, str.length());
        assertTrue(num >=1 && num <=2);
        assertEquals(2, list.size());
    }

    static Stream<Arguments> stringIntAndListProvider() {
        return Stream.of(
                arguments("apple", 1, Stream.<String>builder().add("a").add("b").build().toList()),
                arguments("lemon", 2, Stream.<String>builder().add("x").add("y").build().toList())
        );
    }



    @ParameterizedTest(name = "[{index}] {0} {1}")
    @CsvSource(textBlock = """
    MONDAY,      'Monday'
    TUESDAY,     'Tuesday'
    WEDNESDAY,   'Wednesday'
    THURSDAY,    'Thursday'
    FRIDAY,      'Friday'
    SATURDAY,    'Saturday'
    SUNDAY,       'Sunday'
    """)
    void parameterizedTest_csvSource(String day, String name) {

        assertDayOfWeek(DayOfWeek.valueOf(day), name);
    }


    void assertDayOfWeek(DayOfWeek day, String name) {
        assertTrue(day.toString().equalsIgnoreCase(name));
    }



    //Parameterised test with null source
    @ParameterizedTest
    @NullSource
    void parameterizedTest_nullSource(String candidate) {
        assertNull(candidate);
    }

    //Parameterised test with empty source
    @ParameterizedTest
    @EmptySource
    void parameterizedTest_emptySource(String candidate) {
        assertSame("", candidate);

    }

    //Parameterised test with empty source,
    //Supports java.util.Collection and concrete subtypes thereof,
    //java.util.List, java.util.Set, java.util.SortedSet, java.util.NavigableSet, java.util.Map (and concrete subtypes thereof), java.util.SortedMap, java.util.NavigableMap, primitive arrays and object arrays
    @ParameterizedTest
    @EmptySource
    void parameterizedTest_emptySource(List<String> candidate) {
        assertLinesMatch( Collections.emptyList(), candidate);

    }

    //Parameterised test with Enum source. Note use of assertAll to group assertions so that method does not fail on first assert
    @ParameterizedTest
    @EnumSource(DayOfWeek.class)
    void testDayIsWeekday(DayOfWeek day) {
        assertAll(
                () -> assertEquals(DayOfWeek.MONDAY, day),
                () -> assertEquals(DayOfWeek.TUESDAY, day),
                () -> assertEquals(DayOfWeek.WEDNESDAY, day),
                () -> assertEquals(DayOfWeek.THURSDAY, day),
                () -> assertEquals(DayOfWeek.FRIDAY, day),
                () -> assertEquals(DayOfWeek.SATURDAY, day),
                () -> assertEquals(DayOfWeek.SUNDAY, day)
        );
    }



    @AfterEach
    void tearDown(TestReporter testReporter, TestInfo info) {
        testReporter.publishEntry("Methods annotated with @AfterEach run after each test");
        testReporter.publishEntry(String.format("Method %s DisplayName %s", info.getTestMethod(), info.getDisplayName()));
    }



    @AfterAll
    static void tearDownAll() {
    }


}
