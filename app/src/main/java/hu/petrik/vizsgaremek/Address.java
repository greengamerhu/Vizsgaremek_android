package hu.petrik.vizsgaremek;

import java.util.List;

public class Address {
    private int id;
    private String adress;
    private String city;
    private String postalCode;
    private String mobileNumber;

    public Address(int adress_id, String adress, String city, String postalCode, String mobileNumber) {
        this.id = adress_id;
        this.adress = adress;
        this.city = city;
        this.postalCode = postalCode;
        this.mobileNumber = mobileNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
class AddressListHelper {
    private List<Address> address;

    public List<Address> getAddresses() {
        return address;
    }
}