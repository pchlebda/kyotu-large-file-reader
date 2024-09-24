package com.kyotu;

import java.util.List;
import java.util.Map;

public interface FileReader {

    Map<String, List<Temperature>> load(String file);

    long getLastFileChange(String file);
}
