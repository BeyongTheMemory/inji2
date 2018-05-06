package me.zhanghai.android.douya.network.api.info.dto;

import lombok.Data;

/**
 * @author xugang
 * @date 2018/5/6
 */
@Data
public class UserDTO {
    private long id;
    private String account;
    private String password;
    private String name;
    private String headUrl;
    private String introduction;
    private int sex = 0;
    private String imToken;
}
