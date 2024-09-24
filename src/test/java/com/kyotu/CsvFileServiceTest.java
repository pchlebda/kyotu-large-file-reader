package com.kyotu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CsvFileServiceTest {

    @Mock
    private FileReader fileReader;

    private CsvFileService csvFileService;

    private final String filePath = "src/test/resources/temperature.csv";

    @BeforeEach
    void setUp() {
        csvFileService = new CsvFileService(filePath, fileReader);
    }

    @Test
    void shouldReloadFileAfterChange() {
        //given
        Map<String, List<Temperature>> mockTemperature1 = new HashMap<>();
        List<Temperature> temperatures1 = Arrays.asList(
                new Temperature(2018, 25.4f),
                new Temperature(2019, 23.1f)
        );
        mockTemperature1.put("Warszawa", temperatures1);
        Map<String, List<Temperature>> mockTemperature2 = new HashMap<>();
        List<Temperature> temperatures2 = Arrays.asList(
                new Temperature(2018, 25.4f),
                new Temperature(2019, 23.1f),
                new Temperature(2020, 24.1f),
                new Temperature(2021, 22.2f)
        );
        mockTemperature2.put("Warszawa", temperatures2);

        when(fileReader.getLastFileChange(filePath)).thenReturn(1727178507L, 1727178892L);
        when(fileReader.load(filePath)).thenReturn(mockTemperature1, mockTemperature2);

        //when
        List<Temperature> result = csvFileService.findAverageYearlyTemperature("Warszawa");

        //then
        assertEquals(temperatures1, result);
        result = csvFileService.findAverageYearlyTemperature("Warszawa");
        assertEquals(temperatures2, result);

        verify(fileReader, times(2)).load(filePath);
    }


    @Test
    void shouldReturnEmptyWhenCityDoesNotExist() {
        // given
        Map<String, List<Temperature>> mockData = new HashMap<>();
        when(fileReader.load(filePath)).thenReturn(mockData);

        // when
        List<Temperature> result = csvFileService.findAverageYearlyTemperature("Unknown");

        // then
        assertTrue(result.isEmpty());
    }
}
