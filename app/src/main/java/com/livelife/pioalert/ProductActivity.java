package com.livelife.pioalert;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductActivity extends AppCompatActivity {

    TextView productTitleTextView,prodName,price,priceOff,availableUnits,saveAmount,brandName,prodDesc,brandLocation;
    ImageButton backButton,prodImageButton;
    Button infoButton;
    Product p;
    LinearLayout addToCartButton;
    ProgressBar addProgressBar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        int prodId = getIntent().getIntExtra("productId",0);

        if (prodId == 0) finish();

        p = WebApi.getInstance().getProductById(prodId);

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        productTitleTextView = (TextView) findViewById(R.id.productTitleTextView);
        productTitleTextView.setText(p.brandName);

        prodName = (TextView) findViewById(R.id.prodName);
        prodName.setText(p.name);

        prodImageButton = (ImageButton) findViewById(R.id.prodImageButton);
        Picasso.with(this).load("http://pioalert.com"+p.image).into(prodImageButton);
        prodImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itemImage = new Intent(ProductActivity.this,ImageDetailActivity.class);
                itemImage.putExtra("imgPath","http://pioalert.com"+p.image);
                startActivity(itemImage);
            }
        });


        price = (TextView) findViewById(R.id.price);
        price.setText("€ "+p.price);

        priceOff = (TextView) findViewById(R.id.priceOff);
        if (!p.initialPrice.equals("0")) {
            priceOff.setText("€ "+p.initialPrice);
            priceOff.setPaintFlags(priceOff.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            priceOff.setVisibility(View.INVISIBLE);
        }

        availableUnits = (TextView) findViewById(R.id.availableUnits);
        availableUnits.setText(p.available+" unità");

        saveAmount = (TextView) findViewById(R.id.saveAmount);
        if (p.saveAmount != null) {
            saveAmount.setText(p.saveAmount);
        } else {
            saveAmount.setVisibility(View.INVISIBLE);
        }


        brandName = (TextView) findViewById(R.id.brandName);
        brandName.setText(p.brandName);

        brandLocation = (TextView) findViewById(R.id.brandLocation);
        brandLocation.setText(p.brandLocation);

        prodDesc = (TextView) findViewById(R.id.prodDesc);
        prodDesc.setText(p.descLong);

        infoButton = (Button) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);

                Uri uri = Uri.parse("mailto:feedback@pioalert.com");
                intent.setData(uri);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Richiesta di informazioni su:  "+p.name);
                startActivity(intent);
            }
        });


        addToCartButton = (LinearLayout) findViewById(R.id.addToCartButton);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addProgressBar.setVisibility(View.VISIBLE);


                boolean added = WebApi.getInstance().basketMove(p.pid,1);

                if (added) {
                    addProgressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(ProductActivity.this,CartActivity.class);
                    intent.putExtra("idCom",p.idCom);
                    startActivity(intent);
                } else {
                    addToCartButton.setEnabled(true);
                    addProgressBar.setVisibility(View.GONE);
                }



            }
        });

        addProgressBar = (ProgressBar) findViewById(R.id.addProgressBar);
        addProgressBar.setVisibility(View.GONE);
    }
}
