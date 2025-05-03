package com.orbitflow.util;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbitflow.dto.LaunchData;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LaunchMapper {

    public LaunchData mapLaunchData(JsonNode rawNode) throws Exception {

        Map<String, Object> mappedData = new HashMap<>();
        Map<String, Map<String, String>>  columnFields= launchDataFields();
        for (String apiField : columnFields.keySet()) {
            if (rawNode.has(apiField)) {
                String key = columnFields.get(apiField).get("columnName");
                String dataType = columnFields.get(apiField).get("columnType");
                if(key == null || dataType == null){
                    throw new Error("ColumnDetails not found");
                }
                String value = (rawNode.get(apiField) == null || rawNode.get(apiField).asText().equals("null")) ? (dataType.equals("text") ? "": null): rawNode.get(apiField).asText();
                Object castedValue = castValueToType(value, dataType);
                mappedData.put(key, castedValue);
            }
        }
        return new LaunchData(mappedData);
    }


    private Map<String, Map<String, String>> launchDataFields() throws Exception {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream input =  Thread
                    .currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("mappings/mapper.json");
            if (input == null) {
                throw new IOException("Column Data Input Details file not found");
            }
            return objectMapper.readValue(input, new TypeReference<>() {});

        } catch (Exception e) {
            throw new Exception("Failed to load column data resource! ", e);
        }
    }


    public Object castValueToType(String value, String type) {
        if (value == null) {
            return null;
        }
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyyMMdd"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .parseCaseInsensitive()
                .toFormatter(Locale.ENGLISH);

        return switch (type.toLowerCase()) {
            case "int", "integer" -> Integer.parseInt(value);
            case "bigint" -> Long.parseLong(value);
            case "smallint" -> Short.parseShort(value);
            case "tinyint" -> Byte.parseByte(value);
            case "float" -> Float.parseFloat(value);
            case "double" -> Double.parseDouble(value);
            case "decimal", "numeric" -> new BigDecimal(value);
            case "boolean", "bool" -> Boolean.parseBoolean(value);
            case "date" -> {
                LocalDate localDate = LocalDate.parse(value, formatter);
                yield Date.valueOf(localDate);
            }
            case "time" -> Time.valueOf(value);
            case "datetime", "timestamp" -> {
                DateTimeFormatter dtf= new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSSVV").toFormatter();
                LocalDateTime dateTime = LocalDateTime.parse(value, dtf);
                yield java.sql.Timestamp.valueOf(dateTime);
            }
            case "varchar", "char", "text" -> value;
            default -> value;
        };
    }
}
