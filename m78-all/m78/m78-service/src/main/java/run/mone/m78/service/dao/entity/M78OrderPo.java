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
import java.sql.Timestamp;
import java.util.Date;

import static run.mone.m78.api.constant.TableConstant.IM_USER_TABLE;
import static run.mone.m78.api.constant.TableConstant.ORDER_TABLE;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(ORDER_TABLE)
public class M78OrderPo implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("gmt_create")
    private Date gmtCreate;

    @Column("gmt_modified")
    private Date gmtModified;

    @Column("account")
    private String account;

    @Column("package_id")
    private Integer packageId;

    //价格
    @Column("price")
    private String price;

    //实际价值-加积分按此值来加
    @Column("price_value")
    private String priceValue;

    //订单状态0:未支付, 1:支付成功-待充积分, 2:z积分充值完成, 11:支付失败, 12:z积分充值失败
    @Column("status")
    private Integer status;

    //支付类型0:微信, 1:支付宝, 2:其它
    @Column("pay_type")
    private Integer payType;

    @Column("pay_sub_type")
    private String paySubType;

    //支付失效时间，单位秒，默认7200
    @Column("expire")
    private Integer expire;

    //xor订单id
    @Column("xor_order_id")
    private String xorOrderId;

}
