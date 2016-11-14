package de.schiewe.volker.bgjugend2016.data_models;


public class Info {

    private String Headline;
    private String Wer;
    private String WannWo;
    private String Anmeldung;


    public Info() {
        //required for firebase
    }

    public String getHeadline() {
        return Headline;
    }

    public String getWer() {
        return Wer;
    }

    public String getWannWo() {
        return WannWo;
    }

    public String getAnmeldung() {
        return Anmeldung;
    }
}
