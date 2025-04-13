package itstep.learning.androidpv211;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class CalcActivity extends AppCompatActivity {

    private TextView tvExpression;
    private TextView tvResult;
    private String zero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvExpression=findViewById(R.id.calc_tv_expression);
        tvResult=findViewById(R.id.calc_tv_result);
        zero=getString(R.string.calc_btn_0);

        Button btnC=findViewById(R.id.calc_btn_c);
        btnC.setOnClickListener(this::onClearClick);
            findViewById(R.id.calc_btn_0).setOnClickListener(this::OnDigitClick);
        findViewById(R.id.calc_btn_1).setOnClickListener(this::OnDigitClick);
        findViewById(R.id.calc_btn_2).setOnClickListener(this::OnDigitClick);
        findViewById(R.id.calc_btn_3).setOnClickListener(this::OnDigitClick);
        findViewById(R.id.calc_btn_4).setOnClickListener(this::OnDigitClick);
        findViewById(R.id.calc_btn_5).setOnClickListener(this::OnDigitClick);
        findViewById(R.id.calc_btn_6).setOnClickListener(this::OnDigitClick);
        findViewById(R.id.calc_btn_7).setOnClickListener(this::OnDigitClick);
        findViewById(R.id.calc_btn_8).setOnClickListener(this::OnDigitClick);
        findViewById(R.id.calc_btn_9).setOnClickListener(this::OnDigitClick);

//        findViewById(R.id.calc_btn_plus).setOnClickListener(this::OnOperClick);
//        findViewById(R.id.calc_btn_minus).setOnClickListener(this::OnOperClick);
//        findViewById(R.id.calc_btn_mult).setOnClickListener(this::OnOperClick);
//        findViewById(R.id.calc_btn_div).setOnClickListener(this::OnOperClick);
        findViewById(R.id.calc_btn_equal).setOnClickListener(this::OnEqualClick);

            onClearClick(btnC);
    }

    private void OnEqualClick(View view) {
        String expression=tvResult.getText().toString();

        try{
            double res= mainRes(expression);
            tvExpression.setText(expression);
            tvResult.setText(String.valueOf(res));
        } catch(Exception e){
            tvResult.setText("Error");
        }
    }

    private double mainRes(String expression){
        expression=expression.replaceAll("\\s+","");//delete spaces???

        if(expression.contains("+")){
            String[] parts=expression.split("\\+");
            return Double.parseDouble(parts[0])+Double.parseDouble(parts[1]);
        }
        if(expression.contains("+")){
            String[] parts=expression.split("-");
            return Double.parseDouble(parts[0])-Double.parseDouble(parts[1]);
        }

        if(expression.contains("+")){
            String[] parts=expression.split("\\*");
            return Double.parseDouble(parts[0])*Double.parseDouble(parts[1]);
        }

        if(expression.contains("+")){
            String[] parts=expression.split("/");
            return Double.parseDouble(parts[0])/Double.parseDouble(parts[1]);
        }
return Double.parseDouble(expression);

    }
    private void OnDigitClick (View view){
String result=tvResult.getText().toString();
if(result.equals(zero)){
    result="";
}
result +=((Button)view).getText();
tvResult.setText(result);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("result", tvResult.getText());
    }//вызывается когда заменяется конфигурация - поворот смена языка,
    //bundle сховище для сохранения данных

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvResult.setText(savedInstanceState.getCharSequence("result"));
    }//вызывается когда конфигурация новая уже воплотилась - передает savedInstanceState=outState кот.сохр.при входе.

    private void onClearClick(View view){
        tvExpression.setText("");
        tvResult.setText(zero);

    }
//    private void OnOperClick(View view){
//        String expression=tvResult.getText().toString();
//        String operator=((Button)view).getText().toString();
//
//        if (!expression.isEmpty()){
//            tvResult.setText(expression+operator);
//
//        }

    }



