package com.softdesign.devintensive.data.network.response;

/**
 * @author Sergey Vorobyev
 */

@SuppressWarnings("unused")
abstract class AbsResponse {

    private boolean success;

    public boolean isSuccess() {
        return success;
    }
}
