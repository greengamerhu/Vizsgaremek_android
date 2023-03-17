package hu.petrik.vizsgaremek;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Menu  implements Parcelable {
    private String food_id;
    private String food_name;
    private String food_description;
    private String food_category;
    private int food_price ;

    public Menu(String food_id, String food_name, String food_description, String food_category, int food_price) {
        this.food_id = food_id;
        this.food_name = food_name;
        this.food_description = food_description;
        this.food_category = food_category;
        this.food_price = food_price;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getFood_description() {
        return food_description;
    }

    public void setFood_description(String food_description) {
        this.food_description = food_description;
    }

    public String getFood_category() {
        return food_category;
    }

    public void setFood_category(String food_category) {
        this.food_category = food_category;
    }

    public int getFood_price() {
        return food_price;
    }

    public void setFood_price(int food_price) {
        this.food_price = food_price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
class MenuListHelper {
    private List<Menu> menu;

    public List<Menu> getMenus() {
        return menu;
    }
}
