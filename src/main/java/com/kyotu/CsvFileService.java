package com.kyotu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CsvFileService implements FileService {

    private final String filePath;
    private long lastUpdate;
    private FileReader fileReader;
    private Map<String, List<Temperature>> temperatureByCity;

    public CsvFileService(@Value("${temperature.file}") String filePath, FileReader fileReader) {
        this.filePath = filePath;
        this.fileReader = fileReader;
        this.lastUpdate = fileReader.getLastFileChange(filePath);
    }

    private boolean hasFileChangedSince(long lastChange) {
        return fileReader.getLastFileChange(filePath) > lastChange;
    }

    @Override
    public List<Temperature> findAverageYearlyTemperature(String city) {
        if (temperatureByCity == null || hasFileChangedSince(lastUpdate)) {
            this.temperatureByCity = fileReader.load(filePath);
        }
        return temperatureByCity.getOrDefault(city, new ArrayList<>());
    }
}
