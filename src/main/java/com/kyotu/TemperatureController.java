package com.kyotu;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
class TemperatureController {

    private final FileService fileService;

    @GetMapping("/api/v1/temperature/{city}")
    ResponseEntity<?> getAverageTemperature(@PathVariable("city") String city) {
        var averageYearlyTemperature = fileService.findAverageYearlyTemperature(city);
        return averageYearlyTemperature.isEmpty() ? ResponseEntity.badRequest()
                .body(new NoResults("No results for " + city)) :
                ResponseEntity.ok(averageYearlyTemperature);
    }
}
