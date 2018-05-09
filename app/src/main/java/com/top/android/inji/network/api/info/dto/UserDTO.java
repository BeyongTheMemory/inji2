package com.top.android.inji.network.api.info.dto;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

/**
 * @author xugang
 * @date 2018/5/6
 */
@Data
public class UserDTO implements Parcelable{
    private long id;
    private String account;
    private String password;
    private String name;
    private String headUrl;
    private String introduction;
    private int sex = 0;
    private String imToken;

    protected UserDTO(Parcel in) {
        id = in.readLong();
        account = in.readString();
        password = in.readString();
        name = in.readString();
        headUrl = in.readString();
        introduction = in.readString();
        sex = in.readInt();
        imToken = in.readString();
    }

    public UserDTO() {
    }

    public static final Creator<UserDTO> CREATOR = new Creator<UserDTO>() {
        @Override
        public UserDTO createFromParcel(Parcel in) {
            return new UserDTO(in);
        }

        @Override
        public UserDTO[] newArray(int size) {
            return new UserDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(account);
        dest.writeString(password);
        dest.writeString(name);
        dest.writeString(headUrl);
        dest.writeString(introduction);
        dest.writeInt(sex);
        dest.writeString(imToken);
    }
}
