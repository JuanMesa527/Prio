package unipiloto.edu.co.prio.deciderActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import unipiloto.edu.co.prio.MainActivity;
import unipiloto.edu.co.prio.MapsActivity;
import unipiloto.edu.co.prio.PrioDatabaseHelper;
import unipiloto.edu.co.prio.Project;
import unipiloto.edu.co.prio.R;
import unipiloto.edu.co.prio.citizenActivities.ProjectActivity;

public class ProjectStatisticsActivity extends AppCompatActivity {

    private PrioDatabaseHelper dbHelper;
    private String title;
    private Project item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project_statistics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        dbHelper = new PrioDatabaseHelper(this);


        TextView titleTextView = findViewById(R.id.titleTextView_ProjectStatistics);
        TextView categoryTextView = findViewById(R.id.categoryTextView_ProjectStatistics);
        TextView localityTextView = findViewById(R.id.localityTextView_ProjectStatistics);

        item = getIntent().getParcelableExtra("item");

        if (item != null) {
            titleTextView.setText(item.getTitle());
            categoryTextView.setText("Categoría: " + dbHelper.getCategoryName(item.getCategoryId()));
            localityTextView.setText("Localidad: " + dbHelper.getLocalityName(item.getLocalityId()));
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
            Intent mapIntent = new Intent(ProjectStatisticsActivity.this, MapsActivity.class);
            startActivity(mapIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    public void logout(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("userEmail");
        editor.apply();

        Intent loginIntent = new Intent(ProjectStatisticsActivity.this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }
}