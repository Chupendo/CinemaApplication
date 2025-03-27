package com.tokioschool.ratingapp.securities.routes;

public interface Routes {

    String[] WHITE_LIST_URLS = {
            "/login**", "/error",
            "/oauth2/authenticate","/login/oauth2/code/**","/oauth2/token","/.well-known/**",
            "/h2-console/**"
    };
}
