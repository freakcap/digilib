package com.example.digitallibraryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemandSlip extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    EditText editText_book;
    Button button_demand,button_viewDemandSlips;
    private String TAG=DemandSlip.class.getName();
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demand_slip);
        editText_book=(EditText)findViewById(R.id.editText_book);
        button_demand=(Button)findViewById(R.id.button_demand);
        button_viewDemandSlips=(Button)findViewById(R.id.button_viewDemandSlip);
        button_demand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth=FirebaseAuth.getInstance();
                FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                CollectionReference c2 = firebaseFirestore.collection("Users").document(firebaseUser.getEmail())
                        .collection("Demand_Slips");
                c2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            flag=0;
                            button_demand.setText("hi");
                            for(DocumentSnapshot d : list)
                            {
                                Log.d(TAG, "Document" + d.getString("bookName"));
                                editText_book=(EditText)findViewById(R.id.editText_book);
                                String bName=editText_book.getText().toString().toLowerCase().trim();

                                if(bName.equals(d.getString("bookName").toLowerCase().trim())) {
                                    flag = 1;
                                    break;
                                }
                            }
                            if(flag==0)
                                createBookRequest();
                            else {
                                Toast.makeText(getApplicationContext(), "Demand Slip Already Exists",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                });

            }
        });

        button_viewDemandSlips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(),viewDemandSlips.class));
            }
        });
    }


    public void createBookRequest()
    {
        if(TextUtils.isEmpty(editText_book.getText().toString()))
        {
            Toast.makeText(this,"Empty Field ",Toast.LENGTH_LONG).show();
            return;
        }

        Map<String,Object> docData1=new HashMap<>();
        docData1.put("bookName", editText_book.getText().toString());
        docData1.put("demandDate", FieldValue.serverTimestamp( ));
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        firebaseFirestore.collection("Users").document(firebaseUser.getEmail())
                .collection("Demand_Slips")
                .add(docData1)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(),"Demand Slip Added Successfullly",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot written with ID in users database: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Technical Error",
                                Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

}