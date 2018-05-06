/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info;


import lombok.Data;
import me.zhanghai.android.douya.network.api.info.dto.UserDTO;
@Data
public class AuthenticationResponse {
    private UserDTO userDTO;
}
