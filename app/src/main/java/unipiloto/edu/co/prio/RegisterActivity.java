package unipiloto.edu.co.prio;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private PrioDatabaseHelper dbHelper;
    FusedLocationProviderClient fusedLocationProviderClient;
    String locality;
    Button obtener;

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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        obtener = findViewById(R.id.btnObtenerLoc);
        dbHelper = new PrioDatabaseHelper(this);
    }

    public void registrar(View view) {
        EditText id = findViewById(R.id.id);
        EditText firstName = findViewById(R.id.firstName);
        EditText lastName = findViewById(R.id.lastName);
        EditText age = findViewById(R.id.age);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        EditText conPassword = findViewById(R.id.conPassword);

        String idText = id.getText().toString();
        String firstNameText = firstName.getText().toString();
        String lastNameText = lastName.getText().toString();
        String ageText = age.getText().toString();
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        String conPasswordText = conPassword.getText().toString();
        String localityText = locality;

        if (idText.isEmpty() || firstNameText.isEmpty() || lastNameText.isEmpty() || ageText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || conPasswordText.isEmpty() || localityText.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidacionEmail(emailText)) {
            Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        int ageInt = Integer.parseInt(ageText);
        localityText = quitarTildes(locality);
        int localityId = dbHelper.getLocalityId(localityText);
        int idInt = Integer.parseInt(idText);

        if (!passwordText.equals(conPasswordText)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }


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

    public void obtainLoc(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obtenerLocalizacion();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    public void obtenerLocalizacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        locality = addresses.get(0).getSubLocality();
                        obtener.setText(locality);
                        obtener.setTextColor(Color.parseColor("#FFFFFF"));
                        obtener.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static String quitarTildes(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
}