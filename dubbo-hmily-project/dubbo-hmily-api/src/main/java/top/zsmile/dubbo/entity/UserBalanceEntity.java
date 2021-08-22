package top.zsmile.dubbo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 *
 * @author zsmile
 * @email zsmile@gmail.com
 * @date 2021-08-22 22:07:54
 */
@Data
@TableName("tb_user_balance")
public class UserBalanceEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long userId;
	/**
	 * 钱种类
	 */
	private String cashType;
	/**
	 * 金额w
	 */
	private BigDecimal money;
	/**
	 * 冻结金额
	 */
	private BigDecimal frozenMoney;

}
