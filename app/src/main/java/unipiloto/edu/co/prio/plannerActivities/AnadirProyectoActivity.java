package unipiloto.edu.co.prio.plannerActivities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import unipiloto.edu.co.prio.BuildConfig;
import unipiloto.edu.co.prio.MainActivity;
import unipiloto.edu.co.prio.MapsActivity;
import unipiloto.edu.co.prio.NotificationService;
import unipiloto.edu.co.prio.PrioDatabaseHelper;
import unipiloto.edu.co.prio.R;

public class AnadirProyectoActivity extends AppCompatActivity {
    private EditText editTextDate;
    private EditText editTextDate2;
    private PrioDatabaseHelper dbHelper;
    private String address;
    private static final int REQUEST_POST_NOTIFICATIONS = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_anadir_proyecto);
        editTextDate = findViewById(R.id.startTextDate);
        editTextDate2 = findViewById(R.id.endTextDate);
        dbHelper = new PrioDatabaseHelper(this);
        Spinner localitySpinner = findViewById(R.id.locality);
        Spinner categorySpinner = findViewById(R.id.category);
        loadLocalities(localitySpinner, categorySpinner);
        Places.initialize(getApplicationContext(), BuildConfig.googleApiKey);
        PlacesClient placesClient = Places.createClient(this);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            private static final String TAG = "AnadirProyectoActivity";
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                address = place.getLatLng().toString();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred google api: " + status);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_POST_NOTIFICATIONS);
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_anadir);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


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
            Intent mapIntent = new Intent(AnadirProyectoActivity.this, MapsActivity.class);
            startActivity(mapIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    public void openDatePicker(View view) {
        Date date = new Date();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                //Showing the picked value in the textView
                editTextDate.setText(String.valueOf(day) + "/" + String.valueOf(month+1) + "/" + String.valueOf(year));

            }
        }, (date.getYear()+1900), (date.getMonth()), (date.getDate()));

        datePickerDialog.show();
    }
    public void openDateendPicker(View view) {
        Date date = new Date();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String startText = editTextDate.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                try {
                    Date startDate = sdf.parse(startText);
                    Date endDate = sdf.parse(day + "/" + (month+1) + "/" + year);
                    if (endDate.before(startDate) || endDate.equals(startDate)) {
                        Toast.makeText(AnadirProyectoActivity.this, "La fecha de finalización no puede ser antes o igual a la fecha de inicio", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                editTextDate2.setText(String.valueOf(day) + "/" + String.valueOf(month+1) + "/" + String.valueOf(year));
            }
        }, (date.getYear()+1900), (date.getMonth()), (date.getDate()));

        datePickerDialog.show();
    }
    private void loadLocalities(Spinner spinner, Spinner spinner2) {
        List<String> localities = dbHelper.getAllLocalities();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, localities);
        spinner.setAdapter(adapter);
        List<String> categories = dbHelper.getAllCategories();
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categories);
        spinner2.setAdapter(adapter2);
    }

    public void agregarProyecto(View view) {
        EditText name = findViewById(R.id.name);
        EditText description = findViewById(R.id.description);
        EditText budget = findViewById(R.id.budget);
        EditText start = findViewById(R.id.startTextDate);
        EditText end = findViewById(R.id.endTextDate);
        Spinner locality = findViewById(R.id.locality);
        Spinner category = findViewById(R.id.category);

        String nameText = name.getText().toString();
        String descriptionText = description.getText().toString();
        String budgetText = budget.getText().toString();
        String startText = start.getText().toString();
        String endText = end.getText().toString();
        String localityText = locality.getSelectedItem().toString();
        String categoryText = category.getSelectedItem().toString();


        int localityId = dbHelper.getLocalityId(locality.getSelectedItem().toString());
        int categoryId = dbHelper.getCategoryId(category.getSelectedItem().toString());
        if (nameText.isEmpty() || descriptionText.isEmpty() || budgetText.isEmpty() || startText.isEmpty() || endText.isEmpty() || categoryText.isEmpty() || localityText.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean insertado = dbHelper.insertProject(nameText, descriptionText,Double.parseDouble(budgetText), startText, endText, categoryId, localityId, address);
        if (insertado) {
            Toast.makeText(this, "Proyecto Agregado", Toast.LENGTH_SHORT).show();
            Intent notificationIntent = new Intent(this, NotificationService.class);
            notificationIntent.putExtra("MESSAGE", "Nuevo proyecto agregado a consideracion!");
            Intent intent = new Intent(AnadirProyectoActivity.this, ManageProyectActivity.class);
            startService(notificationIntent);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al agregar Project", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void logout(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("userEmail");
        editor.apply();

        Intent loginIntent = new Intent(AnadirProyectoActivity.this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }
}