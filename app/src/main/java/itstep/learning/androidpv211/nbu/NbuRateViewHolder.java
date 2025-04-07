package itstep.learning.androidpv211.nbu;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import itstep.learning.androidpv211.R;
import itstep.learning.androidpv211.orm.NbuRate;

public class NbuRateViewHolder extends ViewHolder {
    private final TextView tvTxt;
    private final TextView tvCc;
private final TextView tvRate;
public final TextView tvRateF;
    private NbuRate nbuRate;
    public NbuRateViewHolder(@NonNull View itemView) {
        super(itemView);
        tvRateF=itemView.findViewById(R.id.nbu_rate_formatted);
        tvTxt=itemView.findViewById(R.id.nbu_rate_txt);
        tvCc=itemView.findViewById(R.id.nbu_rate_cc);
        tvRate=itemView.findViewById(R.id.nbu_rate_rate);
    }
    private void showData(){
        tvRateF.setText(nbuRate.formattedRate());
        tvTxt.setText(nbuRate.getTxt());
        tvCc.setText(nbuRate.getCc());
        tvRate.setText(String.valueOf(nbuRate.getRate()));

    }

    public NbuRate getNbuRate() {
        return nbuRate;
    }

    public void setNbuRate(NbuRate nbuRate) {
        this.nbuRate = nbuRate;

        tvRateF.setText(nbuRate.formattedRate());
        tvTxt.setText(nbuRate.getTxt());
        tvCc.setText(nbuRate.getCc());
        tvRate.setText(String.valueOf(nbuRate.getRate()));


        showData();
    }
}
//посредник между разметкой и данными