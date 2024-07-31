package com.holanda.bilicacies.Adapters;

public class chat_variable_links {


    private String ch_Chat_ID, ch_Message_Type, ch_Message_Content, ch_Date_Sent,
                    ch_Convo_ID, ch_Sel_Prod_ID, ch_Sel_Product_Name, ch_Sel_Product_Price,
                    ch_Sel_Prod_Date_Added, ch_Seller_ID, ch_Logged_Username, ch_Buyer_ID;


    public chat_variable_links(String chat_ID, String chat_Message_type, String chat_Message_Content,
                               String chat_Date_Sent, String chat_Convo_ID, String chat_Sel_Prod_ID,
                               String chat_Sel_Product_Name, String chat_Sel_Product_Price,
                               String chat_Sel_Prod_Date_Added, String chat_Seller_ID,
                               String chat_Logged_Username, String chat_Buyer_ID){
        ch_Chat_ID = chat_ID;
        ch_Message_Type = chat_Message_type;
        ch_Message_Content = chat_Message_Content;
        ch_Date_Sent = chat_Date_Sent;
        ch_Convo_ID = chat_Convo_ID;
        ch_Sel_Prod_ID = chat_Sel_Prod_ID;
        ch_Sel_Product_Name = chat_Sel_Product_Name;
        ch_Sel_Product_Price = chat_Sel_Product_Price;
        ch_Sel_Prod_Date_Added = chat_Sel_Prod_Date_Added;
        ch_Seller_ID = chat_Seller_ID;
        ch_Logged_Username = chat_Logged_Username;
        ch_Buyer_ID = chat_Buyer_ID;

    }

    public String ch_get_Chat_ID(){
        return ch_Chat_ID;
    }

    public String ch_get_Message_Type(){
        return ch_Message_Type;
    }

    public String ch_get_Message_Content(){
        return ch_Message_Content;
    }

    public String ch_get_Date_Sent(){
        return ch_Date_Sent;
    }

    public String ch_get_Convo_ID(){
        return ch_Convo_ID;
    }

    public String ch_get_Prod_ID(){
        return ch_Sel_Prod_ID;
    }

    public String ch_get_Product_Name(){
        return ch_Sel_Product_Name;
    }

    public String ch_get_Product_Price(){
        return ch_Sel_Product_Price;
    }

    public String ch_get_Prod_Date_Added(){
        return ch_Sel_Prod_Date_Added;
    }

    public String ch_get_Seller_ID(){
        return ch_Seller_ID;
    }

    public String ch_get_Logged_ID(){
        return ch_Logged_Username;
    }

    public String ch_get_Buyer_ID(){
        return ch_Buyer_ID;
    }
}
