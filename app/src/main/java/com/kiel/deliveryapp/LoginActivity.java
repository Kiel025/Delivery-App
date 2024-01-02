package com.kiel.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.kiel.deliveryapp.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private TextView txtErrorMsg, txtSignIn;
    private Button btLogin;
    private EditText editEmail, editPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startComponents();

        txtSignIn.setOnClickListener(v -> {
            startActivity(new Intent(this, SinginActivity.class));
            cleanComponents();
        });

        btLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                txtErrorMsg.setText(R.string.login_txt_error_msg);
            } else {
                txtErrorMsg.setText("");
                authenticateuser();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            Log.i("Login to MainActivity", "Start MainActivity");
        }
    }

    private void authenticateuser() {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.VISIBLE);
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        }, 3000);

                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        String error = "";

                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            error = "Email ou senha incorretos.";
                            Log.e("Login", e.toString());
                        } catch (FirebaseAuthUserCollisionException e) {
                            error = "Usuário já cadastrado.";
                            Log.e("Login", e.toString());
                        } catch (FirebaseNetworkException e) {
                            error = "Erro de conexão com a internet";
                            Log.e("Login", e.toString());
                        } catch (Exception e) {
                            error = "Erro desconhecido.";
                            Log.e("Login", e.toString());
                        } finally {
                            txtErrorMsg.setText(Objects.requireNonNull(error));
                        }

                    }
                });
    }

    private void startComponents() {
        txtErrorMsg = binding.txtErrorMsg;
        txtSignIn = binding.txtSingIn;
        btLogin = binding.btLogin;
        editEmail = binding.editEmail;
        editPassword = binding.editPassword;
        progressBar = binding.progressBar;
    }

    private void cleanComponents() {
        editEmail.setText("");
        editPassword.setText("");
        txtErrorMsg.setText("");
    }
}