package com.razi.furnitar.API;

public class APIResponse {

    private int statusCode;
    private String response;

    public APIResponse(int statusCode, String response) {
        this.statusCode = statusCode;
        this.response = response;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getResponse() {
        return response;
    }

    public void setLastname(String response) { this.response = response; }

}
