package desafiomobiletracknme.tracknme.com.desafiomobiletracknme;

import java.util.Date;

/**
 * Created by arthur on 13/12/17.
 */

public class PosicaoBD {

    private String dateTime;
    private double latitude;
    private double longitude;
    private Date data;

    public PosicaoBD() {
    }

    public PosicaoBD(String dateTime, double latitude, double longitude, Date data) {
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.data = data;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
