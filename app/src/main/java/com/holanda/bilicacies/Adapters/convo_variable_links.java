package com.holanda.bilicacies.Adapters;

public class convo_variable_links {





    private String cn_Convo_ID, cn_Seller_ID, cn_Buyer_ID, cn_Convo_Updated, cn_First_Message,
            cn_Seller_Fullname, cn_Buyer_Fullname, cn_Logged_Username;


    public convo_variable_links(String convo_Convo_ID, String convo_Seller_ID, String convo_Buyer_ID,
                                String convo_Convo_Updated, String convo_First_Message,
                                String convo_Seller_Fullname, String convo_Buyer_Fullname,
                                String convo_Logged_Username){
        cn_Convo_ID = convo_Convo_ID;
        cn_Seller_ID = convo_Seller_ID;
        cn_Buyer_ID = convo_Buyer_ID;
        cn_Convo_Updated = convo_Convo_Updated;
        cn_First_Message = convo_First_Message;
        cn_Seller_Fullname = convo_Seller_Fullname;
        cn_Buyer_Fullname = convo_Buyer_Fullname;
        cn_Logged_Username = convo_Logged_Username;

    }

    public String cn_get_Convo_ID(){
        return cn_Convo_ID;
    }

    public String cn_get_Seller_ID(){
        return cn_Seller_ID;
    }

    public String cn_get_Buyer_ID(){
        return cn_Buyer_ID;
    }

    public String cn_get_Convo_Updated(){
        return cn_Convo_Updated;
    }

    public String cn_get_First_Message(){
        return cn_First_Message;
    }

    public String cn_get_Seller_Fullname(){
        return cn_Seller_Fullname;
    }

    public String cn_get_Buyer_Fullname(){
        return cn_Buyer_Fullname;
    }

    public String cn_get_Logged_Username(){
        return cn_Logged_Username;
    }

}
