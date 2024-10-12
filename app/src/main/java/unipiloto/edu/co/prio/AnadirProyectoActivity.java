package unipiloto.edu.co.prio;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


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

public class AnadirProyectoActivity extends AppCompatActivity {
    private EditText editTextDate;
    private EditText editTextDate2;
    private PrioDatabaseHelper dbHelper;
    private String address;

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
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
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


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
            Intent intent = new Intent(AnadirProyectoActivity.this, ManageProyectActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al agregar Project", Toast.LENGTH_SHORT).show();
        }
    }
}