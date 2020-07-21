package umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01.Adapter.MahasiswaAdapter;
import umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01.Model.Mahasiswa;
import umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01.SqLite.Session;

public class HalamanPertama extends AppCompatActivity {

    private Session session;

    private RecyclerView recyclerView;
    private SearchView searchView;
    private MahasiswaAdapter mahasiswaAdapter;
    DatabaseReference databaseReference;

    private List<Mahasiswa> mMahasiswa;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_pertama);
        this.getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.gradient_1));
        this.getSupportActionBar().setTitle("Data Mahasiswa");



        session = new Session(this);
        if (!session.loggin()){
            logout();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HalamanPertama.this,HalamanAddNew.class));
            }
        });

        mMahasiswa = new ArrayList<>();
        searchView = (SearchView) findViewById(R.id.search_bar_view);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HalamanPertama.this));

        databaseReference = FirebaseDatabase.getInstance().getReference("Mahasiswa");

        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mMahasiswa.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Mahasiswa mahasiswa = dataSnapshot1.getValue(Mahasiswa.class);
                        mMahasiswa.add(mahasiswa);
                    }
                    mahasiswaAdapter = new MahasiswaAdapter(getApplicationContext(), mMahasiswa);
                    recyclerView.setAdapter(mahasiswaAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (searchView != null){

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }

    }

    private void search (String str){

        ArrayList<Mahasiswa> mahasiswaNew = new ArrayList<>();
        for (Mahasiswa object : mMahasiswa){
            if (object.getNama().toLowerCase().contains(str.toLowerCase()))
            {
                mahasiswaNew.add(object);
                MahasiswaAdapter mahasiswaAdapter = new MahasiswaAdapter(getApplicationContext(),mahasiswaNew);
                recyclerView.setAdapter(mahasiswaAdapter);
            }
            if (object.getNIM().toLowerCase().contains(str.toLowerCase()))
            {
                mahasiswaNew.add(object);
                MahasiswaAdapter mahasiswaAdapter = new MahasiswaAdapter(getApplicationContext(),mahasiswaNew);
                recyclerView.setAdapter(mahasiswaAdapter);
            }
            if (str.equals(""))
            {
                mahasiswaNew.clear();
                MahasiswaAdapter mahasiswaAdapter = new MahasiswaAdapter(getApplicationContext(),mMahasiswa);
                recyclerView.setAdapter(mahasiswaAdapter);
            }
        }



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.NIMasc){
            Collections.sort(mMahasiswa, new Comparator<Mahasiswa>() {
                @Override
                public int compare(Mahasiswa o1, Mahasiswa o2) {
                    return o1.getNIM().compareToIgnoreCase(o2.getNIM());
                }
            });
            mahasiswaAdapter.notifyDataSetChanged();
        }
        if(id == R.id.NIMdesc){
            Collections.sort(mMahasiswa, new Comparator<Mahasiswa>() {
                @Override
                public int compare(Mahasiswa o1, Mahasiswa o2) {
                    return o2.getNIM().compareToIgnoreCase(o1.getNIM());
                }
            });
            mahasiswaAdapter.notifyDataSetChanged();
        }

        if(id == R.id.NAMAasc){
            Collections.sort(mMahasiswa, new Comparator<Mahasiswa>() {
                @Override
                public int compare(Mahasiswa o1, Mahasiswa o2) {
                    return o1.getNama().compareToIgnoreCase(o2.getNama());
                }
            });
            mahasiswaAdapter.notifyDataSetChanged();
        }

        if(id == R.id.NAMAdesc){
            Collections.sort(mMahasiswa, new Comparator<Mahasiswa>() {
                @Override
                public int compare(Mahasiswa o1, Mahasiswa o2) {
                    return o2.getNama().compareToIgnoreCase(o1.getNama());
                }
            });
            mahasiswaAdapter.notifyDataSetChanged();
        }

        if(id == R.id.Aboutme){
            Intent intent = new Intent(HalamanPertama.this,HalamanProfile.class);
            startActivity(intent);
        }
        if(id == R.id.logout){
           logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(HalamanPertama.this, MainActivity.class));
    }

}
