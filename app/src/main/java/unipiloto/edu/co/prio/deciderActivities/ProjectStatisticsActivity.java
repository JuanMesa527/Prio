package unipiloto.edu.co.prio.deciderActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;

import unipiloto.edu.co.prio.MainActivity;
import unipiloto.edu.co.prio.MapsActivity;
import unipiloto.edu.co.prio.PrioDatabaseHelper;
import unipiloto.edu.co.prio.Project;
import unipiloto.edu.co.prio.R;
import unipiloto.edu.co.prio.citizenActivities.ProjectActivity;

public class ProjectStatisticsActivity extends AppCompatActivity {

    private PrioDatabaseHelper dbHelper;
    private PieChart pieChart;
    private BarChart barChart;
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
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);


        item = getIntent().getParcelableExtra("item");

        if (item != null) {
            titleTextView.setText(item.getTitle());
            categoryTextView.setText("Categoría: " + dbHelper.getCategoryName(item.getCategoryId()));
            localityTextView.setText("Localidad: " + dbHelper.getLocalityName(item.getLocalityId()));
        }
        fillBarChart();

        fillPieChart();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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

    public void fillBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        List<String> localities = dbHelper.getLocalityByVote(item.getId());
        List<String> xAxisLabels = new ArrayList<>(localities);

        for (int i = 0; i < localities.size(); i++) {
            String locality = localities.get(i);
            int votes = dbHelper.getVotesByLocality(item.getId(), locality).size();
            entries.add(new BarEntry(i , votes));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Votos");
        barDataSet.setColors(Color.GREEN, Color.YELLOW, Color.RED);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.setDrawGridBackground(false);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(2000);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setGranularity(1f);
        yAxis.setGranularityEnabled(true);
        barChart.getAxisRight().setEnabled(false);

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