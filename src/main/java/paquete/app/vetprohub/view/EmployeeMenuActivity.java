package paquete.app.vetprohub.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import paquete.app.vetprohub.R;

public class EmployeeMenuActivity extends AppCompatActivity {

    private Button btnViewCitations;
    private Button btnProfile;
    private Button btnEmployeeCitations;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_menu);

        btnViewCitations = findViewById(R.id.btnViewCitations);
        btnProfile = findViewById(R.id.btnEmployeeProfile);
        btnEmployeeCitations = findViewById(R.id.btnEmployeeCitations);
        btnLogout = findViewById(R.id.btnLogout);

        btnViewCitations.setOnClickListener(view -> {
            startActivity(new Intent(EmployeeMenuActivity.this, EmployeeCitationsListActivity.class));
        });

        btnProfile.setOnClickListener(view -> {
            startActivity(new Intent(EmployeeMenuActivity.this, EmployeeProfileActivity.class));
        });

        btnEmployeeCitations.setOnClickListener(view -> {
            startActivity(new Intent(EmployeeMenuActivity.this, EmployeeCitationsActivity.class));
        });

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(EmployeeMenuActivity.this, LoginActivity.class));
            finish();
        });
    }
}
