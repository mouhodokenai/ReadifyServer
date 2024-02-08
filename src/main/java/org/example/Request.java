package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Request {

    private String request;

    private Map<String, String> mapAttributes;


    Request(Map<String,String> mapAttributes, String request){
        this.mapAttributes = mapAttributes;
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        request = request;
    }

    public Map<String, String> getMapAttributes() {
        return mapAttributes;
    }

    public void setMapAttributes(Map<String, String> mapAttributes) {
        mapAttributes = mapAttributes;
    }
}
