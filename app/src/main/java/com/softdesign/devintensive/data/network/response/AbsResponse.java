package com.softdesign.devintensive.data.network.response;

/**
 * @author Sergey Vorobyev
 */

@SuppressWarnings("unused")
public abstract class AbsResponse {

    private boolean success;

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
