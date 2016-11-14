package de.schiewe.volker.bgjugend2016.data_models;

/**
 * Created by VolkerS on 19.06.2016
 */
public class Contact {
    private String Name;
    private String Address;
    private String Telephone;
    private String Mail;

    public Contact() {
        //required for firebase
    }

    public String getName() {
        return Name;
    }

    public String getAddress() {
        return Address;
    }

    public String getTelephone() {
        return Telephone;
    }

    public String getMail() {
        return Mail;
    }
}
