package com.livelife.pioalert;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Maroof Ahmed Siddique on 17/10/17.
 */

public class ReviewsModal {


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
        @SerializedName("element_type")
        @Expose
        private String elementType;
        @SerializedName("element_id")
        @Expose
        private String elementId;
        @SerializedName("page")
        @Expose
        private String page;
        @SerializedName("response")
        @Expose
        private Boolean response;
        @SerializedName("format")
        @Expose
        private String format;
        @SerializedName("data")
        @Expose
        private Data data;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
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

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
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

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
        public class Data {

            @SerializedName("ratings")
            @Expose
            private List<Rating> ratings = null;


            public List<Rating> getRatings() {
                return ratings;
            }

            public void setRatings(List<Rating> ratings) {
                this.ratings = ratings;
            }
            public class Rating {

                @SerializedName("id")
                @Expose
                private String id;
                @SerializedName("user_id")
                @Expose
                private String userId;
                @SerializedName("ratingid")
                @Expose
                private String ratingid;
                @SerializedName("comment")
                @Expose
                private String comment;
                @SerializedName("element_type")
                @Expose
                private String elementType;
                @SerializedName("element_id")
                @Expose
                private String elementId;
                @SerializedName("g_name")
                @Expose
                private String gName;
                @SerializedName("fb_name")
                @Expose
                private String fbName;
                @SerializedName("g_pic")
                @Expose
                private String gPic;
                @SerializedName("fb_pic")
                @Expose
                private String fbPic;
                @SerializedName("rating")
                @Expose
                private String rating;
                @SerializedName("user_image")
                @Expose
                private String userImage;
                @SerializedName("user_name")
                @Expose
                private String userName;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getUserId() {
                    return userId;
                }

                public void setUserId(String userId) {
                    this.userId = userId;
                }

                public String getRatingid() {
                    return ratingid;
                }

                public void setRatingid(String ratingid) {
                    this.ratingid = ratingid;
                }

                public String getComment() {
                    return comment;
                }

                public void setComment(String comment) {
                    this.comment = comment;
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

                public String getGName() {
                    return gName;
                }

                public void setGName(String gName) {
                    this.gName = gName;
                }

                public String getFbName() {
                    return fbName;
                }

                public void setFbName(String fbName) {
                    this.fbName = fbName;
                }

                public String getGPic() {
                    return gPic;
                }

                public void setGPic(String gPic) {
                    this.gPic = gPic;
                }

                public String getFbPic() {
                    return fbPic;
                }

                public void setFbPic(String fbPic) {
                    this.fbPic = fbPic;
                }

                public String getRating() {
                    return rating;
                }

                public void setRating(String rating) {
                    this.rating = rating;
                }

                public String getUserImage() {
                    return userImage;
                }

                public void setUserImage(String userImage) {
                    this.userImage = userImage;
                }

                public String getUserName() {
                    return userName;
                }

                public void setUserName(String userName) {
                    this.userName = userName;
                }

            }


    }
}}
