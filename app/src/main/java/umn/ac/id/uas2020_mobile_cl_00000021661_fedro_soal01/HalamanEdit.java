package umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01.Model.Mahasiswa;

public class HalamanEdit extends AppCompatActivity {

    Intent intent;
    private String ID;
    private String NIM;

    EditText etTambahNim, etTambahNama, etTambahBiografi;
    Spinner etTambahProdi,etTambahAngkatan;
    Button btnInsertData;

    FrameLayout flChangePicture;
    CircleImageView image_profile;

    DatabaseReference reference;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 22;
    private static final int CAMERA_REQUEST = 33;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS= 4;
    private Uri imageUrl;
    private StorageTask uploadTask;

    String currentPhotoPath;

    Boolean Profile_Image_Uploaded = false;


    private String[] Prodi = {"Pilih Prodi","Informatika","Sistem Informasi","Teknik Komputer","Teknik Fisika","Teknik Elektro"};
    private String[] Angkatan = {"Pilih Angkatan","2014","2015","2016","2017","2018","2019"};

    HashMap<String, Object> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_edit);
        this.getSupportActionBar().setTitle("Edit Mahasiswa");
        this.getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.gradient_1));
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        intent = getIntent();
        ID = intent.getStringExtra("IDnya");
        NIM = intent.getStringExtra("NIMnya");


        etTambahProdi = (Spinner) findViewById(R.id.etAddPRODI);
        etTambahAngkatan = (Spinner) findViewById(R.id.etAddANGKATAN);

        final ArrayAdapter<String> adapterProdi = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Prodi);
        etTambahProdi.setAdapter(adapterProdi);

        final ArrayAdapter<String> adapterAngkatan = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Angkatan);
        etTambahAngkatan.setAdapter(adapterAngkatan);


        etTambahNim = (EditText) findViewById(R.id.etAddNIM);
        etTambahNama = (EditText) findViewById(R.id.etAddNAMA);
        etTambahBiografi = (EditText) findViewById(R.id.etAddBIOGRAFI);
        btnInsertData = (Button) findViewById(R.id.btnInsertDATA);
        flChangePicture = (FrameLayout) findViewById(R.id.change_profile);
        image_profile = (CircleImageView) findViewById(R.id.profile_image);


        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        reference = FirebaseDatabase.getInstance().getReference("Mahasiswa");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Mahasiswa mahasiswa = dataSnapshot1.getValue(Mahasiswa.class);
                    if (mahasiswa.getID() != null && mahasiswa.getID().equals(ID)) {
                        Glide.with(getApplicationContext()).load(mahasiswa.getImageURL()).into(image_profile);
                        etTambahNim.setText(mahasiswa.getNIM());
                        etTambahNama.setText(mahasiswa.getNama());
                        etTambahProdi.setSelection(adapterProdi.getPosition(mahasiswa.getProdi()));
                        etTambahAngkatan.setSelection(adapterAngkatan.getPosition(mahasiswa.getAngkatan()));
                        etTambahBiografi.setText(mahasiswa.getBiografi());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HalamanEdit.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        btnInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HalamanEdit.this, R.style.MyDialogTheme);

                builder.setTitle("Edit Confirm");
                builder.setMessage("Are you sure want to Edit this mahasiswa?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        editMahasiswa(ID);
                        dialog.dismiss();
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



        //change profile picture
        final CharSequence[] items = {"Camera", "Select Image"};
        flChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndroidVersion();
                AlertDialog.Builder builder = new AlertDialog.Builder(HalamanEdit.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        if (item == 1){
                            openImage();
                        }else {
                            openCamera();
                        }

                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });



    }


    private void editMahasiswa(String id){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Mahasiswa");
        final Query query = reference.orderByChild("ID").equalTo(id);

        Pattern pattern = Pattern.compile("[a-zA-Z ]*");
        final Matcher matcher = pattern.matcher(etTambahNama.getText().toString());

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Query querys = FirebaseDatabase.getInstance().getReference().child("Mahasiswa").orderByChild("NIM").equalTo(etTambahNim.getText().toString());
                querys.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (etTambahNim.getText().toString().equals("") || etTambahNama.getText().toString().trim().equals("")||
                                etTambahProdi.getSelectedItem().toString().equals("Pilih Prodi") || etTambahAngkatan.getSelectedItem().toString().equals("Pilih Angkatan") ||
                                etTambahBiografi.getText().toString().trim().equals("")){
                            Toast.makeText(getApplicationContext(),"Semua Data Harus Di isi ! ",Toast.LENGTH_SHORT).show();
                        }else if(!matcher.matches()){
                            Toast.makeText(HalamanEdit.this,"Nama hanya boleh alphabet!", Toast.LENGTH_SHORT).show();
                        }else if (dataSnapshot.getChildrenCount()>0 && !etTambahNim.getText().toString().equals(NIM) ){
                                Toast.makeText(HalamanEdit.this,"NIM Sudah ada yang punya!", Toast.LENGTH_SHORT).show();
                        }else{


                            map.put("Nama",etTambahNama.getText().toString());
                            map.put("NIM",etTambahNim.getText().toString());
                            map.put("Prodi",etTambahProdi.getSelectedItem().toString());
                            map.put("Angkatan",etTambahAngkatan.getSelectedItem().toString());
                            map.put("Biografi",etTambahBiografi.getText().toString());
                            reference.child(ID).updateChildren(map);


                            Toast.makeText(HalamanEdit.this,"Mahasiswa Berhasil di Edit", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(HalamanEdit.this, HalamanDetail.class);
                            intent.putExtra("IDnya", ID);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });








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




    //Request permission

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();

        } else {
            // code for lollipop and pre-lollipop devices
        }

    }

    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(HalamanEdit.this,
                Manifest.permission.CAMERA);
        int wtite = ContextCompat.checkSelfPermission(HalamanEdit.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(HalamanEdit.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (wtite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(HalamanEdit.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }




    private void openCamera(){
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (camera.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }catch (IOException e){

            }
            if (photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "umn.ac.id.android.fileprovider",
                        photoFile);
                camera.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(camera,CAMERA_REQUEST);
            }
        }

    }

    //Create camera image path
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = HalamanEdit.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }



    //uploading image
    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(HalamanEdit.this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUrl != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +":"+getFileExtension(imageUrl));

            uploadTask = fileReference.putFile(imageUrl);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        assert downloadUri != null;
                        String mUri = downloadUri.toString();

//                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

                        map.put("imageURL",mUri);
                        Profile_Image_Uploaded = true;
//                        reference.updateChildren(map);

                        pd.dismiss();
                    } else{
                        Toast.makeText(HalamanEdit.this, "Failed",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HalamanEdit.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });

        }else{
            Toast.makeText(HalamanEdit.this,"No image selected", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Galery
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUrl = data.getData();
            image_profile.setImageURI(imageUrl);

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(HalamanEdit.this,"Upload in progress",Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
            }
        }
        //Camera
        else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            File f = new File(currentPhotoPath);
            image_profile.setImageURI(Uri.fromFile(f));

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            imageUrl = Uri.fromFile(f);
            mediaScanIntent.setData(imageUrl);
            this.sendBroadcast(mediaScanIntent);
            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(HalamanEdit.this,"Upload in progress",Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
            }

        }
    }





    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
