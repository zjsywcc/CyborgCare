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

        private float value;

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    }
}
