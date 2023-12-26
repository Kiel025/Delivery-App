package com.kiel.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.kiel.deliveryapp.databinding.ActivitySinginBinding;

import de.hdodenhof.circleimageview.CircleImageView;

public class SinginActivity extends AppCompatActivity {

    private ActivitySinginBinding binding;
    private FirebaseAuth auth;

    private CircleImageView userPic;
    private Button btSelectPic, btRegister;
    private EditText editName, editEmail, editPassword;
    private TextView txtErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySinginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        startComponents();

        editName.addTextChangedListener(registerTextWatcher);
        editEmail.addTextChangedListener(registerTextWatcher);
        editPassword.addTextChangedListener(registerTextWatcher);

        btRegister.setOnClickListener(this::userRegister);

    }

    public void startComponents() {
        userPic = binding.userPic;
        btSelectPic = binding.btPic;
        btRegister = binding.btRegister;
        editName = binding.editName;
        editEmail = binding.editEmail;
        editPassword = binding.editPassword;
        txtErrorMessage = binding.txtErrorMsg;
    }

    public void userRegister(View v) {

        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Snackbar snackbar = Snackbar.make(v, "Cadastro realizado com sucesso", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", v1 -> finish());
                snackbar.show();
            } else {
                String error;

                try {
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e) {
                    error = "Digite uma senha com no mínimo 6 caracteres";
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    error = "Informe um email válido.";
                } catch (FirebaseAuthUserCollisionException e) {
                    error = "Usuário já cadastrado.";
                } catch (FirebaseNetworkException e) {
                    error = "Erro de conexão com a internet";
                } catch (Exception e) {
                    error = "Ocorreu um erro ao cadastrar o usuário.";
                }
                txtErrorMessage.setText(error);
            }
        });
    }

    TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String name = editName.getText().toString();
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();

            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                btRegister.setEnabled(true);
                btRegister.setBackgroundColor(getResources().getColor(R.color.dark_red, getResources().newTheme()));
            } else {
                btRegister.setEnabled(false);
                btRegister.setBackgroundColor(getResources().getColor(R.color.gray, getResources().newTheme()));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}