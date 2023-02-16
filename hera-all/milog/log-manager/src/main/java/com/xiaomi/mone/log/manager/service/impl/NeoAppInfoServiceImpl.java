package com.xiaomi.mone.log.manager.service.impl;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.manager.model.dto.PodDTO;
import com.xiaomi.mone.log.manager.model.dto.RegionDTO;
import com.xiaomi.mone.log.manager.model.dto.ZoneDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogRegionAvailableZoneDO;
import com.xiaomi.mone.log.manager.service.NeoAppInfoService;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.JsonNode;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangping17
 * @date 2021-10-15
 */
@Service
@Slf4j
public class NeoAppInfoServiceImpl implements NeoAppInfoService {

    @Resource
    private RegionAvailableZoneServiceImpl regionAvailableZoneService;

    @Value("$app.env")
    private String env;

    private static final String NOT_FOUND_CODE = "100032404";

    @Override
    public List<RegionDTO> getNeoAppInfo(List<String> treeIds) {

        return Lists.newArrayList();
    }

    private void buildRegionZonePod(JsonNode p, List<RegionDTO> regionDTOList) {
        if (p.get("ready") == null || !"true".equals(p.get("ready").toString())) {
            return;
        }
        String zoneEN = p.get("idc").toString().substring(1, p.get("idc").toString().length() - 1);
        //取region
        MilogRegionAvailableZoneDO regionZone = regionAvailableZoneService.getRegionAndZone(zoneEN);
        if (regionZone == null) {
            return;
        }
        String zoneCN = regionZone.getZoneNameCN();
        String regionEN = regionZone.getRegionNameEN();
        String regionCN = regionZone.getRegionNameCN();
        String podName = p.get("podName").toString().substring(1, p.get("podName").toString().length() - 1);
        String podIP = p.get("podIP").toString().substring(1, p.get("podIP").toString().length() - 1);
        String nodeName = p.get("nodeName").toString().substring(1, p.get("nodeName").toString().length() - 1);
        String nodeIP = p.get("nodeIP").toString().substring(1, p.get("nodeIP").toString().length() - 1);//System.getenv("host.ip") == null ? NetUtils.getLocalHost() : System.getenv("host.ip");
        boolean regionExist = false;
        for (RegionDTO regionDTO : regionDTOList) {
            if (regionEN.equals(regionDTO.getRegionNameEN())) {
                List<ZoneDTO> zoneDTOList = regionDTO.getZoneDTOList();
                if (zoneDTOList != null && !zoneDTOList.isEmpty()) {
                    boolean zoneExist = false;
                    for (ZoneDTO zoneDTO : zoneDTOList) {
                        if (zoneEN.equals(zoneDTO.getZoneNameEN())) {
                            zoneExist = true;
                            PodDTO podDTO = PodDTO.builder().podIP(podIP).podName(podName).nodeIP(nodeIP).nodeName(nodeName).build();//new PodDTO();
                            List<PodDTO> podDTOList = zoneDTO.getPodDTOList();
                            if (podDTO == null) {
                                podDTOList = new ArrayList<>();
                            }
                            podDTOList.add(podDTO);
                            podDTOList = podDTOList.stream().distinct().collect(Collectors.toList());
                            zoneDTO.setPodDTOList(podDTOList);
                            break;
                        }
                    }
                    if (!zoneExist) {
                        ZoneDTO zoneDTO = buildZone(zoneEN, zoneCN, podIP, podName, nodeIP, nodeName);
                        regionDTO.getZoneDTOList().add(zoneDTO);
                    }
                } else {
                    ZoneDTO zoneDTO = buildZone(zoneEN, zoneCN, podIP, podName, nodeIP, nodeName);
                    zoneDTOList = new ArrayList<>();
                    zoneDTOList.add(zoneDTO);
                    regionDTO.setZoneDTOList(zoneDTOList);
                }

                regionExist = true;
                break;
            }
        }
        if (!regionExist) {
            RegionDTO regionDTO = RegionDTO.builder().regionNameEN(regionEN).regionNameCN(regionCN).build();
            ZoneDTO zoneDTO = buildZone(zoneEN, zoneCN, podIP, podName, nodeIP, nodeName);
            List<ZoneDTO> zoneDTOList = new ArrayList<>();
            zoneDTOList.add(zoneDTO);
            regionDTO.setZoneDTOList(zoneDTOList);
            regionDTOList.add(regionDTO);
        }
    }

    private ZoneDTO buildZone(String zoneEN, String zoneCN, String podIP, String podName, String nodeIP, String nodeName) {
        ZoneDTO zoneDTO = ZoneDTO.builder().zoneNameEN(zoneEN).zoneNameCN(zoneCN).build();
        List<PodDTO> podDTOList = buildPodList(podIP, podName, nodeIP, nodeName);
        zoneDTO.setPodDTOList(podDTOList);
        return zoneDTO;
    }

    private List<PodDTO> buildPodList(String podIP, String podName, String nodeIP, String nodeName) {
        PodDTO podDTO = PodDTO.builder().podIP(podIP).podName(podName).nodeIP(nodeIP).nodeName(nodeName).build();
        List<PodDTO> podDTOList = new ArrayList<>();
        podDTOList.add(podDTO);
        return podDTOList;
    }

}
