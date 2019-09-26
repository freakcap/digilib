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
import android.widget.ProgressBar;
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

public class RequestBooks extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    EditText editText_book;
    Button button_request,button_viewRequestBooks;
    private String TAG=DemandSlip.class.getName();
    private ProgressBar progressBar;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_books);
        editText_book=(EditText)findViewById(R.id.editText_book);
        button_request=(Button)findViewById(R.id.button_request);
        button_viewRequestBooks=(Button)findViewById(R.id.button_viewRequestedBooks);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_request);
        progressBar.setVisibility(View.GONE);
        button_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth=FirebaseAuth.getInstance();
                FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                CollectionReference c2 = firebaseFirestore.collection("Users").document(firebaseUser.getEmail())
                        .collection("Requested_Books");
                c2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty())
                        {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            flag=0;
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
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Book Request Already Exists",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            createBookRequest();
                        }

                    }
                });

            }
        });

        button_viewRequestBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                finish();
                startActivity(new Intent(getApplicationContext(),ViewRequestBooks.class));
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    public void createBookRequest()
    {
        if(TextUtils.isEmpty(editText_book.getText().toString()))
        {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this,"Empty Field ",Toast.LENGTH_LONG).show();
            return;
        }

        Map<String,Object> docData1=new HashMap<>();
        docData1.put("bookName", editText_book.getText().toString());
        docData1.put("demandDate", FieldValue.serverTimestamp( ));
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        firebaseFirestore.collection("Users").document(firebaseUser.getEmail())
                .collection("Requested_Books")
                .add(docData1)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Book Request Added Successfullly",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot written with ID in users database: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

}
