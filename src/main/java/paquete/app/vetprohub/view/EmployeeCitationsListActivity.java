package paquete.app.vetprohub.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import paquete.app.vetprohub.R;
import paquete.app.vetprohub.view.adapter.EmployeeCitationAdapter;
import paquete.app.vetprohub.model.Citation;

import java.util.ArrayList;
import java.util.List;

public class EmployeeCitationsListActivity extends AppCompatActivity implements EmployeeCitationAdapter.OnCitationClickListener {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EmployeeCitationAdapter citationAdapter;
    private List<Citation> citationList;
    private static final String TAG = "EmployeeCitationsList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_citations_list);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        RecyclerView rvCitations = findViewById(R.id.rvCitations);
        rvCitations.setLayoutManager(new LinearLayoutManager(this));
        citationList = new ArrayList<>();
        citationAdapter = new EmployeeCitationAdapter(citationList, this);
        rvCitations.setAdapter(citationAdapter);

        loadCitations();

        findViewById(R.id.btnBackToMenu).setOnClickListener(v -> finish());
    }

    private void loadCitations() {
        String userId = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "Usuario autenticado con ID: " + userId);

        db.collection("employees").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d(TAG, "El usuario es un empleado.");
                db.collection("citations")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Consulta de citas completada exitosamente");
                                citationList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Citation citation = document.toObject(Citation.class);
                                    citation.setId(document.getId());
                                    citationList.add(citation);
                                    Log.d(TAG, "Cita agregada: " + citation.toString());
                                }
                                citationAdapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "Error al cargar las citas", task.getException());
                                Toast.makeText(EmployeeCitationsListActivity.this,
                                        "Error al cargar las citas: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error al buscar las citas", e);
                            Toast.makeText(EmployeeCitationsListActivity.this,
                                    "Error al buscar las citas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Log.e(TAG, "El usuario no es un empleado.");
                Toast.makeText(EmployeeCitationsListActivity.this,
                        "Permisos insuficientes", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error al verificar si el usuario es empleado", e);
            Toast.makeText(EmployeeCitationsListActivity.this, "Error al verificar permisos", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onEditClick(Citation citation) {
        Intent intent = new Intent(this, EditCitationActivity.class);
        intent.putExtra("citationId", citation.getId());
        intent.putExtra("ownerName", citation.getOwnerName());
        intent.putExtra("petName", citation.getPetName());
        intent.putExtra("date", citation.getDate());
        intent.putExtra("time", citation.getTime());
        intent.putExtra("reason", citation.getReason());
        intent.putExtra("employeeName", citation.getEmployeeName());
        intent.putExtra("employeeSpecialty", citation.getEmployeeSpecialty());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Citation citation) {
        db.collection("citations").document(citation.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    citationList.remove(citation);
                    citationAdapter.notifyDataSetChanged();
                    Toast.makeText(EmployeeCitationsListActivity.this, "Cita eliminada con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al eliminar la cita", e);
                    Toast.makeText(EmployeeCitationsListActivity.this, "Error al eliminar la cita: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
