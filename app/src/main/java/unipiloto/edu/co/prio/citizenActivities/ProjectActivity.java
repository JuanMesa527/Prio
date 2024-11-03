package unipiloto.edu.co.prio.citizenActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import unipiloto.edu.co.prio.MainActivity;
import unipiloto.edu.co.prio.MapsActivity;
import unipiloto.edu.co.prio.PrioDatabaseHelper;
import unipiloto.edu.co.prio.Project;
import unipiloto.edu.co.prio.R;

public class ProjectActivity extends AppCompatActivity implements OnMapReadyCallback {

    private PrioDatabaseHelper dbHelper;
    private double lat, lng;
    private PieChart pieChart;
    private String title;
    private Project item;
    private boolean isVoteable;

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
        pieChart = findViewById(R.id.pieChartPublic);

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

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);

        isVoteable = dbHelper.isVoteable(userId,item.getId());
        if (!isVoteable) {
            fillPieChart();
        }else {
            pieChart.setVisibility(View.GONE);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void fillPieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        List<Integer> votes = dbHelper.getVotes(item.getId());
        int totalVotes = votes.size();
        int aFavor = 0;
        int neutral = 0;
        int enContra = 0;
        for(int i : votes) {
            if (i == 1) {
                aFavor++;
            } else if (i == 2) {
                neutral++;
            } else {
                enContra++;
            }
        }
        entries.add(new PieEntry(aFavor/(float)totalVotes*100, "A Favor"));
        entries.add(new PieEntry(neutral/(float)totalVotes*100, "Indiferente"));
        entries.add(new PieEntry(enContra/(float)totalVotes*100, "En Contra"));

        PieDataSet pieDataSet = new PieDataSet(entries, "Votos");
        pieDataSet.setColors(Color.GREEN, Color.YELLOW, Color.RED);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setUsePercentValues(true);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Votos");
        pieChart.animateY(2000);
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

                isVoteable = dbHelper.isVoteable(userId,item.getId());
                if (isVoteable) {
                    dbHelper.insertVote(userId, item.getId(), voteId, comment);
                    Toast.makeText(this, "Votación realizada con éxito", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProjectActivity.this, ProjectActivity.class);
                    intent.putExtra("item", item);
                    startActivity(intent);
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