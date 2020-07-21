package umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
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
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class HalamanAddNew extends AppCompatActivity {

    EditText etTambahNim, etTambahNama, etTambahBiografi;
    Spinner etTambahProdi,etTambahAngkatan;
    Button btnInsertData;

    FrameLayout flChangePicture;
    CircleImageView image_profile;

    DatabaseReference reference;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 11;
    private static final int CAMERA_REQUEST = 23;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS= 7;
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
        setContentView(R.layout.activity_halaman_add_new);


        this.getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.gradient_1));
        this.getSupportActionBar().setTitle("Tambah Mahasiswa");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTambahProdi = (Spinner) findViewById(R.id.etAddPRODI);
        etTambahAngkatan = (Spinner) findViewById(R.id.etAddANGKATAN);

        ArrayAdapter<String> adapterProdi = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Prodi);
        etTambahProdi.setAdapter(adapterProdi);

        ArrayAdapter<String> adapterAngkatan = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Angkatan);
        etTambahAngkatan.setAdapter(adapterAngkatan);


        etTambahNim = (EditText) findViewById(R.id.etAddNIM);
        etTambahNama = (EditText) findViewById(R.id.etAddNAMA);
        etTambahBiografi = (EditText) findViewById(R.id.etAddBIOGRAFI);
        btnInsertData = (Button) findViewById(R.id.btnInsertDATA);
        flChangePicture = (FrameLayout) findViewById(R.id.change_profile);
        image_profile = (CircleImageView) findViewById(R.id.profile_image);




        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        //change profile picture
        final CharSequence[] items = {"Camera", "Select Image"};
        flChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndroidVersion();
                AlertDialog.Builder builder = new AlertDialog.Builder(HalamanAddNew.this);
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



        btnInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


                Pattern pattern = Pattern.compile("[a-zA-Z ]*");
                final Matcher matcher = pattern.matcher(etTambahNama.getText().toString());

                Query query = FirebaseDatabase.getInstance().getReference().child("Mahasiswa").orderByChild("NIM").equalTo(etTambahNim.getText().toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (etTambahNim.getText().toString().equals("") || etTambahNama.getText().toString().trim().equals("")||
                                etTambahProdi.getSelectedItem().toString().equals("Pilih Prodi") || etTambahAngkatan.getSelectedItem().toString().equals("Pilih Angkatan") ||
                                etTambahBiografi.getText().toString().trim().equals("") || !Profile_Image_Uploaded){
                            Toast.makeText(getApplicationContext(),"Semua Data Harus Di isi ! ",Toast.LENGTH_SHORT).show();
                        }else if(!matcher.matches()){
                            Toast.makeText(HalamanAddNew.this,"Nama hanya boleh alphabet!", Toast.LENGTH_SHORT).show();
                        }else if (dataSnapshot.getChildrenCount()>0){
                            Toast.makeText(HalamanAddNew.this,"NIM Sudah ada yang punya!", Toast.LENGTH_SHORT).show();
                        } else{

                            final ProgressDialog pd = new ProgressDialog(HalamanAddNew.this);
                            pd.setMessage("Add New Mahasiswa");
                            pd.show();




                            String temp = reference.push().getKey();

                            DatabaseReference updateref = FirebaseDatabase.getInstance().getReference().child("Mahasiswa").child(temp);
                            HashMap<String, Object> updatemap = new HashMap<>();
                            updatemap.put("ID",temp);
                            updatemap.put("Nama",etTambahNama.getText().toString());
                            updatemap.put("NIM",etTambahNim.getText().toString());
                            updatemap.put("Prodi",etTambahProdi.getSelectedItem().toString());
                            updatemap.put("Angkatan",etTambahAngkatan.getSelectedItem().toString());
                            updatemap.put("Biografi",etTambahBiografi.getText().toString());
                            updatemap.put("imageURL",map.get("imageURL"));
                            updateref.setValue(updatemap);




                            Toast.makeText(HalamanAddNew.this, "Mahasiswa Berhasil Di Tambahkan",Toast.LENGTH_SHORT).show();

                            finish();
                            Intent succesAddnew = new Intent(HalamanAddNew.this,HalamanPertama.class);
                            pd.dismiss();

                        }




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



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
        int camera = ContextCompat.checkSelfPermission(HalamanAddNew.this,
                Manifest.permission.CAMERA);
        int wtite = ContextCompat.checkSelfPermission(HalamanAddNew.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(HalamanAddNew.this, Manifest.permission.READ_EXTERNAL_STORAGE);
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
            ActivityCompat.requestPermissions(HalamanAddNew.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
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
        ContentResolver contentResolver = HalamanAddNew.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }



    //uploading image
    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(HalamanAddNew.this);
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
                        Toast.makeText(HalamanAddNew.this, "Failed",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HalamanAddNew.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });

        }else{
            Toast.makeText(HalamanAddNew.this,"No image selected", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(HalamanAddNew.this,"Upload in progress",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(HalamanAddNew.this,"Upload in progress",Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
            }

        }
    }




    //request permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("in fragment on request", "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("in fragment on request", "CAMERA & WRITE_EXTERNAL_STORAGE READ_EXTERNAL_STORAGE permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("in fragment on request", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(HalamanAddNew.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(HalamanAddNew.this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(HalamanAddNew.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showDialogOK("Camera and Storage Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(HalamanAddNew.this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(HalamanAddNew.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }



    @Override
    public void onBackPressed() {
        finish();
    }
}
