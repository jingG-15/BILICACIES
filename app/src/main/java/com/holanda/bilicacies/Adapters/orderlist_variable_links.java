package com.holanda.bilicacies.Adapters;

public class orderlist_variable_links {




    private String O_ID, O_Order_ID, O_Seller_ID, O_Buyer_ID, O_Order_Status, O_Order_Type,
                O_Order_Created, O_Seller_Fullname, O_First_Product_ID, O_First_Product_Name,
                O_First_Product_Quantity, O_First_Product_Price, O_Total_Product_Order_Count,
                O_Total_Payables;


    public orderlist_variable_links(String Od_ID, String Od_Order_ID, String Od_Seller_ID, String Od_Buyer_ID,
                                    String Od_Order_Status, String Od_Order_Type, String Od_Order_Created,
                                    String Od_Seller_Fullname, String Od_First_Product_ID, String Od_First_Product_Name,
                                    String Od_First_Product_Quantity, String Od_First_Product_Price,
                                    String Od_Total_Product_Order_Count, String Od_Total_Payables){
        O_ID = Od_ID;
        O_Order_ID = Od_Order_ID;
        O_Seller_ID = Od_Seller_ID;
        O_Buyer_ID = Od_Buyer_ID;
        O_Order_Status = Od_Order_Status;
        O_Order_Type = Od_Order_Type;
        O_Order_Created = Od_Order_Created;
        O_Seller_Fullname = Od_Seller_Fullname;
        O_First_Product_ID = Od_First_Product_ID;
        O_First_Product_Name = Od_First_Product_Name;
        O_First_Product_Quantity = Od_First_Product_Quantity;
        O_First_Product_Price = Od_First_Product_Price;
        O_Total_Product_Order_Count = Od_Total_Product_Order_Count;
        O_Total_Payables = Od_Total_Payables;
    }

    public String getO_ID() {
        return O_ID;
    }

    public String getO_Order_ID() {
        return O_Order_ID;
    }

    public String getO_Seller_ID() {
        return O_Seller_ID;
    }

    public String getO_Buyer_ID() {
        return O_Buyer_ID;
    }

    public String getO_Order_Status() {
        return O_Order_Status;
    }

    public String getO_Order_Type() {
        return O_Order_Type;
    }

    public String getO_Order_Created() {
        return O_Order_Created;
    }

    public String getO_Seller_Fullname() {
        return O_Seller_Fullname;
    }

    public String getO_First_Product_ID() {
        return O_First_Product_ID;
    }

    public String getO_First_Product_Name() {
        return O_First_Product_Name;
    }

    public String getO_First_Product_Quantity() {
        return O_First_Product_Quantity;
    }

    public String getO_First_Product_Price() {
        return O_First_Product_Price;
    }

    public String getO_Total_Product_Order_Count() {
        return O_Total_Product_Order_Count;
    }

    public String getO_Total_Payables() {
        return O_Total_Payables;
    }
}
