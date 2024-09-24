package com.kyotu;

import java.util.List;

public interface TemperatureService {
    List<Temperature> findByCity(String city);
}
