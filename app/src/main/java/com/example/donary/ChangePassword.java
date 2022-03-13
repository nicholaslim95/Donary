package com.example.donary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private Button btn_update_password;
    private EditText txt_new_password, txt_current_password;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    String newUserPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        btn_update_password = (Button) findViewById(R.id.btnUpdateChangedPassword);
        txt_new_password = (EditText) findViewById(R.id.txtNewPassword);
        txt_current_password = (EditText) findViewById(R.id.txtCurrentPassword);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); //FirebaseAuth is firebaseUser's parent
        firebaseAuth = FirebaseAuth.getInstance();


        btn_update_password.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("The password field is: " + txt_new_password.getText().toString());
                if(txt_new_password.getText().toString().isEmpty() || txt_current_password.getText().toString().isEmpty()){
                    Toast.makeText(ChangePassword.this, "Make sure to fill in all fields.", Toast.LENGTH_SHORT).show();
                }else{
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), txt_current_password.getText().toString());
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    firebaseUser.updatePassword(""+txt_new_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(ChangePassword.this, "Password update successful.", Toast.LENGTH_SHORT).show();
                                                //firebaseAuth.signOut();
                                                //finish();
                                                //Due to limitation of firebase change password, user have to re-login to complete the process.
                                                //Intent intent = new Intent(ChangePassword.this, MainActivity.class);
                                                //startActivity(intent);
                                            }else{
                                                Toast.makeText(ChangePassword.this, "Password update failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(ChangePassword.this, "Password update failed", Toast.LENGTH_SHORT).show();
                                }
                        }
                    });

                }
            }
        });
    }
}
