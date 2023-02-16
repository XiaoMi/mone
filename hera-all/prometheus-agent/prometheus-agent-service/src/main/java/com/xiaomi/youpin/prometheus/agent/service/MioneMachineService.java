package com.xiaomi.youpin.prometheus.agent.service;

import com.xiaomi.youpin.prometheus.agent.domain.Ips;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MioneMachineService {

    @Value("${mione.machine.port}")
    private String machinePort;
    @Value("${mione.container.port}")
    private String containerPort;

    public List<Ips> queryMachineList(String type) {
        return null;
    }
}
