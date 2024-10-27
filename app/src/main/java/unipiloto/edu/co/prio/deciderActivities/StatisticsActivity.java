// StatisticsActivity.java
package unipiloto.edu.co.prio.deciderActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import unipiloto.edu.co.prio.CustomAdapter;
import unipiloto.edu.co.prio.MainActivity;
import unipiloto.edu.co.prio.MapsActivity;
import unipiloto.edu.co.prio.PrioDatabaseHelper;
import unipiloto.edu.co.prio.Project;
import unipiloto.edu.co.prio.R;

public class StatisticsActivity extends AppCompatActivity {

    private PrioDatabaseHelper dbHelper;
    private CustomAdapter listAdapter;
    private DrawerLayout drawerLayout;
    private ArrayList<Project> projects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        dbHelper = new PrioDatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        projects = dbHelper.getAllProjects();
        Collections.sort(projects, (p1, p2) -> Integer.compare(dbHelper.getVotes(p2.getId()).size(), dbHelper.getVotes(p1.getId()).size()));
        ListView listView = findViewById(R.id.listView_Statistics);
        SearchView searchView = findViewById(R.id.busqueda_Statistics);
        ImageButton filterButton = findViewById(R.id.filter_buttonStatistics);
        drawerLayout = findViewById(R.id.drawer_layout_Statistics);
        NavigationView navigationView = findViewById(R.id.nav_viewStatistics);
        Switch switchOrder = findViewById(R.id.switchOrder);
        loadMenuItems();

        listAdapter = new CustomAdapter(this, R.layout.card_item, projects, dbHelper);
        listView.setAdapter(listAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.getFilter().filter(newText);
                return false;
            }
        });

        switchOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Collections.sort(listAdapter.filteredList, (p1, p2) -> Double.compare(getAprobacion(dbHelper.getVotes(p2.getId())), getAprobacion(dbHelper.getVotes(p1.getId()))));
            } else {
                Collections.sort(listAdapter.filteredList, (p1, p2) -> Integer.compare(dbHelper.getVotes(p2.getId()).size(), dbHelper.getVotes(p1.getId()).size()));
            }
            listAdapter.notifyDataSetChanged();
        });

        filterButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.isChecked()) {
                    item.setChecked(false);
                    listAdapter.getFilter().filter("");
                } else {
                    item.setChecked(true);
                    String filter = item.getTitle().toString();
                    if (item.getItemId() == 1) {
                        filter = "loc " + dbHelper.getLocalityId(filter);
                    } else if (item.getItemId() == 2) {
                        filter = "cat " + dbHelper.getCategoryId(filter);
                    }
                    listAdapter.getFilter().filter(filter);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private double getAprobacion(List<Integer> votes) {
        int aFavor = 0;
        for (int i : votes) {
            if (i == 1) {
                aFavor++;
            }
        }
        return aFavor / (double) votes.size() * 100;
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
            Intent mapIntent = new Intent(StatisticsActivity.this, MapsActivity.class);
            startActivity(mapIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void loadMenuItems() {
        NavigationView navigationView = findViewById(R.id.nav_viewStatistics);
        Menu menu = navigationView.getMenu();
        menu.clear();

        List<String> localities = dbHelper.getAllLocalities();
        List<String> categories = dbHelper.getAllCategories();

        SubMenu subMenu = menu.addSubMenu(menu.NONE, 1, menu.NONE, "Localidades");
        for (String locality : localities) {
            MenuItem menuItem = subMenu.add(menu.NONE, 1, menu.NONE, locality);
            menuItem.setCheckable(true);
        }
        SubMenu subMenu2 = menu.addSubMenu(menu.NONE, 2, menu.NONE, "Categorías");
        for (String category : categories) {
            MenuItem menuItem = subMenu2.add(menu.NONE, 2, menu.NONE, category);
            menuItem.setCheckable(true);
        }
    }

    public void logout(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("userEmail");
        editor.apply();

        Intent loginIntent = new Intent(StatisticsActivity.this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }
}