package com.top.android.inji.network.api.info.request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author xugang
 * @date 2018/5/7
 */
@Data
@AllArgsConstructor
public class LoginRequest {
    private String account;
    private String password;
    private String ip;
    private String clintId;
    private String version;
    private int clientType;
}
