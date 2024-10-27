package unipiloto.edu.co.prio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private PrioDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.titulo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new PrioDatabaseHelper(this);
        Spinner localitySpinner = findViewById(R.id.locality);
        loadLocalities(localitySpinner);
    }

    public void registrar(View view) {
        EditText id = findViewById(R.id.id);
        EditText firstName = findViewById(R.id.firstName);
        EditText lastName = findViewById(R.id.lastName);
        EditText age = findViewById(R.id.age);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        EditText conPassword = findViewById(R.id.conPassword);
        Spinner localitySpinner = findViewById(R.id.locality);

        String idText = id.getText().toString();
        String firstNameText = firstName.getText().toString();
        String lastNameText = lastName.getText().toString();
        String ageText = age.getText().toString();
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        String conPasswordText = conPassword.getText().toString();
        String localityText = localitySpinner.getSelectedItem().toString();

        if (idText.isEmpty() || firstNameText.isEmpty() || lastNameText.isEmpty() || ageText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || conPasswordText.isEmpty() || localityText.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidacionEmail(emailText)) {
            Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        int ageInt = Integer.parseInt(ageText);
        int localityId = dbHelper.getLocalityId(localityText);
        int idInt = Integer.parseInt(idText);

        if (!passwordText.equals(conPasswordText)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Encriptación de la contraseña (opcional)
        // String encryptedPassword = encryptPassword(passwordText);

        boolean insertado = dbHelper.insertUser(idInt, firstNameText, lastNameText, ageInt, emailText, passwordText, 1, localityId);
        if (insertado) {
            Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show();
            Intent registerIntent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(registerIntent);
            finish();
        } else {
            Toast.makeText(this, "ERROR: Cedula y/o email ya registrado", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean ValidacionEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private void loadLocalities(Spinner spinner) {
        List<String> localities = dbHelper.getAllLocalities();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, localities);
        spinner.setAdapter(adapter);
    }
}