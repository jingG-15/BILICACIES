package com.holanda.bilicacies.Adapters;

public class notif_variable_links {

    private String nt_Notif_Type, nt_Notif_Contents, nt_Notif_Data_1, nt_Notif_Data_2, nt_Notif_Data_3,
                    nt_Notif_date;


    public notif_variable_links(String Notif_Type, String Notif_Contents, String Notif_Data_1,
                                    String Notif_Data_2, String Notif_Data_3, String Notif_Date){

        nt_Notif_Type = Notif_Type;
        nt_Notif_Contents = Notif_Contents;
        nt_Notif_Data_1 = Notif_Data_1;
        nt_Notif_Data_2 = Notif_Data_2;
        nt_Notif_Data_3 = Notif_Data_3;
        nt_Notif_date = Notif_Date;
    }

    public String getNt_Notif_Type() {
        return nt_Notif_Type;
    }

    public String getNt_Notif_Contents() {
        return nt_Notif_Contents;
    }

    public String getNt_Notif_Data_1() {
        return nt_Notif_Data_1;
    }

    public String getNt_Notif_Data_2() {
        return nt_Notif_Data_2;
    }

    public String getNt_Notif_Data_3() {
        return nt_Notif_Data_3;
    }

    public String getNt_Notif_date() {
        return nt_Notif_date;
    }
}
