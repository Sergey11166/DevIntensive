package com.softdesign.devintensive.data.network.restmodels;

@SuppressWarnings("unused")
public class Contacts {

    private String vk;
    private String phone;
    private String email;
    private String updated;

    public String getVk() {
        return vk;
    }
    public void setVk(String vk) {
        this.vk = vk;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
}