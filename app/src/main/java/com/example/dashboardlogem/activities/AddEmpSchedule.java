package com.example.dashboardlogem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dashboardlogem.R;
//import com.example.dashboardlogem.databi.ActivityAddEmpScheduleBinding;
import com.example.dashboardlogem.admin.AdminDashboard;
import com.example.dashboardlogem.admin_fragments.AdminHomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEmpSchedule extends AppCompatActivity {

//    ActivityAddEmpScheduleBinding binding;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String eName, eEmail, date;
    CalendarView calendar;
    Button btnCancel, btnConfirm;
    TextView empName, empEmail;
    List<String> scheduledDates = new ArrayList<>();

    private static final String TAG = "AddEmpSchedule";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        View view = binding.getRoot();
        setContentView(R.layout.activity_add_emp_schedule);
        Bundle bundle = getIntent().getExtras();
        String curEmpName = bundle.getString("clickedEmpName", "No Value");
        String curEmpEmail = bundle.getString("clickedEmpEmail", "No Value");
        String curEmpId = bundle.getString("clickedEmpId", "No Value");
        empName = findViewById(R.id.txtViewEmpName);
        empEmail = findViewById(R.id.txtViewEmpEmail);
        TextView selectedDate;
        empName.setText(curEmpName);
        empEmail.setText(curEmpEmail);

//        binding.txtViewEmpEmail.setText(curEmpEmail);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        btnCancel = findViewById(R.id.btnCancel);
        btnConfirm = findViewById(R.id.btnConfirm);
        calendar = findViewById(R.id.calendarView);
        getSchduledDates(curEmpId);
        selectedDate = findViewById(R.id.txtViewSelectedDate);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                try {
                    String curDate = "", Year, Month = " ";
                    if (dayOfMonth < 9) {
                        curDate = "0" + (dayOfMonth);
                    } else {
                        curDate = String.valueOf(dayOfMonth);
                    }
                    if ((month + 1) < 9) {
                        Month = "0" + (month + 1);
                    } else {
                        Month = String.valueOf(month + 1);
                    }
                    Year = String.valueOf(year);
                    date = Month + "/" + curDate + "/" + Year;

                        scheduledDates.add(date);

                    selectedDate.setText(date);
                    Log.e("date", date);
                } catch (Exception e) {
                    Log.d(TAG, "onSelectedDayChange: Error occurred: " + e.getMessage());
                }
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddEmpSchedule.this, AdminDashboard.class));
                finish();
            }
        });


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference df = fStore.collection("Users").document(curEmpId);

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("schedule", scheduledDates);
                df.update(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddEmpSchedule.this, "Added the Schedule Successfully.",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddEmpSchedule.this, AdminDashboard.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddEmpSchedule.this, "Error Occured: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    public void getSchduledDates(String uId) {
        DocumentReference df = fStore.collection("Users").document(uId);

        df.get().addOnCompleteListener(
                (@NonNull Task<DocumentSnapshot> task) -> {
                    Log.d("abcd", "onComplete: Reading From the database");
                    DocumentSnapshot document = task.getResult();

                    if(document.get("schedule")!=null) {
                        scheduledDates = (List<String>) document.get("schedule");
                    }else {
                        Log.d(TAG, "getSchduledDates: The field is null");
                    }
//                 
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Error Occurred: " + e.getMessage());
            }
        });
    }

}