package com.orbitflow.repository;

import com.orbitflow.config.DatabaseManager;
import com.orbitflow.dto.LaunchData;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class LaunchRepository {

    public int insertData(List<LaunchData> data) throws Exception {
        if (data == null || data.isEmpty()) return 0;

        List<String> headers = new ArrayList<>(data.get(0).getColumnData().keySet());
        String placeholders = headers.stream().map(header -> "?").collect(Collectors.joining(", "));


            StringBuilder queryBuilder = new StringBuilder();
        queryBuilder
                .append("INSERT INTO spacex_launches (")
                .append( String.join(", ", headers))
                .append(") VALUES (")
                .append(placeholders)
                .append(")");

        try (Connection connection = DatabaseManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());

            for (LaunchData dto: data){
                Map<String, Object> rowData = dto.getColumnData();
                for (int i =0 ; i < headers.size(); i++){
                    Object value = rowData.get(headers.get(i));
                    statement.setObject(i + 1, value);
                }
                statement.addBatch();
            }
            return getInsertedRowCount(statement.executeBatch());

        }

    }

    final int fermatNumber = 65535;

    public int removeData(String identifierName, List<LaunchData> data) throws Exception {

        List<Object> identifierValues = getIdentifierValues(identifierName,data);

        int rowsUpdated = 0;
        assert identifierName != null;
        if (identifierValues == null || identifierValues.isEmpty()) {
            return 0;
        }

        for(int j = 0; j < identifierValues.size(); j = j + fermatNumber){
            int identifierCursor = min( fermatNumber + j, identifierValues.size());
            if (identifierCursor == 0) {
                rowsUpdated = 0;
                break;
            }

            String placeholders = identifierValues.stream().map(header -> "?").collect(Collectors.joining(", "));

            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder
                    .append("DELETE FROM spacex_launches")
                    .append("\nWHERE")
                    .append("\n\t " + identifierName + " IN (" + placeholders + " )");

            try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {
                    for (int i = j; i < identifierCursor; i++) {
                        preparedStatement.setObject(i - j + 1, identifierValues.get(i));
                    }
                    rowsUpdated += preparedStatement.executeUpdate();
            }
        }
        return rowsUpdated;



    }


    private List<Object> getIdentifierValues(String identifierName, List<LaunchData> data){

        List<Object> identifierValues = new ArrayList<>();
        for (LaunchData rowData: data){
            if(!rowData.getColumnData().isEmpty() && rowData.getColumnData().containsKey(identifierName)){
            identifierValues.add(rowData.getColumnData().get(identifierName));
            }
        }
        return identifierValues;
    }

    private int getInsertedRowCount(int[] results) {
        int count = 0;
        for (int r : results) {
            if (r != Statement.EXECUTE_FAILED) {
                count += (r == Statement.SUCCESS_NO_INFO) ? 0 : r;
            }
        }
        return count;
    }

}
