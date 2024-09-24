package com.kyotu;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class LargeFileReaderApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAverageTemperature_CityExists() throws Exception {
        var temperatures = List.of(new Temperature(2018, 27.4f), new Temperature(2019, 12.7f),
                new Temperature(2020, 39.3f));

        mockMvc.perform(get("/api/v1/temperature/Warszawa")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(temperatures)));
    }

    @Test
    void testGetAverageTemperature_CityDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/temperature/NonExistentCity")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"No results for NonExistentCity\"}"));
    }
}
