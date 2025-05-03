package com.orbitflow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbitflow.dto.LaunchData;
import com.orbitflow.dto.RowsUpdated;
import com.orbitflow.repository.LaunchRepository;
import com.orbitflow.util.LaunchMapper;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class LaunchService {

    String identifierName = "id";

    private final LaunchMapper launchMapper;
    private final LaunchRepository launchRepository;

    public LaunchService(LaunchRepository launchRepository, LaunchMapper launchMapper) {
        this.launchRepository = launchRepository;
        this.launchMapper = launchMapper;
    }


    public void getAndStoreLaunchData() throws Exception {

        RowsUpdated rowsUpdated = new RowsUpdated();
        List<LaunchData> data = getLaunchData();

        rowsUpdated.setRowsDeleted(launchRepository.removeData(identifierName, data));
        rowsUpdated.setRowsAdded(launchRepository.insertData(data));

        System.out.println("Total rows inserted/updated = " + rowsUpdated);
    }


    private List<LaunchData> getLaunchData() throws Exception{
        List<LaunchData> launchDataList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        String launchDataResponse = fetchLaunchData();
        JsonNode arrayNode = mapper.readTree(launchDataResponse);
        for (JsonNode node : arrayNode) {
            launchDataList.add(launchMapper.mapLaunchData(node));
        }
        return launchDataList;
    }


    private String fetchLaunchData() throws Exception{
        String apiUrl = "https://api.spacexdata.com/v4/launches";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        } catch (Exception e){
            throw new Exception("Error fetching data");
        }
    }
}
