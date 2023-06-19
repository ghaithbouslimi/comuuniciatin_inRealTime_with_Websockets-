package com.example.Socket;

public class USPANA {
private String materialId ;
    private String routeName ;
    private int qty ;
    private String part ;
    private String vendor ;
    private String  userdata2;
    private String  userdata;
    private  String lot ;
 private String  ErrorText  ;
private String ErrorCode ;

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
    }

    public String getErrorText() {
        return ErrorText;
    }

    public void setErrorText(String errorText) {
        ErrorText = errorText;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getUserdata2() {
        return userdata2;
    }

    public void setUserdata2(String userdata2) {
        this.userdata2 = userdata2;
    }

    public String getUserdata() {
        return userdata;
    }

    public void setUserdata(String userdata) {
        this.userdata = userdata;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
//    private USPANA getMaterielReadyWithParsingPreSetupAVS(String materialId, String routeName, int qty) {
//        this.materialId = materialId ;
//
//    } ;

}
