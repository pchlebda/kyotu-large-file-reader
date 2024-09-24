package com.kyotu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CsvFileReaderTest {

    private CsvFileReader csvFileReader;

    @BeforeEach
    void setUp() {
        this.csvFileReader = new CsvFileReader();
    }

    @Test
    void testLoadFileSuccess() {
        var warsawExpectedTemperature = List.of(new Temperature(2018, 27.4f), new Temperature(2019, 12.7f),
                new Temperature(2020, 39.3f));
        var lodzExpectedTemperature = List.of(new Temperature(2022, 32.4f), new Temperature(2023, 2.2f));

        Map<String, List<Temperature>> result = csvFileReader.load("src/test//resources/min_file.csv");

        assertEquals(2, result.size());
        assertTrue(result.containsKey("Warszawa"));
        assertTrue(result.containsKey("Łódź"));

        List<Temperature> warsawTemperatures = result.get("Warszawa");
        assertEquals(warsawExpectedTemperature, warsawTemperatures);
        List<Temperature> lodzTemperatures = result.get("Łódź");
        assertEquals(lodzExpectedTemperature, lodzTemperatures);
    }

    @ValueSource(strings = {"invalid_file.csv", "invalid_date_format_file.csv"})
    @ParameterizedTest
    void shouldThrowExceptionWhenInvalidContent(String file) {
        assertThrows(ParsingException.class,
                () -> csvFileReader.load("src/test/resources/" + file));
    }

    @Test
    void testLAstModified() {
        assertTrue(csvFileReader.getLastFileChange("src/test//resources/min_file.csv") > 0);
    }
}
