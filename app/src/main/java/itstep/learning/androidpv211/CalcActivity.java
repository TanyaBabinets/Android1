package itstep.learning.androidpv211;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
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

 ;   @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                EdgeToEdge.enable(this);
                setContentView(R.layout.activity_calc);
                ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
                zero=getString(R.string.calc_btn_0);
                tvExpression=findViewById(R.id.calc_tv_expression);
                tvResult=findViewById(R.id.calc_tv_result);
                if (savedInstanceState != null) {
                    tvResult.setText(savedInstanceState.getCharSequence("result"));
                    tvExpression.setText(savedInstanceState.getCharSequence("expression"));
                } else{
                    tvExpression.setText("");
                    tvResult.setText("0");
        }
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
        findViewById(R.id.calc_btn_backspase).setOnClickListener(this::OnDigitClick);


//        findViewById(R.id.calc_btn_plus).setOnClickListener(this::OnOperClick);
//        findViewById(R.id.calc_btn_minus).setOnClickListener(this::OnOperClick);
//        findViewById(R.id.calc_btn_mult).setOnClickListener(this::OnOperClick);
//        findViewById(R.id.calc_btn_div).setOnClickListener(this::OnOperClick);
        findViewById(R.id.calc_btn_equal).setOnClickListener(this::OnEqualClick);
        findViewById(R.id.calc_btn_c).setOnClickListener(this::onClearClick);
        findViewById(R.id.calc_btn_ce).setOnClickListener(this::onClearClick);
        findViewById(R.id.calc_btn_backspase).setOnClickListener(this::OnBackspaceClick);
        findViewById(R.id.calc_btn_percent).setOnClickListener(this::OnPercentClick);
        findViewById(R.id.calc_btn_coma).setOnClickListener(this::onComaClick);

        //  findViewById(R.id.calc_btn_sqrt).setOnClickListener(this::OnRootClick);
            //onClearClick(btnC);


    }

    private void onComaClick(View view) {
        String number = tvResult.getText().toString();
        if (!number.contains(",")) {
            tvResult.setText(number + ".");
        }
    }

    private void OnEqualClick(View view) {
          String expression=tvResult.getText().toString();
      //  String expression=tvExpression.getText().toString();
        expression = expression.replace(",", ".");//замена на точку
        try{

            double res= mainRes(expression);
            tvExpression.setText(expression);
            tvResult.setText(String.valueOf(res));
        } catch(Exception e){
            tvResult.setText("Error");
            tvExpression.setText(expression);
        }
    }
    public void OnRootClick(View view) {
        try {
            String input = tvResult.getText().toString().trim();
            if (input.isEmpty()) {
                return;
            }

            double number = Double.parseDouble(input);

            if (number < 0) {
                tvResult.setText("Error"); //проверка на отриц число
                return;
            }

            double result = Math.sqrt(number);


            tvExpression.setText("√" + input);
            tvResult.setText(String.format("%.2f", result));
          //  tvResult.setText(String.valueOf(result));
        } catch (NumberFormatException e) {
            tvResult.setText("Error");
        }
    }
    private void OnPercentClick(View view) {
        String expression = tvResult.getText().toString().trim();//пробелы по краям
        expression = expression.replaceAll("\\s+", ""); // удаляем пробелы внутри

        try {
            if (expression.contains("+")) {
                String[] parts = expression.split("\\+"); //25+5%
                double number = Double.parseDouble(parts[0]);
                double percent = Double.parseDouble(parts[1]);
                double result = number + (number * percent / 100);
                tvResult.setText(String.valueOf(result));
            } else if (expression.contains("−") || expression.contains("-")) {
                String[] parts = expression.split("(?<=\\d)[−-](?=\\d)");//25-5%
                double number = Double.parseDouble(parts[0]);
                double percent = Double.parseDouble(parts[1]);
                double result = number - (number * percent / 100);
                tvResult.setText(String.valueOf(result));
            } else {

                double value = Double.parseDouble(expression);
                tvResult.setText(String.valueOf(value / 100));//просто процент от числа
            }
        } catch (Exception e) {
            tvResult.setText("Error");
        }
    }

    private double mainRes(String expression){
        expression=expression.replaceAll("\\s+","");//delete spaces???

        if(expression.contains("+")){
            String[] parts=expression.split("\\+");
            return Double.parseDouble(parts[0])+Double.parseDouble(parts[1]);
        }
        if(expression.contains("−")){
            String[] parts=expression.split("−");
            return Double.parseDouble(parts[0])-Double.parseDouble(parts[1]);
        }

        if(expression.contains("×")){
            String[] parts=expression.split("×");
            return Double.parseDouble(parts[0])*Double.parseDouble(parts[1]);
        }

        if(expression.contains("÷")){
            String[] parts=expression.split("÷");
            double div = Double.parseDouble(parts[1]);
            if (div == 0) {
                throw new ArithmeticException("Do not divide by zero");
            }
            return Double.parseDouble(parts[0])/Double.parseDouble(parts[1]);
        }
return Double.parseDouble(expression);

    }
    private void OnDigitClick (View view){
String result=tvResult.getText().toString();
String digit = ((Button) view).getText().toString();
if(result.equals(zero)){
    result="";
}
    //    result += " " + operator + " ";
        result += digit;
        //result +=((Button)view).getText();//цифра с кнопки
tvResult.setText(result);
    }
    public void OnOperClick (View view) {
        String result = tvResult.getText().toString();
        String oper = ((Button) view).getText().toString();
        if (result.isEmpty()) {
            return; // Не добавляем оператор, если выражение пустое
        }
        result += " " + oper + " ";
        tvResult.setText(result);
    }
    public void OnBackspaceClick(View view) {
        String result = tvResult.getText().toString();
        if (!result.isEmpty()) {
            result = result.substring(0, result.length() - 1);
            tvResult.setText(result);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("result", tvResult.getText());
        outState.putCharSequence("expression", tvExpression.getText());
    }//вызывается когда заменяется конфигурация - поворот смена языка,
    //bundle сховище для сохранения данных

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvResult.setText(savedInstanceState.getCharSequence("result"));
        tvExpression.setText(savedInstanceState.getCharSequence("expression"));
    }//вызывается когда конфигурация новая уже воплотилась - передает savedInstanceState=outState кот.сохр.при входе.


    private void onClearClick(View view){
        tvExpression.setText("");
        tvResult.setText(zero);

    }


    }



