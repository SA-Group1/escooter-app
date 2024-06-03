package com.example.escooter.data.model;

import java.util.List;

public class ReturnAreas {
    private List<ReturnArea> data;

    // Getter and Setter
    public List<ReturnArea> getData() {
        return data;
    }

    public void setData(List<ReturnArea> data) {
        this.data = data;
    }

    public static class ReturnArea {
        private int idreturnArea;
        private List<AreaPoint> areaPoint;

        // Getters and Setters
        public int getIdreturnArea() {
            return idreturnArea;
        }

        public void setIdreturnArea(int idreturnArea) {
            this.idreturnArea = idreturnArea;
        }

        public List<AreaPoint> getAreaPoint() {
            return areaPoint;
        }

        public void setAreaPoint(List<AreaPoint> areaPoint) {
            this.areaPoint = areaPoint;
        }
    }

    public static class AreaPoint {
        private Double latitude;
        private Double longitude;

        // Getters and Setters
        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
    }
}
