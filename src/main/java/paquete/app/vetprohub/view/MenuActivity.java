package paquete.app.vetprohub.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import paquete.app.vetprohub.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Obtener las referencias a los botones
        Button btnUserProfile = findViewById(R.id.btnUserProfile);
        Button btnAppointments = findViewById(R.id.btnAppointments);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Configurar los listeners de los botones
        btnUserProfile.setOnClickListener(v -> openUserProfile());
        btnAppointments.setOnClickListener(v -> openAppointments());
        btnLogout.setOnClickListener(v -> logout());

    }

    private void openUserProfile() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    private void openAppointments() {
        Intent intent = new Intent(this, CitationActivity.class);
        startActivity(intent);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(this, "Navegando a: " + message, Toast.LENGTH_SHORT).show();
    }
}
