package com.example.pnp2_inventory_app;

import android.app.AlertDialog;
import android.graphics.Color;
//import android.content.ClipData;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.EditText;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.Context;

//import android.widget.RelativeLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;

// DbStuff for testing
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

//import org.w3c.dom.Text;

import org.w3c.dom.Text;

import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import DbConfig.FirebaseConfig;

//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Map;

//import java.util.ArrayList;
//import java.util.List;
import java.util.Locale;

public class fragment_home extends Fragment {
    private Button buttonEditItem;
    private AlertDialog dialog; // Declare the dialog as a member variable
    // Rafael Testing, ignore this
    private Button addItem;
    private TextView items;
    private Context context;
    private  View rootView;
    private  FirebaseConfig db;
    List<Item> itemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();

        db = new FirebaseConfig();
        db.ConnectDatabase();

        // Create a sample list of items
        itemList = new ArrayList<>();

        // Find the "Edit" button and set its initial visibility
        buttonEditItem = rootView.findViewById(R.id.ButtonEditItem);
        buttonEditItem.setVisibility(View.VISIBLE); // Set the visibility to always be visible

        ImageButton RefreshBtn = rootView.findViewById(R.id.ImgBtnRefresh);
        RefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetItemsFromDatabase();
            }
        });

        //sets a object for the add button
        ImageButton buttonAddItem = rootView.findViewById(R.id.ButtonAddItem);
        buttonAddItem.setOnClickListener(v -> {
            showDialogToAddItem(itemList);
        });


        // Add OnClickListener to hide the "Edit" button when the user clicks anywhere on the screen
        rootView.setOnClickListener(v -> buttonEditItem.setVisibility(View.GONE));

        return rootView;
    }

    private void AddToScrollView(Item newItem ){
        ItemObject newItemObject = CreateItemObject(newItem);
        LinearLayout VerticalLinearView = rootView.findViewById(R.id.LinearLayoutOutside);
        LinearLayout InsideLinearLayout = new LinearLayout(VerticalLinearView.getContext());
        InsideLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        newItemObject.AmountObject.setPadding(0,0,200,0);
        InsideLinearLayout.addView(newItemObject.AmountObject);
        InsideLinearLayout.addView(newItemObject.NameObject);
        newItemObject.ExpireDateObject.setPadding(300,0,0,0);
        InsideLinearLayout.addView(newItemObject.ExpireDateObject);

        VerticalLinearView.addView(InsideLinearLayout); //adds the objects to the scrollView
    }

    private ItemObject CreateItemObject(Item NewItem){
        ItemObject NewItemObject = new ItemObject(NewItem, context);
        return NewItemObject;
    }

    private void showDialogToAddItem(List<Item> adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Item");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);

        EditText editTextQuantity = dialogView.findViewById(R.id.editTextQuantity);
        EditText editTextItemName = dialogView.findViewById(R.id.editTextItemName);
        CalendarView calendarView = dialogView.findViewById(R.id.calendarView);
        Button buttonAccept = dialogView.findViewById(R.id.buttonAccept);

        // Variable to store the selected date
        final Calendar selectedDate = Calendar.getInstance();

        // Set the initial selected date
        selectedDate.setTimeInMillis(calendarView.getDate());

        // Set the OnDateChangeListener to update the selected date
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        });

        buttonAccept.setOnClickListener(v -> {
            int quantity = Integer.parseInt(editTextQuantity.getText().toString());
            String itemName = editTextItemName.getText().toString();
            String expirationDate = getFormattedDate(selectedDate);

            Item NewItem = new Item(itemName, quantity, expirationDate);
            AddToScrollView(NewItem);
            db.InsertDb(NewItem);
            adapter.add(NewItem);

            // Dismiss the dialog after accepting the input
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
        dialog = builder.create();
        dialog.show();
    }

    private void GetItemsFromDatabase(){
        List<Item> itemList = new ArrayList<>();

        db.GetAll("InventoryItems", new FirebaseConfig.FirestoreCallback() {
            @Override
            public void OnCallBack(QuerySnapshot querySnapshot) {
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    String name = document.getString("name");
                    int quantity = document.getLong("quantity").intValue();
                    String expirationDate = document.getString("expirationDate");
                    String documentId = document.getString("documentId");
                    String insertedDate = document.getString("insertedDate ");
                    String lastUpdated = document.getString("lastUpdated ");

                    Item item = new Item(name, quantity, expirationDate, documentId);
                    item.insertedDate = insertedDate;
                    item.lastUpdated = lastUpdated;
                    itemList.add(item);
                }
            }
        });
        for(Item items: itemList){
            AddToScrollView(items);
        }
    }


    private String getFormattedDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public void testingThisShit(View view) {
        //scrollView testing
        //items = view.findViewById(R.id.textView2);

        addItem = view.findViewById(R.id.ButtonAddItem);

        // for testing, deprecated
        //items = view.findViewById(R.id.textView);


        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String, Object>> temp;

                //Item newItem = new Item("MilkTest", 1, "12/12/12", dbActions.GetDate(), dbActions.GetDate());
                //dbActions.testingItemAdd(newItem);

                // it takes a bit of time for the Cloudstore to return the data its getting.
                // using a callback interface (which is configured and declared inside FirebaseConfig,
                // this will return to the function when the call returns something!
                // currently trasnforming to json
                // TODO: figure if we want json or just convert into item class
                db.GetAll("InventoryItems", new FirebaseConfig.FirestoreCallback() {
                    @Override
                    public void OnCallBack(QuerySnapshot querySnapshot) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String json = document.getData().toString();
                            List<String> test = new ArrayList<>();
                            test.add(json);
                        }
                    }
                });
            }
        });
    }
}