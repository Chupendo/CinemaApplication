package com.tokioschool.store.authentications;

public interface StoreAuthenticationService {

    String getAccessToken();
    String getAccessToken(String userName);

}