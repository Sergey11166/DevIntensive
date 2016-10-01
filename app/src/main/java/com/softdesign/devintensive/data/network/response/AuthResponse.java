package com.softdesign.devintensive.data.network.response;

import com.softdesign.devintensive.data.network.restmodels.AuthData;

/**
 * @author Sergey Vorobyev.
 */

@SuppressWarnings("unused")
public class AuthResponse extends AbsResponse {

    private AuthData data;

    public AuthData getData() {
        return data;
    }
}
