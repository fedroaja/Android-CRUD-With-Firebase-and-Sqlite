package umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01.Model.Mahasiswa;

public class HalamanDetail extends AppCompatActivity {

    Intent intent;
    String ID;
    DatabaseReference databaseReference;

    private CircleImageView image_profile;
    private TextView tvNIM,tvNAMA,tvProdi,tvAngkatan,tvBiografi;
    private Button btnEdit, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_detail);
        this.getSupportActionBar().setTitle("Detail Mahasiswa");
        this.getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.gradient_1));
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        ID = intent.getStringExtra("IDnya");


        image_profile = (CircleImageView) findViewById(R.id.profile_image_detail);
        tvNIM = (TextView) findViewById(R.id.NIMval);
        tvNAMA = (TextView) findViewById(R.id.NAMAval);
        tvProdi = (TextView) findViewById(R.id.PRODIval);
        tvAngkatan = (TextView) findViewById(R.id.ANGKATANval);
        tvBiografi = (TextView) findViewById(R.id.BIOGRAFIval);

        btnEdit = (Button) findViewById(R.id.btnEditPROFILE);
        btnDelete = (Button) findViewById(R.id.btnDeleteMHS);

        tvBiografi.setMovementMethod(new ScrollingMovementMethod());
        databaseReference = FirebaseDatabase.getInstance().getReference("Mahasiswa");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Mahasiswa mahasiswa = dataSnapshot1.getValue(Mahasiswa.class);
                    if (mahasiswa.getNIM() != null && mahasiswa.getID().equals(ID)) {
                        Glide.with(getApplicationContext()).load(mahasiswa.getImageURL()).into(image_profile);
                        tvNIM.setText(mahasiswa.getNIM());
                        tvNAMA.setText(mahasiswa.getNama());
                        tvProdi.setText(mahasiswa.getProdi());
                        tvAngkatan.setText(mahasiswa.getAngkatan());
                        tvBiografi.setText(mahasiswa.getBiografi());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HalamanDetail.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HalamanDetail.this,HalamanEdit.class);
                intent.putExtra("IDnya", ID);
                intent.putExtra("NIMnya", tvNIM.getText().toString());
                startActivity(intent);
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HalamanDetail.this, R.style.MyDialogTheme);

                builder.setTitle("Delete Confirm");
                builder.setMessage("Are you sure want to delete this mahasiswa?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        deletmahasiswa(ID);
                        Toast.makeText(HalamanDetail.this,"Mahasiswa Berhasil di hapus", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Intent intent = new Intent(HalamanDetail.this,HalamanPertama.class);
                        finish();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    private void deletmahasiswa(String id){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Mahasiswa");
        Query query = reference.orderByChild("ID").equalTo(id);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                dataSnapshot.getRef().setValue(null);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
