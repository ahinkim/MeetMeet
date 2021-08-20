package org.techtown.meetmeetapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import androidx.appcompat.app.AppCompatActivity;

import org.techtown.meetmeetapp.R;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button month_button;
    Button year_button;
    ImageButton write_button;

    DatePickerDialog.OnDateSetListener monthlistener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            if(monthOfYear==1){month_button.setText("JANUARY");}
            else if(monthOfYear==2){month_button.setText("FEBRUARY");}
            else if(monthOfYear==3){month_button.setText("MARCH");}
            else if(monthOfYear==4){month_button.setText("APRIL");}
            else if(monthOfYear==5){month_button.setText("MAY");}
            else if(monthOfYear==6){month_button.setText("JUNE");}
            else if(monthOfYear==7){month_button.setText("JULY");}
            else if(monthOfYear==8){month_button.setText("AUGUST");}
            else if(monthOfYear==9){month_button.setText("SEPTEMBER");}
            else if(monthOfYear==10){month_button.setText("OCTOBER");}
            else if(monthOfYear==11){month_button.setText("NOVEMBER");}
            else if(monthOfYear==12){month_button.setText("DECEMBER");}
        }
    };
    DatePickerDialog.OnDateSetListener yearlistener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_button.setText(""+year);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        month_button = findViewById(R.id.month_button);
        year_button=findViewById(R.id.year_button);
        write_button=findViewById(R.id.write_button);

        month_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MonthPickerDialog monthPickerDialog = new MonthPickerDialog();
                monthPickerDialog.setListener(monthlistener);
                monthPickerDialog.show(getSupportFragmentManager(), "YearMonthPickerTest");
            }
        });
        year_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                YearPickerDialog yearPickerDialog = new YearPickerDialog();
                yearPickerDialog.setListener(yearlistener);
                yearPickerDialog.show(getSupportFragmentManager(), "YearMonthPickerTest");
            }
        });
        write_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, DiaryWriteActivity.class );
                startActivity( intent );
            }
        });

    }
}