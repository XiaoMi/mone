package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liuchuankang
 * @Type M78CategoryFlowRel.java
 * @Desc
 * @date 2024/8/21 10:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_category_flow_rel")
public class M78CategoryFlowRel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id(keyType = KeyType.Auto)
	private Long id;

	/**
	 * 分类id
	 */
	@Column("cat_id")
	private Long catId;

	/**
	 * flow id
	 */
	@Column("flow_id")
	private Long flowId;

	/**
	 * 是否删除0-否 1-是
	 */
	private Integer deleted;
	@Column("create_time")

	private Date createTime;
	@Column("update_time")
	private Date updateTime;
}
