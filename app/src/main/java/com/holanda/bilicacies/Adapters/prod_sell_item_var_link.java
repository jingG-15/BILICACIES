package com.holanda.bilicacies.Adapters;

public class prod_sell_item_var_link {

    private String prod_Name;
    private String prod_Price;
    private String prod_Date_Added;
    private String prod_Cover_URL;
    private String prod_Product_ID;
    private String prod_Photo_Count;
    private String prod_Product_Description;

    public prod_sell_item_var_link(String p_Name, String p_Price, String p_Date_Added, String p_Cover_URL, String p_Product_ID, String p_Photo_Count, String p_Product_Desc){
        prod_Name = p_Name;
        prod_Price = p_Price;
        prod_Date_Added = p_Date_Added;
        prod_Cover_URL = p_Cover_URL;
        prod_Product_ID = p_Product_ID;
        prod_Photo_Count = p_Photo_Count;
        prod_Product_Description = p_Product_Desc;
    }
    public String getImageURL(){
        return prod_Cover_URL;

    }
    public String getProductName(){
        return prod_Name;
    }
    public String getPrice(){
        return prod_Price;
    }
    public String getDate(){
        return prod_Date_Added;
    }
    public String getProdID(){
        return prod_Product_ID;
    }
    public String getPhotoCount(){
        return prod_Photo_Count;
    }

    public String getProductDesc(){
        return prod_Product_Description;
    }

}
