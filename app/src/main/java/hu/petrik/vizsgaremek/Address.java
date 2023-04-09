package hu.petrik.vizsgaremek;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Address implements Parcelable {
    private int id;
    private String address;
    private String city;
    private String  postalCode;
    private String mobileNumber;

    public Address(int adress_id, String address, String city, String postalCode, String mobileNumber) {
        this.id = adress_id;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String  getPostalCode() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
class AddressListHelper {
    private List<Address> address;

    public List<Address> getAddresses() {
        return address;
    }
}