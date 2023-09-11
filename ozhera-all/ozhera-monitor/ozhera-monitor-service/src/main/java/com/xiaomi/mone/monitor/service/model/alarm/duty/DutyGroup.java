package com.xiaomi.mone.monitor.service.model.alarm.duty;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2023/6/8 2:06 下午
 */
@Data
public class DutyGroup implements Serializable {

    private Integer id;//id
    private String name;//子值班表（子告警组）名称，不允许重名
    private Integer rotation_type=0;//值班周期类型，0为天，1为周，2为自定义【默认值为0】
    private Integer shift_length=0;//自定义周期，RotationType=2时生效【默认值为0】
    private String shift_length_unit="";//自定义周期单位，days或weeks
    private Long duty_start_time;//值班开始时间,十位长度的时间戳
    private Long handoff_time;//值班交接时间,时间点，单位为秒数。如 43200=12:00
    private Integer preset_vacation=0;//标记是否预设假期值班，0=否，1=是。【默认值为0】当为是（=1）时，oncall_vacations字段不能为空
    private List<UserInfo> oncall_vacations;//假期值班信息。假期值班人员必须 为日常值班人员（oncall_users）的子集
    private List<UserInfo> oncall_users;//值班人列表信息

    private ShiftUserInfo duty_user;//当前值班人员
    private ShiftUserInfo next_duty_user;//下期值班人员
    private List<ShiftUserInfo> duty_order_users;//实际值班表

}
