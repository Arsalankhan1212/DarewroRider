package com.darewro.rider.data.api.Interface;

import org.json.JSONException;

import java.util.HashMap;

/**
 * Created by AhmedAbbas on 12/7/2017.
 */

public interface InitApi {
    public HashMap<String,String> getBody();
    public HashMap<String,Object> getObjBody();

    public HashMap<String,String> getHeader();
}
