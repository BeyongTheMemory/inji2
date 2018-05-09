/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.network.api.info.response;


import com.google.gson.annotations.SerializedName;
import com.top.android.inji.network.api.info.dto.UserDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.top.android.inji.network.api.info.dto.UserDTO;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuthenticationResponse extends BaseResponse {
    @SerializedName("userDto")
    private UserDTO userDTO;
}
