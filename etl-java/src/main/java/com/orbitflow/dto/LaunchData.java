package com.orbitflow.dto;

import java.util.Map;
import lombok.Data;

@Data
public class LaunchData {

    private Map<String, Object> columnData;

    public LaunchData(Map<String, Object> columnData) {
        this.columnData = columnData;
    }
}
