package com.tokioschool.ratingapp.securities.routes;

public interface Routes {
    String H2_CONSOLE_FULL= "/h2-console/**";
    String LOGIN_API_FULL= "/api/ratings/login";
    String[] WHITE_LIST_URLS = {
            "/login**", "/error",
            "/oauth2/authenticate","/login/oauth2/code/**","/oauth2/token","/.well-known/**",
            H2_CONSOLE_FULL,
            LOGIN_API_FULL
    };
}
