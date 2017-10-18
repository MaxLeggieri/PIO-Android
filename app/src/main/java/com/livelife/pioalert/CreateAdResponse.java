package com.livelife.pioalert;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Maroof Ahmed Siddique on 17/10/17.
 */

public class CreateAdResponse {

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

        @SerializedName("start")
        @Expose
        private Integer start;
        @SerializedName("beacon")
        @Expose
        private String beacon;
        @SerializedName("method")
        @Expose
        private String method;
        @SerializedName("link")
        @Expose
        private String link;
        @SerializedName("userid")
        @Expose
        private String userid;
        @SerializedName("categories")
        @Expose
        private String categories;
        @SerializedName("alertkind")
        @Expose
        private String alertkind;
        @SerializedName("hashtags")
        @Expose
        private String hashtags;
        @SerializedName("idcom")
        @Expose
        private String idcom;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("location")
        @Expose
        private String location;
        @SerializedName("coupon")
        @Expose
        private String coupon;
        @SerializedName("idad")
        @Expose
        private String idad;
        @SerializedName("raykm")
        @Expose
        private String raykm;
        @SerializedName("expiration")
        @Expose
        private Boolean expiration;
        @SerializedName("youtube")
        @Expose
        private String youtube;
        @SerializedName("RelatedProducts")
        @Expose
        private String relatedProducts;
        @SerializedName("product")
        @Expose
        private String product;
        @SerializedName("response")
        @Expose
        private Boolean response;
        @SerializedName("format")
        @Expose
        private String format;
        @SerializedName("idcat0")
        @Expose
        private String idcat0;
        @SerializedName("idcat1")
        @Expose
        private String idcat1;
        @SerializedName("idcat2")
        @Expose
        private String idcat2;
        @SerializedName("idcat3")
        @Expose
        private String idcat3;
        @SerializedName("locations")
        @Expose
        private String locations;
        @SerializedName("couponcode")
        @Expose
        private String couponcode;
        @SerializedName("reason")
        @Expose
        private String reason;

        public Integer getStart() {
            return start;
        }

        public void setStart(Integer start) {
            this.start = start;
        }

        public String getBeacon() {
            return beacon;
        }

        public void setBeacon(String beacon) {
            this.beacon = beacon;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getCategories() {
            return categories;
        }

        public void setCategories(String categories) {
            this.categories = categories;
        }

        public String getAlertkind() {
            return alertkind;
        }

        public void setAlertkind(String alertkind) {
            this.alertkind = alertkind;
        }

        public String getHashtags() {
            return hashtags;
        }

        public void setHashtags(String hashtags) {
            this.hashtags = hashtags;
        }

        public String getIdcom() {
            return idcom;
        }

        public void setIdcom(String idcom) {
            this.idcom = idcom;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getCoupon() {
            return coupon;
        }

        public void setCoupon(String coupon) {
            this.coupon = coupon;
        }

        public String getIdad() {
            return idad;
        }

        public void setIdad(String idad) {
            this.idad = idad;
        }

        public String getRaykm() {
            return raykm;
        }

        public void setRaykm(String raykm) {
            this.raykm = raykm;
        }

        public Boolean getExpiration() {
            return expiration;
        }

        public void setExpiration(Boolean expiration) {
            this.expiration = expiration;
        }

        public String getYoutube() {
            return youtube;
        }

        public void setYoutube(String youtube) {
            this.youtube = youtube;
        }

        public String getRelatedProducts() {
            return relatedProducts;
        }

        public void setRelatedProducts(String relatedProducts) {
            this.relatedProducts = relatedProducts;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
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

        public String getIdcat0() {
            return idcat0;
        }

        public void setIdcat0(String idcat0) {
            this.idcat0 = idcat0;
        }

        public String getIdcat1() {
            return idcat1;
        }

        public void setIdcat1(String idcat1) {
            this.idcat1 = idcat1;
        }

        public String getIdcat2() {
            return idcat2;
        }

        public void setIdcat2(String idcat2) {
            this.idcat2 = idcat2;
        }

        public String getIdcat3() {
            return idcat3;
        }

        public void setIdcat3(String idcat3) {
            this.idcat3 = idcat3;
        }

        public String getLocations() {
            return locations;
        }

        public void setLocations(String locations) {
            this.locations = locations;
        }

        public String getCouponcode() {
            return couponcode;
        }

        public void setCouponcode(String couponcode) {
            this.couponcode = couponcode;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    }
