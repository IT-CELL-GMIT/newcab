package com.codinglegend.legendsp.cabme.models;

public class NotificationModel {

    String
          id,  timeDate,
    fromUsername,
    content,
    isSeen,
    isDisabled,
    type,
    address;

    public NotificationModel(String id, String timeDate, String fromUsername, String content, String isSeen, String isDisabled, String type, String address) {
        this.id = id;
        this.timeDate = timeDate;
        this.fromUsername = fromUsername;
        this.content = content;
        this.isSeen = isSeen;
        this.isDisabled = isDisabled;
        this.type = type;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(String isDisabled) {
        this.isDisabled = isDisabled;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }
}
