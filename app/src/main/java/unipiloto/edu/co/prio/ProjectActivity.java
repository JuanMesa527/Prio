package unipiloto.edu.co.prio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ProjectActivity extends AppCompatActivity implements OnMapReadyCallback {

    private PrioDatabaseHelper dbHelper;
    private double lat, lng;
    private String title;
    private Project item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        dbHelper = new PrioDatabaseHelper(this);

        ImageView logoImageView = findViewById(R.id.logoImageView);
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView budgetTextView = findViewById(R.id.budgetTextView);
        TextView datesTextView = findViewById(R.id.datesTextView);
        TextView categoryTextView = findViewById(R.id.categoryTextView);
        TextView localityTextView = findViewById(R.id.localityTextView);

        item = getIntent().getParcelableExtra("item");

        if (item != null) {
            logoImageView.setImageResource(item.getLogoResId());
            titleTextView.setText(item.getTitle());
            descriptionTextView.setText(item.getDescription());
            budgetTextView.setText("Presupuesto: " + item.getBudget());
            datesTextView.setText("Fechas: " + item.getStartDate() + " - " + item.getEndDate());
            categoryTextView.setText("Categoría: " + dbHelper.getCategoryName(item.getCategoryId()));
            localityTextView.setText("Localidad: " + dbHelper.getLocalityName(item.getLocalityId()));
        }

        String localizacion = item.getAddress();
        String salida = localizacion.substring(10, localizacion.indexOf(')'));
        String[] partes = salida.split(",");
        lat=Double.parseDouble(partes[0]);
        lng=Double.parseDouble(partes[1]);
        title = item.getTitle();



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_icon) {
            logout(null);
            return true;
        } else if (item.getItemId() == R.id.map_icon) {
            Intent mapIntent = new Intent(ProjectActivity.this, MapsActivity.class);
            startActivity(mapIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng initialLocation = new LatLng(lat, lng);
        googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(initialLocation, 15));
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(title));
    }

    public void votar(View view) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        EditText comentario = findViewById(R.id.commentsEditText);

        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);

        if (radioButton != null) {
            int voteId = dbHelper.getVoteTypeId(radioButton.getText().toString());
            String comment = comentario.getText().toString();

            if (voteId > 0) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("userId", -1);

                boolean isVoteable = dbHelper.isVoteable(userId,item.getId());
                if (isVoteable) {
                    dbHelper.insertVote(userId, item.getId(), voteId, comment);
                    Toast.makeText(this, "Votación realizada con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Ya ha votado por este proyecto", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "interno", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Por favor, seleccione una puntuación", Toast.LENGTH_SHORT).show();
        }
    }
    public void logout(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("userEmail");
        editor.apply();

        Intent loginIntent = new Intent(ProjectActivity.this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }
}