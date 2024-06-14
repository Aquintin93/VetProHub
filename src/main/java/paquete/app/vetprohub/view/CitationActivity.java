package paquete.app.vetprohub.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import paquete.app.vetprohub.R;
import paquete.app.vetprohub.view.adapter.CitationAdapter;
import paquete.app.vetprohub.model.Citation;

import java.util.ArrayList;
import java.util.List;

public class CitationActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CitationAdapter citationAdapter;
    private List<Citation> citationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citation);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        RecyclerView rvCitations = findViewById(R.id.rvCitations);
        rvCitations.setLayoutManager(new LinearLayoutManager(this));
        citationList = new ArrayList<>();
        citationAdapter = new CitationAdapter(citationList);
        rvCitations.setAdapter(citationAdapter);

        loadCitations();

        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnBackToMenu.setOnClickListener(v -> backToMenu());
    }

    private void loadCitations() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("citations")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        citationList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Citation citation = document.toObject(Citation.class);
                            citation.setId(document.getId()); // Establecer el ID de la cita
                            citationList.add(citation);
                        }
                        citationAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CitationActivity.this, "Error al cargar las citas: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(CitationActivity.this, "Error al buscar las citas: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void backToMenu() {
        Intent intent = new Intent(CitationActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}
