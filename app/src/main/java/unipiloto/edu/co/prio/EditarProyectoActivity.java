package unipiloto.edu.co.prio;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditarProyectoActivity extends AppCompatActivity {

    private Project project;
    private String id;
    private TextView textViewId;
    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextBudget;
    private EditText editTextDateEditing;
    private EditText editTextDateEndEditing;
    private Spinner categorySpinner;
    private Spinner localitySpinner;
    private String address;

    private PrioDatabaseHelper dbHelper;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_proyecto);
        dbHelper = new PrioDatabaseHelper(this);

        id = getIntent().getStringExtra("id");
        textViewId = findViewById(R.id.showIdTextView);
        textViewId.setText(textViewId.getText() + id);
        editTextName = findViewById(R.id.nameEditing);
        editTextDescription = findViewById(R.id.descriptionEditing);
        editTextBudget = findViewById(R.id.budgetEditing);
        editTextDateEditing = findViewById(R.id.startTextDateEditing);
        editTextDateEndEditing = findViewById(R.id.endTextDateEditing);
        categorySpinner = findViewById(R.id.categoryEditing);
        localitySpinner = findViewById(R.id.localityEditing);
        loadLocalities(localitySpinner, categorySpinner);


        project = dbHelper.getProjectById(Integer.parseInt(id));

        rellenarCampos();


        Places.initialize(getApplicationContext(), BuildConfig.googleApiKey);
        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_editing);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            private static final String TAG = "EditarProyectoActivity";
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                address = place.getLatLng().toString();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred google api: " + status);
            }
        });
        setAutocompleteFragmentAddress(project.getAddress(), autocompleteFragment);
    }

    public void editarProyecto(View view) {
        String nameText = editTextName.getText().toString();
        String descriptionText = editTextDescription.getText().toString();
        String budgetText = editTextBudget.getText().toString();
        String startText = editTextDateEditing.getText().toString();
        String endText = editTextDateEndEditing.getText().toString();
        String localityText = localitySpinner.getSelectedItem().toString();
        String categoryText = categorySpinner.getSelectedItem().toString();
        address = address == null ? project.getAddress() : address;


        int localityId = dbHelper.getLocalityId(localitySpinner.getSelectedItem().toString());
        int categoryId = dbHelper.getCategoryId(categorySpinner.getSelectedItem().toString());
        if (nameText.isEmpty() || descriptionText.isEmpty() || budgetText.isEmpty() || startText.isEmpty() || endText.isEmpty() || categoryText.isEmpty() || localityText.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean actualizado = dbHelper.updateProject(Integer.parseInt(id), nameText, descriptionText, Double.parseDouble(budgetText), startText, endText, categoryId, localityId, address);
        if (actualizado) {
            Toast.makeText(this, "Proyecto Editado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditarProyectoActivity.this, EliminarEditarProyectoActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al editar Project", Toast.LENGTH_SHORT).show();
        }
    }

    public void rellenarCampos() {
        editTextName.setText(project.getTitle());
        editTextDescription.setText(project.getDescription());
        editTextBudget.setText(String.valueOf(project.getBudget()));
        editTextDateEditing.setText(project.getStartDate());
        editTextDateEndEditing.setText(project.getEndDate());

        String categoryName = dbHelper.getCategoryName(project.getCategoryId());
        String localityName = dbHelper.getLocalityName(project.getLocalityId());

        int categoryPosition = ((ArrayAdapter<String>) categorySpinner.getAdapter()).getPosition(categoryName);
        int localityPosition = ((ArrayAdapter<String>) localitySpinner.getAdapter()).getPosition(localityName);

        categorySpinner.setSelection(categoryPosition);
        localitySpinner.setSelection(localityPosition);
    }

    public void openDatePicker(View view) {
        Date date = new Date();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                editTextDateEditing.setText(String.valueOf(day) + "/" + String.valueOf(month+1) + "/" + String.valueOf(year));

            }
        }, (date.getYear()+1900), (date.getMonth()), (date.getDate()));

        datePickerDialog.show();
    }
    public void openDateendPicker(View view) {
        Date date = new Date();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                editTextDateEndEditing.setText(String.valueOf(day) + "/" + String.valueOf(month+1) + "/" + String.valueOf(year));

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

    private void setAutocompleteFragmentAddress(String latLngString, AutocompleteSupportFragment autocompleteFragment) {
        String[] latLng = latLngString.replace("lat/lng: (", "").replace(")", "").split(",");
        double latitude = Double.parseDouble(latLng[0]);
        double longitude = Double.parseDouble(latLng[1]);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressString = address.getAddressLine(0);
                autocompleteFragment.setText(addressString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}