package paquete.app.vetprohub.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import paquete.app.vetprohub.R;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private EditText etUserName, etUserSurname;
    private Button btnSaveUserProfile, btnBackToMenu, btnAddAnimal;
    private LinearLayout animalContainer;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etUserName = findViewById(R.id.etUserName);
        etUserSurname = findViewById(R.id.etUserSurname);
        btnSaveUserProfile = findViewById(R.id.btnSaveUserProfile);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnAddAnimal = findViewById(R.id.btnAddAnimal);
        animalContainer = findViewById(R.id.animalContainer);

        btnSaveUserProfile.setOnClickListener(v -> saveUserProfile());
        btnBackToMenu.setOnClickListener(v -> backToMenu());
        btnAddAnimal.setOnClickListener(v -> addAnimalFields());

        // Cargar el perfil del usuario
        loadUserProfile();
    }

    private void addAnimalFields() {
        View animalView = getLayoutInflater().inflate(R.layout.animal_fields, null);

        // Añadir botón de eliminación
        Button btnDeleteAnimal = animalView.findViewById(R.id.btnDeleteAnimal);
        btnDeleteAnimal.setOnClickListener(v -> animalContainer.removeView(animalView));

        animalContainer.addView(animalView);
    }

    private void saveUserProfile() {
        String userName = etUserName.getText().toString().trim();
        String userSurname = etUserSurname.getText().toString().trim();

        if (userName.isEmpty() || userSurname.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un HashMap para almacenar los datos del usuario
        Map<String, Object> user = new HashMap<>();
        user.put("userName", userName);
        user.put("userSurname", userSurname);

        // Verificar que el usuario esté autenticado
        if (mAuth.getCurrentUser() != null) {
            db.collection("users").document(mAuth.getCurrentUser().getUid())
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> saveAnimals())
                    .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Error al guardar el perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Usuario no autenticado, no se puede guardar el perfil.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAnimals() {
        if (mAuth.getCurrentUser() != null) {
            for (int i = 0; i < animalContainer.getChildCount(); i++) {
                View animalView = animalContainer.getChildAt(i);

                EditText etPetName = animalView.findViewById(R.id.etPetName);
                EditText etPetSpecies = animalView.findViewById(R.id.etPetSpecies);
                EditText etPetAge = animalView.findViewById(R.id.etPetAge);

                String petName = etPetName.getText().toString().trim();
                String petSpecies = etPetSpecies.getText().toString().trim();
                String petAge = etPetAge.getText().toString().trim();

                if (petName.isEmpty() || petSpecies.isEmpty() || petAge.isEmpty()) {
                    Toast.makeText(this, "Por favor, complete todos los campos del animal", Toast.LENGTH_SHORT).show();
                    return;
                }

                String petId = (String) animalView.getTag(); // Obtener el ID del documento del animal

                // Si el nombre del animal ha cambiado, eliminar el documento antiguo y crear uno nuevo
                if (petId != null && !petId.equals(petName)) {
                    db.collection("users").document(mAuth.getCurrentUser().getUid())
                            .collection("pets").document(petId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                // Crear un nuevo documento con el nuevo nombre
                                createOrUpdatePetDocument(petName, petSpecies, petAge);
                            })
                            .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Error al actualizar el nombre del animal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    createOrUpdatePetDocument(petName, petSpecies, petAge);
                }

                // Actualizar el ID del documento del animal
                animalView.setTag(petName);
            }
        }
    }

    private void createOrUpdatePetDocument(String petName, String petSpecies, String petAge) {
        Map<String, Object> pet = new HashMap<>();
        pet.put("petName", petName);
        pet.put("petSpecies", petSpecies);
        pet.put("petAge", petAge);

        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("pets").document(petName)
                .set(pet, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast.makeText(UserProfileActivity.this, "Perfil guardado con éxito.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Error al guardar el perfil del animal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        backToMenu();
    }

    private void loadUserProfile() {
        if (mAuth.getCurrentUser() != null) {
            db.collection("users").document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            etUserName.setText(documentSnapshot.getString("userName"));
                            etUserSurname.setText(documentSnapshot.getString("userSurname"));
                        } else {
                            Toast.makeText(UserProfileActivity.this, "Perfil no encontrado.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Error al cargar el perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            db.collection("users").document(mAuth.getCurrentUser().getUid())
                    .collection("pets").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                View animalView = getLayoutInflater().inflate(R.layout.animal_fields, null);

                                EditText etPetName = animalView.findViewById(R.id.etPetName);
                                EditText etPetSpecies = animalView.findViewById(R.id.etPetSpecies);
                                EditText etPetAge = animalView.findViewById(R.id.etPetAge);

                                String petId = document.getId();
                                String petName = document.getString("petName");
                                etPetName.setText(petName);
                                etPetSpecies.setText(document.getString("petSpecies"));
                                etPetAge.setText(document.getString("petAge"));

                                // Establecer el ID del documento del animal como etiqueta oculta
                                animalView.setTag(petId);

                                // Añadir botón de eliminación
                                Button btnDeleteAnimal = animalView.findViewById(R.id.btnDeleteAnimal);
                                btnDeleteAnimal.setOnClickListener(v -> {
                                    db.collection("users").document(mAuth.getCurrentUser().getUid())
                                            .collection("pets").document(petId)
                                            .delete()
                                            .addOnSuccessListener(aVoid -> {
                                                animalContainer.removeView(animalView);
                                                Toast.makeText(UserProfileActivity.this, "Animal eliminado con éxito.", Toast.LENGTH_SHORT).show();
                                                backToMenu();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Error al eliminar el animal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                });

                                animalContainer.addView(animalView);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Error al cargar el perfil del animal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Usuario no autenticado, no se puede cargar el perfil.", Toast.LENGTH_SHORT).show();
        }
    }

    private void backToMenu() {
        Intent intent = new Intent(UserProfileActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}
