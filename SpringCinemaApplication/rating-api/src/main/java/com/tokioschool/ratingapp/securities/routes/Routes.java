package com.tokioschool.ratingapp.securities.routes;

public interface Routes {

    String[] WHITE_LIST_URLS = {
            "/oauth2**","/oauth2/**",
            "/.well-known/**",
            "/h2-console/**"
    };
}
