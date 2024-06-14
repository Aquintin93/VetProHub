package paquete.app.vetprohub.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import paquete.app.vetprohub.R;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword, tilPasswordConfirm;
    private TextInputEditText etEmail, etPassword, etPasswordConfirm;
    private MaterialButton btnRegister, btnToLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance(); // Inicializa Firebase Auth

        // Initialize views
        initViews();

        // Set click listeners
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();  // Assuming LoginActivity is the parent activity
            }
        });
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmailRegistration);
        tilPassword = findViewById(R.id.tilPasswordRegistration);
        tilPasswordConfirm = findViewById(R.id.tilPasswordConfirmRegistration);

        etEmail = findViewById(R.id.etEmailRegistration);
        etPassword = findViewById(R.id.etPasswordRegistration);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirmRegistration);

        btnRegister = findViewById(R.id.btnRegistrationRegistration);
        btnToLogin = findViewById(R.id.btnLoginRegistration);
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etPasswordConfirm.getText().toString().trim();

        if (!validateForm(email, password, confirmPassword)) {
            return;
        }

        // Registro del usuario con Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registro exitoso, actualizar UI con la información del usuario registrado
                        Toast.makeText(RegistrationActivity.this, "Se Registro correctamente.", Toast.LENGTH_SHORT).show();
                        finish(); // Cierra esta actividad y regresa a la pantalla de login
                    } else {
                        // Falla el registro, mostrar un mensaje al usuario
                        Toast.makeText(RegistrationActivity.this, "Registro fallido: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateForm(String email, String password, String confirmPassword) {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email no válido");
            valid = false;
        } else {
            tilEmail.setError(null);
        }

        // Actualizado para incluir al menos un número y una letra mayúscula
        if (password.isEmpty() || !password.matches("^(?=.*[0-9])(?=.*[A-Z]).{6,}$")) {
            tilPassword.setError("La contraseña debe tener al menos 6 caracteres, contener al menos un número y una letra mayúscula.");
            valid = false;
        } else {
            tilPassword.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            tilPasswordConfirm.setError("Las contraseñas no coinciden.");
            valid = false;
        } else {
            tilPasswordConfirm.setError(null);
        }

        return valid;
    }

}
