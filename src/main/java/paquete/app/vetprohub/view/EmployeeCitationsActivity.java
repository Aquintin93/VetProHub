package paquete.app.vetprohub.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import paquete.app.vetprohub.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeCitationsActivity extends AppCompatActivity {

    private Spinner spinnerAnimals;
    private EditText etDate, etTime, etReason;
    private Button btnSaveCitation, btnBackToEmployeeMenu;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String employeeName;
    private String employeeSpecialty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_citations);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        spinnerAnimals = findViewById(R.id.spinnerAnimals);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etReason = findViewById(R.id.etReason);
        btnSaveCitation = findViewById(R.id.btnSaveCitation);
        btnBackToEmployeeMenu = findViewById(R.id.btnBackToEmployeeMenu);

        etDate.setOnClickListener(view -> showDatePicker());
        etTime.setOnClickListener(view -> showTimePicker());

        btnBackToEmployeeMenu.setOnClickListener(view -> finish());

        btnSaveCitation.setOnClickListener(view -> saveCitation());

        loadEmployeeProfile();
        loadAnimals();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            etDate.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis()); // Fecha mínima: hoy
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String selectedTime = hourOfDay + ":" + String.format("%02d", minute1);
            etTime.setText(selectedTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void loadEmployeeProfile() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("employees").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                employeeName = documentSnapshot.getString("name");
                employeeSpecialty = documentSnapshot.getString("specialty");
            } else {
                Toast.makeText(EmployeeCitationsActivity.this, "Error al cargar el perfil del empleado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAnimals() {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> animalList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String userName = document.getString("userName");
                    String userSurname = document.getString("userSurname");
                    db.collection("users").document(document.getId()).collection("pets").get().addOnCompleteListener(petTask -> {
                        if (petTask.isSuccessful()) {
                            for (QueryDocumentSnapshot petDocument : petTask.getResult()) {
                                String petName = petDocument.getString("petName");
                                animalList.add(userName + " " + userSurname + " - " + petName);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(EmployeeCitationsActivity.this, android.R.layout.simple_spinner_item, animalList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerAnimals.setAdapter(adapter);
                        } else {
                            Toast.makeText(EmployeeCitationsActivity.this, "Error al cargar las mascotas: " + petTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(EmployeeCitationsActivity.this, "Error al cargar los usuarios: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCitation() {
        String selectedAnimal = spinnerAnimals.getSelectedItem().toString();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String reason = etReason.getText().toString().trim();

        if (selectedAnimal.isEmpty() || date.isEmpty() || time.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] animalDetails = selectedAnimal.split(" - ");
        String ownerName = animalDetails[0];
        String petName = animalDetails[1];

        String[] ownerNameParts = ownerName.split(" ");
        if (ownerNameParts.length < 2) {
            Toast.makeText(this, "Formato de nombre de dueño inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        String userName = ownerNameParts[0];
        String userSurname = ownerNameParts[1];
        for (int i = 2; i < ownerNameParts.length; i++) {
            userSurname += " " + ownerNameParts[i];
        }

        db.collection("users").whereEqualTo("userName", userName)
                .whereEqualTo("userSurname", userSurname)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String userId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        Map<String, Object> citation = new HashMap<>();
                        citation.put("userId", userId);
                        citation.put("ownerName", ownerName);
                        citation.put("petName", petName);
                        citation.put("date", date);
                        citation.put("time", time);
                        citation.put("reason", reason);
                        citation.put("employeeName", employeeName);
                        citation.put("employeeSpecialty", employeeSpecialty);

                        db.collection("citations")
                                .add(citation)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(EmployeeCitationsActivity.this,
                                            "Cita guardada con éxito", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(EmployeeCitationsActivity.this,
                                        "Error al guardar la cita: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(EmployeeCitationsActivity.this,
                                "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(EmployeeCitationsActivity.this,
                        "Error al buscar el usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
