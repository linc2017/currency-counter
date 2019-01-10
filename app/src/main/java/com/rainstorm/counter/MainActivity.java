package com.rainstorm.counter;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rainstorm.counter.utils.MathUtil;
import com.rainstorm.counter.widget.CounterView;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * @description main launcher activity
 * @author liys
 */
public class MainActivity extends AppCompatActivity {
    private CounterView counterView;

    DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
    private double amount = 6306.45;
    private int countForRandom = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        //init currency count view
        counterView = findViewById(R.id.counter_view);
        //start to count currency
        startCountingCurrency();
    }

    /**
     * start to count currency
     */
    private void startCountingCurrency() {
        counterView.setText("$" + decimalFormat.format(amount));
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //compute amount increase
                amount = MathUtil.add(amount, 0.09);
                countForRandom++;
                if (countForRandom >= 10) {
                    countForRandom = 0;
                    amount = MathUtil.add(amount, new Random().nextInt(10000 + 1 + 10000) -10000);
                }
                //set amount text
                if (amount > 0) {
                    counterView.setText("$" + decimalFormat.format(amount));
                } else {
                    counterView.setText("-$" + decimalFormat.format(Math.abs(amount)));
                }
                
                handler.postDelayed(this, 1000);
            }
        },1000);
    }
}
