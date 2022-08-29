package com.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private Button addButton;
    private Button deleteButton;
    private EditText mNameField;
    private EditText codeField;
    private TextView productError;
    private TextView welcome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* Database Instance */
        mDatabase = FirebaseDatabase.getInstance().getReference();
        /* Setting up arrayadapter and listview */
        ListView databaseListView = (ListView) findViewById(R.id.user_list);
        ArrayList<String> userDatas = new ArrayList<>();
        ArrayList<String> mKeys = new ArrayList<>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, userDatas);
        databaseListView.setAdapter(arrayAdapter);
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot,
                                     @Nullable String s) {
                String value = dataSnapshot.getValue(String.class);
                userDatas.add(value);
                String key = dataSnapshot.getKey();
                mKeys.add(key);
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot,
                                       @Nullable String s) {
                String value = dataSnapshot.getValue(String.class);
                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);
                userDatas.set(index, value);
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot,
                                     @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        /* Deleting Data From Database */
        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userDatas.clear();
                        mKeys.clear();
                        for (DataSnapshot dataSnapshot1:snapshot.getChildren()){
                            snapshot.getRef().removeValue();
// set the adapter
                            databaseListView.setAdapter(arrayAdapter);
                            arrayAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }
    public boolean onClickAdd (View view){

    }
    /* Public method for onClick function using the "Click To Add To Database" button */
    public boolean onClickAdd(View view){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mNameField = (EditText) findViewById(R.id.name_field);
        codeField = (EditText) findViewById(R.id.codeField);
        productError = (TextView) findViewById(R.id.productError);
        String name = mNameField.getText().toString();
        String code = codeField.getText().toString();
        /* Data Validation */
// Product name validation must less than 16 characters
        if (name.length() > 15){
            productError.setText("Product Name Error");
            return false;
        }
// Product code must be 7 characters starts with PCC
        boolean validation = code.length() == 7 && code.startsWith("PCC");
        if (!validation){
            productError.setText("Code Input Error");
            return false;
        }
        /* Adding Data into database */
        mDatabase.push().setValue(name).addOnCompleteListener(new
                                                                      OnCompleteListener<Void>() {
                                                                          @Override
                                                                          public void onComplete(@NonNull Task<Void> task) {
                                                                              if(task.isSuccessful()){
                                                                                  Toast.makeText(MainActivity.this, "Data has been Stored",
                                                                                          Toast.LENGTH_LONG).show();
                                                                              } else {
                                                                                  Toast.makeText(MainActivity.this, "An Error Has Been Occured",
                                                                                          Toast.LENGTH_SHORT).show();
                                                                              }
                                                                          }
                                                                      });
        mDatabase.push().setValue(code).addOnCompleteListener(new
                                                                      OnCompleteListener<Void>() {
                                                                          @Override
                                                                          public void onComplete(@NonNull Task<Void> task) {
                                                                              if(task.isSuccessful()){
                                                                                  Toast.makeText(MainActivity.this, "Data has been Stored",
                                                                                          Toast.LENGTH_LONG).show();
                                                                              } else {
                                                                                  Toast.makeText(MainActivity.this, "An Error Has Been Occured",
                                                                                          Toast.LENGTH_SHORT).show();
                                                                              }
                                                                          }
                                                                      });
        return false;
    }
}