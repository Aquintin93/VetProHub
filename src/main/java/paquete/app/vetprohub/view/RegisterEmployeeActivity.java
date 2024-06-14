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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;
import paquete.app.vetprohub.R;

public class RegisterEmployeeActivity extends AppCompatActivity {

    private EditText etEmployeeNumber, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister, btnBackToLogin;
    private TextInputLayout tilEmployeeNumber, tilEmail, tilPassword, tilConfirmPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmployeeNumber = findViewById(R.id.etEmployeeNumber);
        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etPasswordRegister);
        etConfirmPassword = findViewById(R.id.etConfirmPasswordRegister);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        tilEmployeeNumber = findViewById(R.id.tilEmployeeNumber);
        tilEmail = findViewById(R.id.tilEmailRegister);
        tilPassword = findViewById(R.id.tilPasswordRegister);
        tilConfirmPassword = findViewById(R.id.tilConfirmPasswordRegister);

        btnRegister.setOnClickListener(view -> registerEmployee());
        btnBackToLogin.setOnClickListener(view -> {
            startActivity(new Intent(RegisterEmployeeActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerEmployee() {
        String employeeNumber = etEmployeeNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (employeeNumber.isEmpty()) {
            tilEmployeeNumber.setError("Número de empleado es requerido");
            return;
        } else {
            tilEmployeeNumber.setError(null);
        }

        if (!isEmailValid(email)) {
            tilEmail.setError(getString(R.string.invalid_email));
            return;
        } else {
            tilEmail.setError(null);
        }

        if (!isPasswordValid(password)) {
            tilPassword.setError(getString(R.string.password_requirements));
            return;
        } else {
            tilPassword.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.password_mismatch));
            return;
        } else {
            tilConfirmPassword.setError(null);
        }

        // Verificar si el número de empleado ya existe
        db.collection("employees").whereEqualTo("employeeNumber", employeeNumber).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    tilEmployeeNumber.setError("Número de empleado ya existe");
                } else {
                    // Crear usuario de Firebase
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, authTask -> {
                        if (authTask.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Guardar información del empleado en Firestore
                            Map<String, Object> employeeData = new HashMap<>();
                            employeeData.put("employeeNumber", employeeNumber);
                            employeeData.put("email", email);

                            db.collection("employees").document(user.getUid()).set(employeeData).addOnSuccessListener(aVoid -> {
                                Toast.makeText(RegisterEmployeeActivity.this, "Registro de empleado exitoso.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterEmployeeActivity.this, LoginActivity.class)); // Redirigir a LoginActivity
                                finish();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(RegisterEmployeeActivity.this, "Error al guardar datos del empleado: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                        } else {
                            Toast.makeText(RegisterEmployeeActivity.this, "Error en el registro: " + authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Toast.makeText(RegisterEmployeeActivity.this, "Error al verificar número de empleado: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[A-Z]).{6,}$");
    }

    private boolean isEmployeeNumberValid(String employeeNumber) {
        return employeeNumber.matches("^\\d{6}$");
    }
}
