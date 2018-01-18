package com.moecheng.cyborgcare.network.bean.request;


import java.util.List;

/**
 * Created by wangchengcheng on 2017/11/30.
 */

public class UploadRequest extends BaseRequest {

    private List<ValuePair> valuePairs;

    public List<ValuePair> getValuePairs() {
        return valuePairs;
    }

    public void setValuePairs(List<ValuePair> valuePairs) {
        this.valuePairs = valuePairs;
    }

    public static class ValuePair {

        private long timestamp;

        private float emgValue;
        private float rrValue;
        private float eegValue;
        private float tempValue;

        public ValuePair(long timestamp, float emgValue, float rrValue, float eegValue, float tempValue) {
            this.timestamp = timestamp;
            this.emgValue = emgValue;
            this.rrValue = rrValue;
            this.eegValue = eegValue;
            this.tempValue = tempValue;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public float getEmgValue() {
            return emgValue;
        }

        public void setEmgValue(float emgValue) {
            this.emgValue = emgValue;
        }

        public float getRrValue() {
            return rrValue;
        }

        public void setRrValue(float rrValue) {
            this.rrValue = rrValue;
        }

        public float getEegValue() {
            return eegValue;
        }

        public void setEegValue(float eegValue) {
            this.eegValue = eegValue;
        }

        public float getTempValue() {
            return tempValue;
        }

        public void setTempValue(float tempValue) {
            this.tempValue = tempValue;
        }
    }
}
