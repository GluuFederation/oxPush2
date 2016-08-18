/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.u2f.v2.model;

import com.google.gson.annotations.SerializedName;

/**
 * oxPush2 mobile device configuration
 *
 * Created by Yuriy Movchan on 02/22/2016.
 */
public class DeviceData {

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("push_token")
    private String pushToken;

    @SerializedName("type")
    private String type;

    @SerializedName("platform")
    private String platform;

    @SerializedName("name")
    private String name;

    @SerializedName("os_name")
    private String osName;

    @SerializedName("os_version")
    private String osVersion;

    public DeviceData() {
    }

    public DeviceData(String uuid, String pushToken, String type, String platform, String name, String osName, String osVersion) {
        this.uuid = uuid;
        this.pushToken = pushToken;
        this.type = type;
        this.platform = platform;
        this.name = name;
        this.osName = osName;
        this.osVersion = osVersion;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }
}
