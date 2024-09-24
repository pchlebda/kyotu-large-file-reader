package com.kyotu;

import java.util.List;

public interface FileService {
    List<Temperature> findAverageYearlyTemperature(String city);
}
