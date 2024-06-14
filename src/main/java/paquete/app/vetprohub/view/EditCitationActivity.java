package paquete.app.vetprohub.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import paquete.app.vetprohub.R;

public class EditCitationActivity extends AppCompatActivity {

    private EditText etDate, etTime, etReason;
    private Button btnUpdateCitation, btnDeleteCitation, btnCancel;
    private FirebaseFirestore db;
    private String citationId;
    private static final String TAG = "EditCitationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_citation);

        db = FirebaseFirestore.getInstance();

        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etReason = findViewById(R.id.etReason);
        btnUpdateCitation = findViewById(R.id.btnUpdateCitation);
        btnDeleteCitation = findViewById(R.id.btnDeleteCitation);
        btnCancel = findViewById(R.id.btnCancel);

        Intent intent = getIntent();
        citationId = intent.getStringExtra("citationId");
        etDate.setText(intent.getStringExtra("date"));
        etTime.setText(intent.getStringExtra("time"));
        etReason.setText(intent.getStringExtra("reason"));

        btnUpdateCitation.setOnClickListener(view -> updateCitation());
        btnDeleteCitation.setOnClickListener(view -> deleteCitation());
        btnCancel.setOnClickListener(view -> finish());
    }

    private void updateCitation() {
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String reason = etReason.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("citations").document(citationId)
                .update("date", date, "time", time, "reason", reason)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Cita actualizada con éxito");
                    Toast.makeText(EditCitationActivity.this, "Cita actualizada con éxito", Toast.LENGTH_SHORT).show();
                 moveto();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar la cita", e);
                    Toast.makeText(EditCitationActivity.this, "Error al actualizar la cita: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void moveto() {
            startActivity(new Intent(this, EmployeeCitationsListActivity.class));
        finish(); // Finaliza la actividad actual
    }

    private void deleteCitation() {
        db.collection("citations").document(citationId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Cita eliminada con éxito");
                    Toast.makeText(EditCitationActivity.this, "Cita eliminada con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al eliminar la cita", e);
                    Toast.makeText(EditCitationActivity.this, "Error al eliminar la cita: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
