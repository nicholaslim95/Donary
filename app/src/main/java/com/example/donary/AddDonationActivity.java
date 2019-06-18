package com.example.donary;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class AddDonationActivity extends AppCompatActivity {
    Uri imageUri;
    String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    ImageView back, image_added;
    TextView donate;
    EditText Title, etDescription, etCondition;

    final ProgressDialog progressDialog = new ProgressDialog(this);


    //info of donation post to be edited
    String editTitle, editDescription, editCondition, editImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donation);

        back = findViewById(R.id.back);
        image_added = findViewById(R.id.image_added);
        donate = findViewById(R.id.donate);
        Title = findViewById(R.id.ettitle);
        etDescription = findViewById(R.id.etDescription);
        etCondition = findViewById(R.id.etCondition);

        storageReference = FirebaseStorage.getInstance().getReference("donates");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //get data through intent from previous activitie's adapter
        Intent intent = getIntent();
        final String isUpdatedKey = ""+intent.getStringExtra("key");
        final String editDonateId = ""+intent.getStringExtra("editDonationId");
        //set the donate text to update
        if(isUpdatedKey.equals("editDonation")){
            //update
            donate.setText("Update");
            loadPostData(editDonateId);
        }
        else
        {
            //add
            donate.setText("Donate");
            
        }

        //back to hoempage
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddDonationActivity.this, Homepage.class));
                finish();
            }
        });

        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUpdatedKey.equals("editDonation")){
                    beginUpdate(editTitle, editDescription, editCondition, editDonateId);
                }
                else {
                    uploadImage();
                }
            }
        });

        image_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                .setAspectRatio(1,1)
                .start(AddDonationActivity.this);
            }
        });

    }

    private void beginUpdate(String editTitle, String editDescription, String editCondition, String editDonateId) {

        progressDialog.setMessage("Updating...");
        progressDialog.show();

        if(!editImage.equals("")){
            //without Image
            updateWasWithImage(editTitle, editDescription, editDonateId);
        }else{
            //with Image

        }
    }

    private void updateWasWithImage(final String editTitle, final String editDescription, final String editDonateId) {
        //pos is with Image, delet previous image first
        StorageReference mPicturerRef = FirebaseStorage.getInstance().getReferenceFromUrl(editImage);
        mPicturerRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Image deleted, uoload new image
                        //for post-image name, post-id, publish time
                        final String timeStamp = String.valueOf(System.currentTimeMillis());
                        String filePathAndName = "donates/" + timeStamp;

                        //get image from imageview
                        Bitmap bitmap = ((BitmapDrawable)image_added.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        //image compress
                        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
                        byte[] data = baos.toByteArray();

                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                        ref.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //image uploaded get its url
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while(!uriTask.isSuccessful());

                                        String downloadUri = uriTask.getResult().toString();
                                        if(uriTask.isSuccessful()){
                                            //url is received, upload to firebase databse
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            //put post info
                                            hashMap.put("posImage", editImage);
                                            hashMap.put("title", editTitle);
                                            hashMap.put("description", editDescription);
                                            hashMap.put("condition", editCondition);
                                            hashMap.put("donater",   user.getUid());
                                            hashMap.put("time",   timeStamp);
                                            hashMap.put("status",   "Available");

                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("donates");
                                            ref.child(editDonateId)
                                                    .updateChildren(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddDonationActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddDonationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //image uploaded get its url
                                        progressDialog.dismiss();
                                        Toast.makeText(AddDonationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddDonationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void loadPostData(final String editDonateId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("donates");
        //get detail of post using if of post
        Query fquery = reference.orderByChild("donateid").equalTo(editDonateId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    editTitle = ""+ds.child("title").getValue();
                    editDescription = ""+ds.child("description").getValue();
                    editCondition = ""+ds.child("condition").getValue();
                    editImage = ""+ds.child("posImage").getValue();

                    //set data to views
                    Title.setText(editTitle);
                    etDescription.setText(editDescription);
                    etCondition.setText(editCondition);

                    //set image
                    if(!editImage.equals("noImage")){
                        try{
                            Picasso.get().load(editImage).into(image_added);
                        }catch (Exception e){

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private  String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private  void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting");
        progressDialog.show();

        if(imageUri != null){
            final StorageReference filereference = storageReference.child(System.currentTimeMillis()
                    + "." +getFileExtension(imageUri));

            uploadTask = filereference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isComplete()){
                        throw task.getException();
                    }
                    return  filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("donates");

                        String donateid = reference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("donateid", donateid);
                        hashMap.put("posImage", myUrl);
                        hashMap.put("title", Title.getText().toString());
                        hashMap.put("description", etDescription.getText().toString());
                        hashMap.put("condition", etCondition.getText().toString());
                        hashMap.put("donater",   user.getUid());
                        hashMap.put("time",   String.valueOf(System.currentTimeMillis()));
                        hashMap.put("status",   "Available");

                        reference.child(donateid).setValue(hashMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(AddDonationActivity.this, Homepage.class));
                        finish();
                    }else{
                        Toast.makeText(AddDonationActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddDonationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(AddDonationActivity.this, "Image Required!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    //ctrl + o
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            image_added.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this,"Something goes wrong", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddDonationActivity.this, Homepage.class));
            finish();
        }
    }
}
