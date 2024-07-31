package com.holanda.bilicacies.Adapters;

public class cart_variable_links {



    private String c_Supplier_Name, c_Product_Name, c_Price, c_Quantity, c_Product_ID,
            c_Type_to_Display, c_Date_Added, c_Photo_Count, c_Product_Desc, c_Supplier_Username;


    public cart_variable_links(String cart_Supplier_Name, String cart_Supplier_Username, String cart_Product_Name, String cart_Price, String cart_Quantity, String cart_Product_ID,
                               String cart_Type_to_display, String cart_Date_Added, String cart_Photo_Count, String cart_Product_Desc){
        c_Supplier_Name = cart_Supplier_Name;
        c_Product_Name = cart_Product_Name;
        c_Price = cart_Price;
        c_Quantity = cart_Quantity;
        c_Product_ID = cart_Product_ID;
        c_Type_to_Display = cart_Type_to_display;
        c_Date_Added = cart_Date_Added;
        c_Photo_Count = cart_Photo_Count;
        c_Product_Desc = cart_Product_Desc;
        c_Supplier_Username = cart_Supplier_Username;
    }

    public String c_get_Supplier_Name(){
        return c_Supplier_Name;
    }

    public String c_get_Product_Name(){
        return c_Product_Name;
    }

    public String c_get_Price(){
        return c_Price;
    }

    public String c_get_Quantity(){
        return c_Quantity;
    }

    public String c_get_Product_ID(){
        return c_Product_ID;
    }

    public String c_get_Type_to_Display(){
        return c_Type_to_Display;
    }

    public String c_get_Date_Added(){
        return c_Date_Added;
    }

    public String c_get_Photo_Count(){
        return c_Photo_Count;
    }

    public String c_get_Product_Desc(){
        return c_Product_Desc;
    }

    public String c_get_Supplier_Username(){
        return c_Supplier_Username;
    }


}
