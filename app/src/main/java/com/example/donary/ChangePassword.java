package com.example.donary;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private Button btn_update_password;
    private EditText txt_new_password;
    private FirebaseUser firebaseUser;

    String newUserPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        btn_update_password = (Button) findViewById(R.id.btnUpdateChangedPassword);
        txt_new_password = (EditText) findViewById(R.id.txtNewPassword);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); //FirebaseAuth is firebaseUser's parent



        btn_update_password.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                newUserPassword = txt_new_password.getText().toString();
                if(newUserPassword.isEmpty()){
                    Toast.makeText(ChangePassword.this, "Password field is empty.", Toast.LENGTH_SHORT).show();
                }else{
                    firebaseUser.updatePassword(newUserPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ChangePassword.this, "Password update successful.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ChangePassword.this, "Password update failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
