package com.nahuelbentos.uberclone.activities.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nahuelbentos.uberclone.R;
import com.nahuelbentos.uberclone.activities.client.UpdateProfileActivity;
import com.nahuelbentos.uberclone.includes.MyToolbar;
import com.nahuelbentos.uberclone.models.Client;
import com.nahuelbentos.uberclone.models.Driver;
import com.nahuelbentos.uberclone.providers.AuthProvider;
import com.nahuelbentos.uberclone.providers.ClientProvider;
import com.nahuelbentos.uberclone.providers.DriverProvider;
import com.nahuelbentos.uberclone.providers.ImageProvider;
import com.nahuelbentos.uberclone.utils.CompressorBitmapImage;
import com.nahuelbentos.uberclone.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.File;

public class UpdateProfileDriverActivity extends AppCompatActivity {

    private ImageView mImageViewUpdateProfile;
    private Button mButtonUpdateProfile;
    private TextView mTextViewName;
    private TextView mTextViewBrandVehicle;
    private TextView mTextViewPlateVehicle;

    private DriverProvider mDriverProvider;
    private AuthProvider mAuthProvider;

    private File mImageFile;
    private String mImageUrl;
    private static final int GALLERY_REQUEST = 1;

    private ProgressDialog mProgressDialog;
    private String mName;
    private String mVehicleBrand;
    private String mVehiclePlate;
    private ImageProvider mImageProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_driver);

        MyToolbar.show(this, "Actualizar Perfil", true);

        mImageViewUpdateProfile = findViewById(R.id.imageViewUpdateProfile);
        mButtonUpdateProfile = findViewById(R.id.btnUpdateProfile);
        mTextViewName = findViewById(R.id.textViewName);
        mTextViewBrandVehicle = findViewById(R.id.textInputVehicleBrand);
        mTextViewPlateVehicle = findViewById(R.id.textInputVehiclePlate);

        mDriverProvider = new DriverProvider();
        mAuthProvider =  new AuthProvider();

        mProgressDialog = new ProgressDialog(this);
        mImageProvider = new ImageProvider("driver_images");
        getDriverInfo();

        mButtonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        mImageViewUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            try {
                mImageFile = FileUtil.from(this, data.getData());
                mImageViewUpdateProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR", "Mensaje : " + e.getMessage());
            }
        }
    }

    private void getDriverInfo(){
        mDriverProvider.getDriver(mAuthProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    String vehicleBrand = dataSnapshot.child("vehicleBrand").getValue().toString();
                    String vehiclePlate = dataSnapshot.child("vehiclePlate").getValue().toString();
                    mTextViewName.setText(name);
                    mTextViewBrandVehicle.setText(vehicleBrand);
                    mTextViewPlateVehicle.setText(vehiclePlate);


                    String image = "";
                    if(dataSnapshot.hasChild("image")){

                        image = dataSnapshot.child("image").getValue().toString();
                        Picasso.with(UpdateProfileDriverActivity.this ).load(image).into(mImageViewUpdateProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateProfile() {
        mName = mTextViewName.getText().toString();
        mVehiclePlate = mTextViewPlateVehicle.getText().toString();
        mVehicleBrand = mTextViewBrandVehicle.getText().toString();

        Log.d("ErrorUpdate", "1) mName: " + mName);

        if (!mName.equals("") && mImageViewUpdateProfile != null){
            mProgressDialog.setMessage("Espere un momento...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            saveImage();

        }else {
            Toast.makeText(this, "Ingresa la imagen y el nombre", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage() {

        mImageProvider.saveImage(UpdateProfileDriverActivity.this, mImageFile, mAuthProvider.getId()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String image = uri.toString();
                            Driver driver = new Driver();
                            Log.d("ErrorUpdate", "mName: " + mName);
                            Log.d("ErrorUpdate", "image: " + image);
                            driver.setName(mName);
                            driver.setVehicleBrand(mVehicleBrand);
                            driver.setVehiclePlate(mVehiclePlate);
                            driver.setImage(image);
                            driver.setId(mAuthProvider.getId());
                            mDriverProvider.update(driver).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(UpdateProfileDriverActivity.this, "Su informacin se actualizo correctamente", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                }
                            });
                        }
                    });
                }else {
                    Toast.makeText(UpdateProfileDriverActivity.this, "Hubo un error al subir la imagen.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}