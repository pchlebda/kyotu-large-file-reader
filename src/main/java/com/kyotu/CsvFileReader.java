package com.kyotu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@Slf4j
@Component
public class CsvFileReader implements FileReader {

    private static final int CITY_INDEX = 0;
    private static final int DATE_INDEX = 1;
    private static final int TEMPERATURE_INDEX = 2;

    private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public Map<String, List<Temperature>> load(String file) {
        log.info("Loading file {}", file);
        Map<String, List<Temperature>> result = new HashMap<>();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);

        List<CityTemperature> cityTemperatures = readCityTemperature(file);
        log.info("Found {} entries in the file {}", cityTemperatures.size(), file);
        Map<String, List<CityTemperature>> groupedByCity = cityTemperatures.stream()
                .collect(Collectors.groupingBy(CityTemperature::city));

        groupedByCity.forEach((cityX, cityTemperaturesX) -> {
            Map<Integer, List<CityTemperature>> byYear = cityTemperaturesX.stream()

                    .collect(Collectors.groupingBy(CityTemperature::year));
            byYear.forEach((yearY, cityTemperaturesY) -> {
                double avgTemperature = cityTemperaturesY.stream().mapToDouble(CityTemperature::temperature).average()
                        .getAsDouble();
                List<Temperature> temperatures = result.computeIfAbsent(cityX, s -> new ArrayList<>());
                temperatures.add(new Temperature(yearY, Float.parseFloat(df.format(avgTemperature))));
            });
        });
        return result;
    }

    @Override
    public long getLastFileChange(String file) {
        return new File(file).lastModified();
    }

    private List<CityTemperature> readCityTemperature(String file) {
        List<CityTemperature> cityTemperatures = new ArrayList<>();
        String line = null;
        try (Scanner sc = new Scanner(new FileInputStream(ResourceUtils.getFile(file)))) {
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                String[] array = line.split(";");
                if (array.length != 3) {
                    log.error("Invalid line '{}' format in file {}", line, file);
                    throw new ParsingException();
                }
                String lineCity = array[CITY_INDEX];
                int lineYear = LocalDateTime.parse(array[DATE_INDEX], DATE_TIME_FORMATTER).toLocalDate().getYear();
                float lineTemp = Float.parseFloat(array[TEMPERATURE_INDEX]);
                CityTemperature cityTemperature = new CityTemperature(lineCity, lineYear, lineTemp);
                cityTemperatures.add(cityTemperature);
            }
        } catch (DateTimeParseException e) {
            log.error("Error parsing line {} in file {}", line, file, e);
            throw new ParsingException();
        } catch (FileNotFoundException e) {
            log.error("Missing expected {} file.", file);
            throw new MissingCsvFileException();
        }
        return cityTemperatures;
    }

    private record CityTemperature(String city, int year, float temperature) {

    }
}
