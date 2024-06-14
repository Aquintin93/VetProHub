package paquete.app.vetprohub.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import paquete.app.vetprohub.R;

public class EmployeeProfileActivity extends AppCompatActivity {

    private EditText etEmployeeName, etEmployeeSurname, etEmployeeSpecialty;
    private Button btnSaveEmployeeProfile, btnBackToEmployeeMenu;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmployeeName = findViewById(R.id.etEmployeeName);
        etEmployeeSurname = findViewById(R.id.etEmployeeSurname);
        etEmployeeSpecialty = findViewById(R.id.etEmployeeSpecialty);
        btnSaveEmployeeProfile = findViewById(R.id.btnSaveEmployeeProfile);
        btnBackToEmployeeMenu = findViewById(R.id.btnBackToEmployeeMenu);

        btnSaveEmployeeProfile.setOnClickListener(view -> saveEmployeeProfile());
        btnBackToEmployeeMenu.setOnClickListener(view -> {
            startActivity(new Intent(EmployeeProfileActivity.this, EmployeeMenuActivity.class));
            finish();
        });

        loadEmployeeProfile();
    }

    private void loadEmployeeProfile() {
        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("employees").document(uid);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String surname = documentSnapshot.getString("surname");
                String specialty = documentSnapshot.getString("specialty");

                etEmployeeName.setText(name);
                etEmployeeSurname.setText(surname);
                etEmployeeSpecialty.setText(specialty);
            }
        });
    }

    private void saveEmployeeProfile() {
        String uid = mAuth.getCurrentUser().getUid();
        String name = etEmployeeName.getText().toString().trim();
        String surname = etEmployeeSurname.getText().toString().trim();
        String specialty = etEmployeeSpecialty.getText().toString().trim();

        if (name.isEmpty() || surname.isEmpty() || specialty.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> employeeData = new HashMap<>();
        employeeData.put("name", name);
        employeeData.put("surname", surname);
        employeeData.put("specialty", specialty);

        DocumentReference docRef = db.collection("employees").document(uid);
        docRef.set(employeeData).addOnSuccessListener(aVoid -> {
            Toast.makeText(EmployeeProfileActivity.this, "Perfil guardado con éxito.", Toast.LENGTH_SHORT).show();
            moveto();
        }).addOnFailureListener(e -> {
            Toast.makeText(EmployeeProfileActivity.this, "Error al guardar el perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void moveto() {
        startActivity(new Intent( this,EmployeeMenuActivity.class));
        finish();
    }
}
