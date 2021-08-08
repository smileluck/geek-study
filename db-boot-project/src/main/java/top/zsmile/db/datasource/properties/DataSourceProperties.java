/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package top.zsmile.db.datasource.properties;

import lombok.Data;

/**
 * 多数据源属性
 *
 * @author zz@gmail.com
 * @since 1.0.0
 */
@Data
public class DataSourceProperties {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
}
