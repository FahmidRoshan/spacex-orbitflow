package com.orbitflow;

import com.orbitflow.repository.LaunchRepository;
import com.orbitflow.service.LaunchService;
import com.orbitflow.util.LaunchMapper;

public class OrbitFlow {

    public OrbitFlow() {
    }

    public void run() throws Exception {
        LaunchRepository repo = new LaunchRepository();
        LaunchMapper mapper = new LaunchMapper();
        LaunchService launchService = new LaunchService(repo, mapper);
        launchService.getAndStoreLaunchData();
    }

    public static void main(String[] args) throws Exception {
        new OrbitFlow().run();
    }
}
