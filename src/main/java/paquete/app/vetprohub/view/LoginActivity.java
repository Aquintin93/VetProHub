package paquete.app.vetprohub.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import paquete.app.vetprohub.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister, btnRegisterEmployee, btnForgotPassword;
    private TextInputLayout tilEmail, tilPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLoginLogin);
        btnRegister = findViewById(R.id.btnRegisterLogin);
        btnRegisterEmployee = findViewById(R.id.btnRegisterEmployee);
        btnForgotPassword = findViewById(R.id.btnForgotLogin);
        tilEmail = findViewById(R.id.tilEmailLogin);
        tilPassword = findViewById(R.id.tilPasswordLogin);

        btnLogin.setOnClickListener(view -> login());
        btnRegister.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
        btnRegisterEmployee.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterEmployeeActivity.class)));
        btnForgotPassword.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class)));
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!isEmailValid(email)) {
            tilEmail.setError(getString(R.string.invalid_email));
            return;
        }

        if (!isPasswordValid(password)) {
            tilPassword.setError(getString(R.string.password_requirements));
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    checkUserType(user.getUid());
                }
            } else {
                Toast.makeText(LoginActivity.this, "Autenticación fallida: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkUserType(String uid) {
        db.collection("employees").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Usuario es empleado
                startActivity(new Intent(LoginActivity.this, EmployeeMenuActivity.class));
            } else {
                // Usuario es cliente
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
            }
            finish(); // Finaliza la actividad actual
        });
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[A-Z]).{6,}$");
    }
}
