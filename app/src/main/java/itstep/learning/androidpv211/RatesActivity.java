package itstep.learning.androidpv211;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import itstep.learning.androidpv211.nbu.NbuRateAdapter;
import itstep.learning.androidpv211.orm.NbuRate;

public class RatesActivity extends AppCompatActivity {
    private static final String nbuRatesUrl="https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";

private ExecutorService pool;
    private final List<NbuRate> nbuRates=new ArrayList<>();
private NbuRateAdapter nbuRateAdapter;
private RecyclerView rvContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rates);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeBars = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, Math.max(systemBars.bottom, imeBars.bottom)
            );
            return insets;
        });
       // tvContainer=findViewById(R.id.rates_tv_container);
        String url = nbuRatesUrl;
        //  https://bank.gov.ua/ua/open-data/api-dev
        pool= Executors.newFixedThreadPool(3);

        pool.submit(() -> loadRates(url));
        //new Thread(this::loadRates).start();
        CompletableFuture
                .supplyAsync(()->loadRates(url), pool)
                .thenAccept(this::parseNbuResponse)
                .thenRun(this::showNbuRates);
        rvContainer=findViewById(R.id.rates_rv_container);
        rvContainer.post(()->{
            int w=getWindow().getDecorView().getWidth();//post- очередь действий
           //Log.d("post", "" + getWindow().getDecorView().getWidth());//post- очередь действий
        });
        //внутр.организация контента
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(this, 2);
        rvContainer.setLayoutManager(layoutManager);
        nbuRateAdapter = new NbuRateAdapter(nbuRates);
        rvContainer.setAdapter(nbuRateAdapter);
        //ДАТА
        Button dateButton = findViewById(R.id.btn_select_date);
        dateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(RatesActivity.this, (view, year, month, dayOfMonth) -> {
                // месяц нужно +1 (считается с 0)
                String formattedDate = String.format(Locale.US, "%04d%02d%02d", year, month + 1, dayOfMonth);
                String datedUrl = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?date=" + formattedDate + "&json";
                loadRatesForDate(datedUrl);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        SearchView svFilter=findViewById(R.id.rates_sv_filter);
        svFilter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return onFilterChange(s);
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return onFilterChange(s);
            }
        });
    }

    private void loadRatesForDate(String url) {
        CompletableFuture
                .supplyAsync(() -> loadRates(url), pool)
                .thenAccept(data -> {
                    nbuRates.clear(); // очищаем старые данные
                    parseNbuResponse(data);//парсим новый данные курсов
                })
                .thenRun(this::showNbuRates);//RecycleView
    }

    private boolean onFilterChange(String s){

        Log.d("onFilterChange", s);
        nbuRateAdapter.setNbuRates(
                nbuRates.stream()
                .filter(r->r.getCc().toUpperCase().contains(s.toUpperCase())||
                        r.getTxt().toUpperCase().contains(s.toUpperCase()))
                .collect(Collectors.toList()));
        return true;//обработано
    }

    private void showNbuRates(){
//        StringBuilder sb=new StringBuilder();
//        for(NbuRate nbuRate:nbuRates){
//            sb.append(nbuRate.getTxt());
//            sb.append('\n');
//        }
        runOnUiThread(()->{
            //tvContainer.setText(sb.toString());
        nbuRateAdapter.notifyItemChanged(0, nbuRates.size());
    });
    }
    private void parseNbuResponse(String body) {
        try {
            JSONArray arr = new JSONArray(body);
           // nbuRates = new ArrayList<>(); new List написали вверху
            int len = arr.length();
            for (int i = 0; i < len; i++) {

                nbuRates.add(
                        NbuRate.fromJsonObject(arr.getJSONObject(i))
                );
            }
        } catch (JSONException ex) {
            Log.d("parseNbuResponse", "JSONException " + ex.getMessage());
        }
    }
    private String loadRates(String urlDate){
        try {
            URL url = new URL(urlDate);
            InputStream urlStream= url.openStream();//GET request
            ByteArrayOutputStream byteBuilder=new ByteArrayOutputStream();//поток гет-запрос
            byte[]buffer=new byte[8192];//содержимое страницы в буфер
            int len;
            while ((len=urlStream.read(buffer))>0){
                byteBuilder.write(buffer,0,len);
            }
            String charsetName= StandardCharsets.UTF_8.name();
            String data=byteBuilder.toString(charsetName);
            urlStream.close();
//            runOnUiThread(()->tvContainer.setText(data));
            return data;
        }
        catch(MalformedURLException ex){
            Log.d("loadRates", "MalformedURLException "+ex.getMessage());

        }
        catch(IOException ex){
            Log.d("loadRates", "IOException "+ex.getMessage());
        }
        return null;
    }

    @Override
    protected void onDestroy(){
        pool.shutdownNow();
        super.onDestroy();//close thread
    }
}
//errors in CAT-LOG
//android.os.NetworkOnMainThreadException - всі запити до мережі мають бути в окремих потоках.

//java.lang.SecurityException: Permission denied (missing INTERNET permission?)
//андроид требует допуски. Интернет например. Допуски декларируются в манифесте.

//android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
//для работы с эелементами UI надо вернуть управление до потока UI runOnUiThread();
