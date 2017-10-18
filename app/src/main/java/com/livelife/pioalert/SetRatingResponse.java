package com.livelife.pioalert;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Shoeb on 17/10/17.
 */

public class SetRatingResponse {

    @SerializedName("response")
    @Expose
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public class Response {

        @SerializedName("method")
        @Expose
        private String method;
        @SerializedName("uid")
        @Expose
        private String uid;
        @SerializedName("rating")
        @Expose
        private String rating;
        @SerializedName("element_type")
        @Expose
        private String elementType;
        @SerializedName("element_id")
        @Expose
        private String elementId;
        @SerializedName("comment")
        @Expose
        private String comment;
        @SerializedName("response")
        @Expose
        private Boolean response;
        @SerializedName("format")
        @Expose
        private String format;
        @SerializedName("device_token")
        @Expose
        private Boolean deviceToken;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getElementType() {
            return elementType;
        }

        public void setElementType(String elementType) {
            this.elementType = elementType;
        }

        public String getElementId() {
            return elementId;
        }

        public void setElementId(String elementId) {
            this.elementId = elementId;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public Boolean getResponse() {
            return response;
        }

        public void setResponse(Boolean response) {
            this.response = response;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public Boolean getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(Boolean deviceToken) {
            this.deviceToken = deviceToken;
        }

    }
}
