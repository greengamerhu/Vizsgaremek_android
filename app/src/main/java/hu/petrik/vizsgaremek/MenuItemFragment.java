package hu.petrik.vizsgaremek;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MenuItemFragment extends Fragment {
    private TextView textViewShowMenuItemTitle;
    private TextView textViewShowMenuItemDesc;
    private TextView textViewShowMenuItemCategory;
    private TextView textViewShowMenuItemPrice;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu_item, container, false);
        init(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Menu receivedItem = bundle.getParcelable("menuItem"); // Key
            textViewShowMenuItemTitle.setText(receivedItem.getFood_name());
            textViewShowMenuItemDesc.setText(receivedItem.getFood_description());
            textViewShowMenuItemCategory.setText(receivedItem.getFood_category());
            textViewShowMenuItemPrice.setText(String.valueOf(receivedItem.getFood_price()) + " Ft");
        }
        return view;
    }

    public void init(View view) {
        textViewShowMenuItemTitle = view.findViewById(R.id.textViewShowMenuItemTitle);
        textViewShowMenuItemDesc = view.findViewById(R.id.textViewShowMenuItemDesc);
        textViewShowMenuItemCategory = view.findViewById(R.id.textViewShowMenuItemCategory);
        textViewShowMenuItemPrice = view.findViewById(R.id.textViewShowMenuItemPrice);
    }
}