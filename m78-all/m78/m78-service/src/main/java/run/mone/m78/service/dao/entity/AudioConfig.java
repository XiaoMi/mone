package run.mone.m78.service.dao.entity;

import lombok.Data;

/**
 * @author liuchuankang
 * @Type AudioConfig.java
 * @Desc 语音通话
 * @date 2024/9/5 15:52
 */
@Data
public class AudioConfig {

	//语音通话开关 0-关闭 1-开启
	private Integer callSwitch;

	private String asrVendor;
}
