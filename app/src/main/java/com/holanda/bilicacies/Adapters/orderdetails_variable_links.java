package com.holanda.bilicacies.Adapters;

public class orderdetails_variable_links {


    private String OD_ID, OD_Order_ID, OD_Product_ID, OD_Product_Quantity,
                    OD_Product_Price, OD_Seller_ID, OD_Seller_Fullname,
                    OD_Product_Name, OD_Total_Payables, OD_Order_Type,
                    OD_Order_Status,  OD_Order_Created, OD_Product_Date_Added,
                    OD_Product_Photo_Count, OD_Product_Desc, OD_Delivery_Fee,
                    OD_Delivery_Date, OD_Buyer_Fullname, OD_Buyer_Contact_Number,
                    OD_Complete_Address, OD_Remarks;

    Boolean OD_product_Only, OD_products_Done;


    public orderdetails_variable_links(String p_OD_ID, String p_OD_Order_ID, String p_OD_Product_ID,
                                       String p_OD_Product_Quantity, String p_OD_Product_Price,
                                       String p_OD_Seller_ID, String p_OD_Seller_Fullname,
                                       String p_OD_Product_Name, String p_OD_Total_Payables,
                                       String p_OD_Order_Type, String p_OD_Order_Status,
                                       Boolean p_OD_product_Only, Boolean p_OD_products_Done,
                                       String p_OD_Order_Created, String p_OD_Product_Date_Added,
                                       String p_OD_Product_Photo_Count, String p_OD_Product_Desc,
                                       String p_OD_Delivery_Fee, String p_OD_Delivery_Date,
                                       String p_OD_Buyer_Fullname, String p_OD_Buyer_Contact_Number,
                                       String p_OD_Complete_Address, String p_OD_Remarks){


        OD_ID = p_OD_ID;
        OD_Order_ID = p_OD_Order_ID;
        OD_Product_ID = p_OD_Product_ID;
        OD_Product_Quantity = p_OD_Product_Quantity;
        OD_Product_Price = p_OD_Product_Price;
        OD_Seller_ID = p_OD_Seller_ID;
        OD_Seller_Fullname = p_OD_Seller_Fullname;
        OD_Product_Name = p_OD_Product_Name;
        OD_Total_Payables = p_OD_Total_Payables;
        OD_Order_Type = p_OD_Order_Type;
        OD_Order_Status = p_OD_Order_Status;
        OD_product_Only = p_OD_product_Only;
        OD_products_Done = p_OD_products_Done;
        OD_Order_Created = p_OD_Order_Created;
        OD_Product_Date_Added = p_OD_Product_Date_Added;
        OD_Product_Photo_Count = p_OD_Product_Photo_Count;
        OD_Product_Desc = p_OD_Product_Desc;
        OD_Delivery_Fee = p_OD_Delivery_Fee;
        OD_Delivery_Date = p_OD_Delivery_Date;
        OD_Buyer_Fullname = p_OD_Buyer_Fullname;
        OD_Buyer_Contact_Number = p_OD_Buyer_Contact_Number;
        OD_Complete_Address = p_OD_Complete_Address;
        OD_Remarks = p_OD_Remarks;
    }

    public String getOD_ID() {
        return OD_ID;
    }

    public String getOD_Order_ID() {
        return OD_Order_ID;
    }

    public String getOD_Product_ID() {
        return OD_Product_ID;
    }

    public String getOD_Product_Quantity() {
        return OD_Product_Quantity;
    }

    public String getOD_Product_Price() {
        return OD_Product_Price;
    }

    public String getOD_Seller_ID() {
        return OD_Seller_ID;
    }

    public String getOD_Seller_Fullname() {
        return OD_Seller_Fullname;
    }

    public String getOD_Product_Name() {
        return OD_Product_Name;
    }

    public String getOD_Total_Payables() {
        return OD_Total_Payables;
    }

    public String getOD_Order_Type() {
        return OD_Order_Type;
    }

    public String getOD_Order_Status() {
        return OD_Order_Status;
    }

    public Boolean getOD_product_Only() {
        return OD_product_Only;
    }

    public Boolean getOD_products_Done() {
        return OD_products_Done;
    }

    public String getOD_Order_Created() {
        return OD_Order_Created;
    }

    public String getOD_Product_Date_Added() {
        return OD_Product_Date_Added;
    }

    public String getOD_Product_Photo_Count() {
        return OD_Product_Photo_Count;
    }

    public String getOD_Product_Desc() {
        return OD_Product_Desc;
    }

    public String getOD_Delivery_Fee() {
        return OD_Delivery_Fee;
    }

    public String getOD_Delivery_Date() {
        return OD_Delivery_Date;
    }

    public String getOD_Buyer_Fullname() {
        return OD_Buyer_Fullname;
    }

    public String getOD_Buyer_Contact_Number() {
        return OD_Buyer_Contact_Number;
    }

    public String getOD_Complete_Address() {
        return OD_Complete_Address;
    }

    public String getOD_Remarks() {
        return OD_Remarks;
    }
}
