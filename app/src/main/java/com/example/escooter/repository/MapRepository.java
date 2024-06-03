package com.example.escooter.repository;

import com.example.escooter.BuildConfig;
import com.example.escooter.callback.EscooterGpsCallback;
import com.example.escooter.callback.HttpResultCallback;
import com.example.escooter.callback.ReturnAreasCallback;
import com.example.escooter.data.model.Gps;
import com.example.escooter.data.model.ReturnAreas;
import com.example.escooter.network.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapRepository {
    public void getReturnAreas(ReturnAreasCallback callback) {
        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/getReturnAreas", "GET",null, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    JSONArray returnAreasArray = result.getJSONArray("data");
                    List<ReturnAreas.ReturnArea> returnAreaList = new ArrayList<>();

                    for (int i = 0; i < returnAreasArray.length(); i++) {
                        JSONObject returnAreaObject = returnAreasArray.getJSONObject(i);
                        ReturnAreas.ReturnArea returnArea = new ReturnAreas.ReturnArea();
                        returnArea.setIdreturnArea(returnAreaObject.getInt("idreturnArea"));

                        JSONArray areaPointsArray = returnAreaObject.getJSONArray("areaPoint");
                        List<ReturnAreas.AreaPoint> areaPointList = new ArrayList<>();
                        for (int j = 0; j < areaPointsArray.length(); j++) {
                            JSONObject areaPointObject = areaPointsArray.getJSONObject(j);
                            ReturnAreas.AreaPoint areaPoint = new ReturnAreas.AreaPoint();
                            areaPoint.setLatitude(areaPointObject.getDouble("latitude"));
                            areaPoint.setLongitude(areaPointObject.getDouble("longitude"));
                            areaPointList.add(areaPoint);
                        }
                        returnArea.setAreaPoint(areaPointList);
                        returnAreaList.add(returnArea);
                    }

                    ReturnAreas returnAreas = new ReturnAreas();
                    returnAreas.setData(returnAreaList);

                    callback.onSuccess(returnAreas);
                } catch (JSONException e) {
                    System.out.println("00000");
                    callback.onFailure(e);
                }
            }

            @Override
            public void onError(Exception e) {
                System.out.println("1111");
                callback.onFailure(e);
            }
        });
    }
}
