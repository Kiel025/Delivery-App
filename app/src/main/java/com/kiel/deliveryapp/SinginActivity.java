package com.kiel.deliveryapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kiel.deliveryapp.databinding.ActivitySinginBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SinginActivity extends AppCompatActivity {

    private ActivitySinginBinding binding;
    private FirebaseAuth auth;

    private CircleImageView userPic;
    private Button btSelectPic, btRegister;
    private EditText editName, editEmail, editPassword;
    private TextView txtErrorMessage;

    private String userID;

    private Uri mSelectUri;

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

        btSelectPic.setOnClickListener(this::selectPictureGallery);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    mSelectUri = data.getData();

                    try {
                        userPic.setImageURI(mSelectUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    public void selectPictureGallery(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    private void saveUserData() {
        String fileName = UUID.randomUUID().toString();

        final StorageReference reference = FirebaseStorage.getInstance().getReference("/images/" + fileName);

        reference.putFile(mSelectUri)
                .addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String pic = uri.toString();

                    // Start firestore database
                    String name = editName.getText().toString();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> users = new HashMap<>();
                    users.put("name", name);
                    users.put("user_pic", pic);

                    userID = Objects.requireNonNull(auth.getCurrentUser()).getUid();

                    DocumentReference documentReference = db.collection("Users").document(userID);
                    documentReference.set(users)
                            .addOnSuccessListener(unused -> Log.i("db", "Sucesso ao salvar os dados"))
                            .addOnFailureListener(e -> Log.i("db", "Erro ao salvar os dados" + e));


                }).addOnFailureListener(e -> {

                }))
                .addOnFailureListener(e -> {

                });
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
                saveUserData();
                Snackbar snackbar = Snackbar.make(v, "Cadastro realizado com sucesso", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", v1 -> finish());
                snackbar.show();
            } else {
                String error;

                try {
                    throw Objects.requireNonNull(task.getException());
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