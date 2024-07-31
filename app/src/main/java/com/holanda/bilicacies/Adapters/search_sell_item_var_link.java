package com.holanda.bilicacies.Adapters;

public class search_sell_item_var_link {


    private String search_Name;
    private String search_Price;
    private String search_Date_Added;
    private String search_Cover_URL;
    private String search_Product_ID;
    private String search_Photo_Count;
    private String search_Product_Description;

    public search_sell_item_var_link(String p_Name, String p_Price, String p_Date_Added, String p_Cover_URL, String p_Product_ID, String p_Photo_Count, String p_Product_Desc){
        search_Name = p_Name;
        search_Price = p_Price;
        search_Date_Added = p_Date_Added;
        search_Cover_URL = p_Cover_URL;
        search_Product_ID = p_Product_ID;
        search_Photo_Count = p_Photo_Count;
        search_Product_Description = p_Product_Desc;
    }
    public String getImageURL(){
        return search_Cover_URL;

    }
    public String getProductName(){
        return search_Name;
    }
    public String getPrice(){
        return search_Price;
    }
    public String getDate(){
        return search_Date_Added;
    }
    public String getProdID(){
        return search_Product_ID;
    }
    public String getPhotoCount(){
        return search_Photo_Count;
    }

    public String getProductDesc(){
        return search_Product_Description;
    }
}
