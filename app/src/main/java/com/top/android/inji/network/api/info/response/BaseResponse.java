package com.top.android.inji.network.api.info.response;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xugang
 * @date 2018/5/7
 */
@Data
public class BaseResponse {

    @SerializedName("errorMsg")
    private String errorMsg;

    @SerializedName("result")
    private Integer result;
}
