package com.livelife.pioalert;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by maroof on 10/18/2017.
 */

public class CompanyLocationResponseModal {

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
        @SerializedName("device_token")
        @Expose
        private Boolean deviceToken;
        @SerializedName("rec")
        @Expose
        private String rec;
        @SerializedName("lat")
        @Expose
        private String lat;
        @SerializedName("lng")
        @Expose
        private String lng;
        @SerializedName("page")
        @Expose
        private String page;
        @SerializedName("response")
        @Expose
        private Boolean response;
        @SerializedName("format")
        @Expose
        private String format;
        @SerializedName("ord")
        @Expose
        private String ord;
        @SerializedName("direction")
        @Expose
        private String direction;
        @SerializedName("mode")
        @Expose
        private String mode;
        @SerializedName("idcat")
        @Expose
        private Integer idcat;
        @SerializedName("data")
        @Expose
        private Data data;

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

        public Boolean getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(Boolean deviceToken) {
            this.deviceToken = deviceToken;
        }

        public String getRec() {
            return rec;
        }

        public void setRec(String rec) {
            this.rec = rec;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
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

        public String getOrd() {
            return ord;
        }

        public void setOrd(String ord) {
            this.ord = ord;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public Integer getIdcat() {
            return idcat;
        }

        public void setIdcat(Integer idcat) {
            this.idcat = idcat;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
        public class Data {

            @SerializedName("products")
            @Expose
            private List<Product> products = null;


            public List<Product> getProducts() {
                return products;
            }

            public void setProducts(List<Product> products) {
                this.products = products;
            }


            public class Product {

                @SerializedName("officialname")
                @Expose
                private String officialname;
                @SerializedName("brandname")
                @Expose
                private String brandname;
                @SerializedName("level")
                @Expose
                private String level;
                @SerializedName("logoext")
                @Expose
                private String logoext;
                @SerializedName("vat")
                @Expose
                private String vat;
                @SerializedName("phone")
                @Expose
                private String phone;
                @SerializedName("email")
                @Expose
                private String email;
                @SerializedName("address")
                @Expose
                private String address;
                @SerializedName("description")
                @Expose
                private String description;
                @SerializedName("idcom")
                @Expose
                private String idcom;
                @SerializedName("distance")
                @Expose
                private String distance;
                @SerializedName("idlocation")
                @Expose
                private String idlocation;
                @SerializedName("lat")
                @Expose
                private String lat;
                @SerializedName("lng")
                @Expose
                private String lng;
                @SerializedName("name")
                @Expose
                private String name;
                @SerializedName("addressloc")
                @Expose
                private String addressloc;
                @SerializedName("companylogo")
                @Expose
                private String companylogo;
                @SerializedName("dl")
                @Expose
                private Boolean dl;

                public String getOfficialname() {
                    return officialname;
                }

                public void setOfficialname(String officialname) {
                    this.officialname = officialname;
                }

                public String getBrandname() {
                    return brandname;
                }

                public void setBrandname(String brandname) {
                    this.brandname = brandname;
                }

                public String getLevel() {
                    return level;
                }

                public void setLevel(String level) {
                    this.level = level;
                }

                public String getLogoext() {
                    return logoext;
                }

                public void setLogoext(String logoext) {
                    this.logoext = logoext;
                }

                public String getVat() {
                    return vat;
                }

                public void setVat(String vat) {
                    this.vat = vat;
                }

                public String getPhone() {
                    return phone;
                }

                public void setPhone(String phone) {
                    this.phone = phone;
                }

                public String getEmail() {
                    return email;
                }

                public void setEmail(String email) {
                    this.email = email;
                }

                public String getAddress() {
                    return address;
                }

                public void setAddress(String address) {
                    this.address = address;
                }

                public String getDescription() {
                    return description;
                }

                public void setDescription(String description) {
                    this.description = description;
                }

                public String getIdcom() {
                    return idcom;
                }

                public void setIdcom(String idcom) {
                    this.idcom = idcom;
                }

                public String getDistance() {
                    return distance;
                }

                public void setDistance(String distance) {
                    this.distance = distance;
                }

                public String getIdlocation() {
                    return idlocation;
                }

                public void setIdlocation(String idlocation) {
                    this.idlocation = idlocation;
                }

                public String getLat() {
                    return lat;
                }

                public void setLat(String lat) {
                    this.lat = lat;
                }

                public String getLng() {
                    return lng;
                }

                public void setLng(String lng) {
                    this.lng = lng;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getAddressloc() {
                    return addressloc;
                }

                public void setAddressloc(String addressloc) {
                    this.addressloc = addressloc;
                }

                public String getCompanylogo() {
                    return companylogo;
                }

                public void setCompanylogo(String companylogo) {
                    this.companylogo = companylogo;
                }


                public Boolean getDl() {
                    return dl;
                }

                public void setDl(Boolean dl) {
                    this.dl = dl;
                }

                public List<Loc> getLoc() {
                    return loc;
                }

                public void setLoc(List<Loc> loc) {
                    this.loc = loc;
                }

                @SerializedName("loc")
                @Expose
                private List<Loc> loc = null;
                public class Loc {

                    @SerializedName("idlocation")
                    @Expose
                    private String idlocation;
                    @SerializedName("name")
                    @Expose
                    private String name;
                    @SerializedName("address")
                    @Expose
                    private String address;
                    @SerializedName("locemail")
                    @Expose
                    private String locemail;
                    @SerializedName("loctel")
                    @Expose
                    private String loctel;
                    @SerializedName("lat")
                    @Expose
                    private String lat;
                    @SerializedName("lng")
                    @Expose
                    private String lng;
                    @SerializedName("distance")
                    @Expose
                    private String distance;

                    public String getIdlocation() {
                        return idlocation;
                    }

                    public void setIdlocation(String idlocation) {
                        this.idlocation = idlocation;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getAddress() {
                        return address;
                    }

                    public void setAddress(String address) {
                        this.address = address;
                    }

                    public String getLocemail() {
                        return locemail;
                    }

                    public void setLocemail(String locemail) {
                        this.locemail = locemail;
                    }

                    public String getLoctel() {
                        return loctel;
                    }

                    public void setLoctel(String loctel) {
                        this.loctel = loctel;
                    }

                    public String getLat() {
                        return lat;
                    }

                    public void setLat(String lat) {
                        this.lat = lat;
                    }

                    public String getLng() {
                        return lng;
                    }

                    public void setLng(String lng) {
                        this.lng = lng;
                    }

                    public String getDistance() {
                        return distance;
                    }

                    public void setDistance(String distance) {
                        this.distance = distance;
                    }

                }
        }

    }
}}
