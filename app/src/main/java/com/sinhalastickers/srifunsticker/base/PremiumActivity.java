package com.sinhalastickers.srifunsticker.base;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.sinhalastickers.srifunsticker.R;

public class PremiumActivity extends AppCompatActivity  implements BillingProcessor.IBillingHandler{

    private BillingProcessor bp;
    private Context context;
    private Button purchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);
        context =this;
        purchase = findViewById(R.id.purchase_btn);
        bp = new BillingProcessor(this, null, this);
        if(!bp.isInitialized()){
            bp.initialize();
        }


        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAvailable = BillingProcessor.isIabServiceAvailable(PremiumActivity.this);
                if(!isAvailable) {
                    Toast.makeText(PremiumActivity.this,"Make sure google play service is active.",Toast.LENGTH_LONG).show();
                }else{
                    bp.purchase(PremiumActivity.this, "android.test.purchased");
                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Toast.makeText(this,"You have already purchased.   ",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this,"Something is went wrong.." ,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

}
