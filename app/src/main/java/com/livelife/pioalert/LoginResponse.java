package com.livelife.pioalert;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Maroof Ahmed Siddique on 17/10/17.
 */

public class LoginResponse {


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

        @SerializedName("user_name")
        @Expose
        private String userName;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("method")
        @Expose
        private String method;
        @SerializedName("response")
        @Expose
        private Boolean response;
        @SerializedName("format")
        @Expose
        private String format;
        @SerializedName("reason")
        @Expose
        private String reason;


        @SerializedName("data")
        @Expose
        private Data data;


        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
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

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
        public class Data {

            @SerializedName("users_id")
            @Expose
            private String usersId;
            @SerializedName("code")
            @Expose
            private Object code;
            @SerializedName("father_id")
            @Expose
            private String fatherId;
            @SerializedName("startActivity")
            @Expose
            private Object startActivity;
            @SerializedName("user_level")
            @Expose
            private String userLevel;
            @SerializedName("idcom")
            @Expose
            private String idcom;
            @SerializedName("user_name")
            @Expose
            private String userName;
            @SerializedName("user_pass")
            @Expose
            private String userPass;
            @SerializedName("user_real_name")
            @Expose
            private String userRealName;
            @SerializedName("user_real_surname")
            @Expose
            private String userRealSurname;
            @SerializedName("user_email")
            @Expose
            private String userEmail;
            @SerializedName("user_phone")
            @Expose
            private String userPhone;
            @SerializedName("sex")
            @Expose
            private String sex;
            @SerializedName("birthdate")
            @Expose
            private String birthdate;
            @SerializedName("birthdate_place")
            @Expose
            private String birthdatePlace;
            @SerializedName("town")
            @Expose
            private String town;
            @SerializedName("id_province")
            @Expose
            private String idProvince;
            @SerializedName("address")
            @Expose
            private String address;
            @SerializedName("tax_code")
            @Expose
            private String taxCode;
            @SerializedName("vat_code")
            @Expose
            private String vatCode;
            @SerializedName("notActiveSince")
            @Expose
            private String notActiveSince;
            @SerializedName("created")
            @Expose
            private String created;
            @SerializedName("updated")
            @Expose
            private String updated;
            @SerializedName("updatedUID")
            @Expose
            private String updatedUID;
            @SerializedName("status")
            @Expose
            private String status;
            @SerializedName("note")
            @Expose
            private String note;
            @SerializedName("attachment")
            @Expose
            private String attachment;
            @SerializedName("iban")
            @Expose
            private String iban;
            @SerializedName("FBupdate")
            @Expose
            private String fBupdate;
            @SerializedName("FBemail")
            @Expose
            private String fBemail;
            @SerializedName("FBid")
            @Expose
            private String fBid;
            @SerializedName("FBgender")
            @Expose
            private String fBgender;

            public String getUsersId() {
                return usersId;
            }

            public void setUsersId(String usersId) {
                this.usersId = usersId;
            }

            public Object getCode() {
                return code;
            }

            public void setCode(Object code) {
                this.code = code;
            }

            public String getFatherId() {
                return fatherId;
            }

            public void setFatherId(String fatherId) {
                this.fatherId = fatherId;
            }

            public Object getStartActivity() {
                return startActivity;
            }

            public void setStartActivity(Object startActivity) {
                this.startActivity = startActivity;
            }

            public String getUserLevel() {
                return userLevel;
            }

            public void setUserLevel(String userLevel) {
                this.userLevel = userLevel;
            }

            public String getIdcom() {
                return idcom;
            }

            public void setIdcom(String idcom) {
                this.idcom = idcom;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getUserPass() {
                return userPass;
            }

            public void setUserPass(String userPass) {
                this.userPass = userPass;
            }

            public String getUserRealName() {
                return userRealName;
            }

            public void setUserRealName(String userRealName) {
                this.userRealName = userRealName;
            }

            public String getUserRealSurname() {
                return userRealSurname;
            }

            public void setUserRealSurname(String userRealSurname) {
                this.userRealSurname = userRealSurname;
            }

            public String getUserEmail() {
                return userEmail;
            }

            public void setUserEmail(String userEmail) {
                this.userEmail = userEmail;
            }

            public String getUserPhone() {
                return userPhone;
            }

            public void setUserPhone(String userPhone) {
                this.userPhone = userPhone;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getBirthdate() {
                return birthdate;
            }

            public void setBirthdate(String birthdate) {
                this.birthdate = birthdate;
            }

            public String getBirthdatePlace() {
                return birthdatePlace;
            }

            public void setBirthdatePlace(String birthdatePlace) {
                this.birthdatePlace = birthdatePlace;
            }

            public String getTown() {
                return town;
            }

            public void setTown(String town) {
                this.town = town;
            }

            public String getIdProvince() {
                return idProvince;
            }

            public void setIdProvince(String idProvince) {
                this.idProvince = idProvince;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getTaxCode() {
                return taxCode;
            }

            public void setTaxCode(String taxCode) {
                this.taxCode = taxCode;
            }

            public String getVatCode() {
                return vatCode;
            }

            public void setVatCode(String vatCode) {
                this.vatCode = vatCode;
            }

            public String getNotActiveSince() {
                return notActiveSince;
            }

            public void setNotActiveSince(String notActiveSince) {
                this.notActiveSince = notActiveSince;
            }

            public String getCreated() {
                return created;
            }

            public void setCreated(String created) {
                this.created = created;
            }

            public String getUpdated() {
                return updated;
            }

            public void setUpdated(String updated) {
                this.updated = updated;
            }

            public String getUpdatedUID() {
                return updatedUID;
            }

            public void setUpdatedUID(String updatedUID) {
                this.updatedUID = updatedUID;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getNote() {
                return note;
            }

            public void setNote(String note) {
                this.note = note;
            }

            public String getAttachment() {
                return attachment;
            }

            public void setAttachment(String attachment) {
                this.attachment = attachment;
            }

            public String getIban() {
                return iban;
            }

            public void setIban(String iban) {
                this.iban = iban;
            }

            public String getFBupdate() {
                return fBupdate;
            }

            public void setFBupdate(String fBupdate) {
                this.fBupdate = fBupdate;
            }

            public String getFBemail() {
                return fBemail;
            }

            public void setFBemail(String fBemail) {
                this.fBemail = fBemail;
            }

            public String getFBid() {
                return fBid;
            }

            public void setFBid(String fBid) {
                this.fBid = fBid;
            }

            public String getFBgender() {
                return fBgender;
            }

            public void setFBgender(String fBgender) {
                this.fBgender = fBgender;
            }

        }
    }
}
