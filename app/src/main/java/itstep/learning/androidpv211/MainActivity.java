package itstep.learning.androidpv211;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);//эта команда загружает разметку на экран, все действия с элеметами надо делать после этой команды

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());//отступы на экране тел
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById (R.id.main_btn_calc).setOnClickListener(this::onCalcButtonClick);
        findViewById (R.id.main_btn_rates).setOnClickListener(this::onRatesButtonClick);

        }
    private void onCalcButtonClick(View view){
        startActivity( new Intent(this, CalcActivity.class));
    }
    private void onRatesButtonClick(View view){
        startActivity( new Intent(this, RatesActivity.class));
    }
}