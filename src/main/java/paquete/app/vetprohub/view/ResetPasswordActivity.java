package paquete.app.vetprohub.view;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import paquete.app.vetprohub.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmailReset;
    private TextInputLayout tilEmailReset;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();  // Inicializar Firebase Auth

        etEmailReset = findViewById(R.id.etEmailReset);
        tilEmailReset = findViewById(R.id.tilEmailReset);
        MaterialButton btnReset = findViewById(R.id.btnResetReset);
        MaterialButton btnLoginReset = findViewById(R.id.btnLoginReset);

        btnReset.setOnClickListener(v -> resetPassword());
        btnLoginReset.setOnClickListener(v -> finish());
    }

    private void resetPassword() {
        String email = etEmailReset.getText().toString().trim();
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmailReset.setError(getString(R.string.invalid_email));
            return;
        }

        // Limpia errores previos
        tilEmailReset.setError(null);

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ResetPasswordActivity.this, getString(R.string.reset_email_sent), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ResetPasswordActivity.this, getString(R.string.reset_email_failed) + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
