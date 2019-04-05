package com.example.tdsapp;

import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tdsapp.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;

    private RecyclerView recyclerView;

    //global variables
    private String post_key;
    private String name;
    private String description;

    //database reference
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("TDS APP");

        mAuth = FirebaseAuth.getInstance();

        //user
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uID = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("All Data").child(uID);

        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
            }
        });

    }

    private void addData(){

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.imputlayout, null);

        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();

        dialog.setCancelable(false);

        final EditText name = myView.findViewById(R.id.name);
        final EditText desc = myView.findViewById(R.id.description);
        Button save = myView.findViewById(R.id.btnSave);
        Button cancel = myView.findViewById(R.id.btnCancel);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName = name.getText().toString().trim();
                String mDescription = desc.getText().toString().trim();

                if (TextUtils.isEmpty(mName)){
                    name.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(mDescription)){
                    desc.setError("Required Field");
                    return;
                }

                //generate id - get random key - get date
                String id = mDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                //calling model and pushing the data with specific id
                Data data = new Data(mName, mDescription, id, mDate);
                mDatabase.child(id).setValue(data);

                Toast.makeText(getApplicationContext(), "DATA SAVED", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    //retrieve data from database
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder>adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.itemdesign,
                MyViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position) {

                viewHolder.setDate(model.getDate());
                viewHolder.setName(model.getName());
                viewHolder.setDescription(model.getDescription());

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(position).getKey();
                        name = model.getName();
                        description = model.getDescription();

                        updateData();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

    }

    //new class for recycler view
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setName(String name){
            TextView mName = view.findViewById(R.id.nameItem);
            mName.setText(name);
        }

        public void setDescription(String description){
            TextView mDescription = view.findViewById(R.id.descItem);
            mDescription.setText(description);
        }

        public void setDate(String date){
            TextView mDate = view.findViewById(R.id.date);
            mDate.setText(date);
        }
    }

    public void updateData(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.updatedata, null);

        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        EditText mName = myView.findViewById(R.id.updateName);
        EditText mDesc = myView.findViewById(R.id.updateDesc);

        //setting default value in the clicked item
        mName.setText(name);
        //cursor will start from the end of the name
        mName.setSelection(name.length());

        mDesc.setText(description);
        mDesc.setSelection(description.length());

        Button btnDel = myView.findViewById(R.id.btnDelete);
        Button btnUpdate = myView.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
