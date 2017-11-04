package com.livelife.pioalert;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ProductActivity extends AppCompatActivity {

    TextView productTitleTextView, prodName, price, priceOff, availableUnits, saveAmount, brandName, prodDesc, brandLocation,
            numberReviews,review_info_tv;
    ImageButton backButton, prodImageButton;
    Button infoButton,viewShopButton;
    Button write_a_review_btn;
    Product p;
    LinearLayout addToCartButton;
    LinearLayout review_ll;
    LinearLayout check_in_ll;
    RelativeLayout check_in_rl;
    LinearLayout giorna_ll;
    ProgressBar addProgressBar;
    EditText mCheckIn_et;
    EditText mCheckOut_et;
    EditText giorna_et;
    EditText ora_et;
    private List<String> mWorkingDays;
    private int mFromtimeHours = 0;
    private int mFromtimeMins = 0;
    private int mTotimeHours = 0;
    private int mTotimeMins = 0;
    private int mHoursOra = 0;
    private int mMinsOra = 0;


    RatingBar ratingBar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    int prodId = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        prodId = getIntent().getIntExtra("productId", 0);

        if (prodId == 0) finish();
        if (!Utility.isNetworkConnected(this)){
            Toast.makeText(this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
            return ;
        }
        p = WebApi.getInstance().getProductById(prodId);

        viewShopButton = (Button) findViewById(R.id.viewShopButton);
        viewShopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent companyIntent = new Intent(ProductActivity.this,CompanyActivity.class);
                companyIntent.putExtra("comId",p.idCom);
                startActivity(companyIntent);
            }
        });

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setRating((float) p.avrReviews);
        numberReviews = (TextView) findViewById(R.id.numberReviews);
        ratingBar.setRating((float) p.avrReviews);
        numberReviews.setText(""+p.numReviews);
        review_info_tv = (TextView) findViewById(R.id.review_info_tv);

        if (p.numReviews<=0){
            review_info_tv.setText("NO REVIEWS");
        }

        backButton = (ImageButton) findViewById(R.id.backButton);
        mCheckIn_et = (EditText) findViewById(R.id.check_in_et);
        mCheckOut_et = (EditText) findViewById(R.id.check_out_et);
        giorna_et = (EditText) findViewById(R.id.giorna_et);
        ora_et = (EditText) findViewById(R.id.ora_et);
        mCheckIn_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerForCheckIn();
            }
        });

        mCheckOut_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerForCheckOut();
            }
        });
        giorna_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerForGiorna();
            }
        });

        ora_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerForOra();
            }
        });
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
        Picasso.with(this).load("http://pioalert.com" + p.image).into(prodImageButton);
        prodImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itemImage = new Intent(ProductActivity.this, ImageDetailActivity.class);
                itemImage.putExtra("imgPath", "http://pioalert.com" + p.image);
                startActivity(itemImage);
            }
        });


        price = (TextView) findViewById(R.id.price);
        price.setText("€ " + p.price);

        priceOff = (TextView) findViewById(R.id.priceOff);
        if (!p.initialPrice.equals("0")) {
            priceOff.setText("€ " + p.initialPrice);
            priceOff.setPaintFlags(priceOff.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            priceOff.setVisibility(View.INVISIBLE);
        }

        availableUnits = (TextView) findViewById(R.id.availableUnits);
        availableUnits.setText(p.available + " unità");

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
        write_a_review_btn = (Button) findViewById(R.id.write_a_review_btn);
        write_a_review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ProductActivity.this,RatingScreen.class);
                mIntent.putExtra("PRODUCT",p);
                startActivity(mIntent);
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);

                Uri uri = Uri.parse("mailto:feedback@pioalert.com");
                intent.setData(uri);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Richiesta di informazioni su:  " + p.name);
                startActivity(intent);
            }
        });


        review_ll = (LinearLayout) findViewById(R.id.review_ll);
        review_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ProductActivity.this,AllReviewsListActivity.class);
                mIntent.putExtra("PRODUCT",p);

                startActivity(mIntent);
            }
        });
        addToCartButton = (LinearLayout) findViewById(R.id.addToCartButton);
        check_in_ll = (LinearLayout) findViewById(R.id.check_in_ll);
        check_in_rl = (RelativeLayout) findViewById(R.id.check_in_rl);
        giorna_ll = (LinearLayout) findViewById(R.id.giorna_ll);
        if (p.calendarType.equalsIgnoreCase("1")) {
            check_in_ll.setVisibility(View.VISIBLE);
            check_in_rl.setVisibility(View.VISIBLE);
            giorna_ll.setVisibility(View.GONE);
        } else if (p.calendarType.equalsIgnoreCase("2")) {
            check_in_ll.setVisibility(View.GONE);
            giorna_ll.setVisibility(View.VISIBLE);
            check_in_rl.setVisibility(View.VISIBLE);
            if (!p.workingDays.isEmpty()) {
                mWorkingDays = new ArrayList<>();
                mWorkingDays = Arrays.asList(p.workingDays.split(","));

            }
            if (!p.fromtime.isEmpty()) {
                if (p.fromtime.contains(":")) {
                    String[] separated = p.fromtime.split(":");
                    mFromtimeHours = Integer.parseInt(separated[0]);
                    mFromtimeMins = Integer.parseInt(separated[1]);

                }
            }
            if (!p.totime.isEmpty()) {
                if (p.totime.contains(":")) {
                    String[] separated = p.totime.split(":");
                    mTotimeHours = Integer.parseInt(separated[0]);
                    mTotimeMins = Integer.parseInt(separated[1]);

                }
            }
        } else {
            check_in_ll.setVisibility(View.GONE);
            giorna_ll.setVisibility(View.GONE);
            check_in_rl.setVisibility(View.GONE);
        }

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addProgressBar.setVisibility(View.VISIBLE);


                int mCalType = 0;
                boolean added = false;
                if (!p.calendarType.isEmpty()) {
                    mCalType = Integer.parseInt(p.calendarType);
                    if (mCalType == 2) {
                        // TODO: 11/10/17
                        final Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, yearGiorna);
                        calendar.set(Calendar.MONTH, monthOfYearGiorna);
                        calendar.set(Calendar.DAY_OF_WEEK, dayOfMonthGiorna);
                        calendar.set(Calendar.HOUR_OF_DAY, mHoursOra);
                        calendar.set(Calendar.MINUTE, mMinsOra);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        mCalTime = calendar.getTimeInMillis();

                        long mQuantity = TimeUnit.DAYS.convert(mCheckOutTime - mCalTime, TimeUnit.MILLISECONDS);
                        System.out.println ("Days: " +mQuantity );
                        if (!Utility.isNetworkConnected(ProductActivity.this)){
                            Toast.makeText(ProductActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                            return ;
                        }
                        added = WebApi.getInstance().basketMove(p.pid,mQuantity  , mCalType, mCalTime);
                    }
                    else{
                        if (!Utility.isNetworkConnected(ProductActivity.this)){
                            Toast.makeText(ProductActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                            return ;
                        }
                        added = WebApi.getInstance().basketMove(p.pid, 1, mCalType, mCalTime);

                    } /*if (mCalType == 0) {

                        added = WebApi.getInstance().basketMove(p.pid, 1, mCalType, mCalTime);
                    }*/
                }







                if (added) {
                    addProgressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(ProductActivity.this, CartActivity.class);
                    intent.putExtra("idCom", p.idCom);
                    intent.putExtra("calendarType", p.calendarType);
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


    boolean isReloaded = false;
    @Override
    protected void onResume() {
        super.onResume();


        if (isReloaded) {
            if (!Utility.isNetworkConnected(this)){
                Toast.makeText(this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                return ;
            }
            p = WebApi.getInstance().getProductById(prodId);

            ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            ratingBar.setRating((float) p.avrReviews);
            numberReviews = (TextView) findViewById(R.id.numberReviews);

            numberReviews.setText("" + p.numReviews);
            review_info_tv = (TextView) findViewById(R.id.review_info_tv);

            if (p.numReviews<=0){
                review_info_tv.setText("NO REVIEWS");
            }
        }

        isReloaded = true;
    }

    private void showTimePickerForOra() {
        final Calendar now = Calendar.getInstance();

        TimePickerDialog mTimePickerDialog =
                new TimePickerDialog().newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePickerDialog view,
                                                  int hourOfDay, int minute, int second) {
                                mHoursOra = hourOfDay;
                                mMinsOra = minute;

                                final Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                ora_et.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));


                            }
                        }, now.get(Calendar.HOUR_OF_DAY)
                        , now.get(Calendar.MINUTE)
                        , now.get(Calendar.SECOND), true);

        mTimePickerDialog.setTitle(getResources().getString(R.string.chooseOraText));

        mTimePickerDialog.setMinTime(mFromtimeHours, mFromtimeMins, 0);
        mTimePickerDialog.setMaxTime(mTotimeHours, mTotimeMins, 0);
        mTimePickerDialog.setTimeInterval(1,30);
        mTimePickerDialog.show(getFragmentManager(), "Timepickerdialog");


    }

    private void showDatePickerForGiorna() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        yearGiorna = year;
                        monthOfYearGiorna = monthOfYear;
                        dayOfMonthGiorna = dayOfMonth;

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        mGiornaTime = calendar.getTimeInMillis();

                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        giorna_et.setText(date);

                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMinDate(now);

        Calendar monday; //0
        Calendar tuesday;//1
        Calendar wednesday;//2
        Calendar thursday;//3
        Calendar friday;//4
        Calendar saturday; //5
        Calendar sunday;//6

        List<Calendar> weekends = new ArrayList<>();
        int weeks = 1000;


        for (int i = 0; i < (weeks * 7); i = i + 7) {


            if (mWorkingDays.contains("0")) {
                monday = Calendar.getInstance();
                monday.add(Calendar.DAY_OF_YEAR, (Calendar.MONDAY - monday.get(Calendar.DAY_OF_WEEK) + i));
                weekends.add(monday);
            }
            if (mWorkingDays.contains("1")) {

                tuesday = Calendar.getInstance();
                tuesday.add(Calendar.DAY_OF_YEAR, (Calendar.TUESDAY - tuesday.get(Calendar.DAY_OF_WEEK) + i));
                weekends.add(tuesday);
            }
            if (mWorkingDays.contains("2")) {

                wednesday = Calendar.getInstance();
                wednesday.add(Calendar.DAY_OF_YEAR, (Calendar.WEDNESDAY - wednesday.get(Calendar.DAY_OF_WEEK) + i));
                weekends.add(wednesday);
            }
            if (mWorkingDays.contains("3")) {


                thursday = Calendar.getInstance();
                thursday.add(Calendar.DAY_OF_YEAR, (Calendar.THURSDAY - thursday.get(Calendar.DAY_OF_WEEK) + i));
                weekends.add(thursday);
            }
            if (mWorkingDays.contains("4")) {

                friday = Calendar.getInstance();
                friday.add(Calendar.DAY_OF_YEAR, (Calendar.FRIDAY - friday.get(Calendar.DAY_OF_WEEK) + i));
                weekends.add(friday);
            }
            if (mWorkingDays.contains("5")) {


                saturday = Calendar.getInstance();
                saturday.add(Calendar.DAY_OF_YEAR, (Calendar.SATURDAY - saturday.get(Calendar.DAY_OF_WEEK) + i));
                weekends.add(saturday);
            }
            if (mWorkingDays.contains("6")) {
                sunday = Calendar.getInstance();
                sunday.add(Calendar.DAY_OF_YEAR, (Calendar.SUNDAY - sunday.get(Calendar.DAY_OF_WEEK) + 7 + i));
                weekends.add(sunday);
            }


        }
        Calendar[] mSelectableDays = weekends.toArray(new Calendar[weekends.size()]);
        dpd.setSelectableDays(mSelectableDays);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private int yearCheckIn = 0;
    private int monthOfYearCheckIn = 0;
    private int dayOfMonthCheckIn = 0;

    private long mCalTime = 0;
    private long mCheckOutTime = 0;

    private int yearGiorna = 0;
    private int monthOfYearGiorna = 0;
    private int dayOfMonthGiorna = 0;

    private long mGiornaTime = 0;
    private long mTimeQuantityDiff = 0;

    private void showDatePickerForCheckOut() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        mCheckOutTime = calendar.getTimeInMillis();


                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        mCheckOut_et.setText(date);

                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        Calendar cal = Calendar.getInstance();
//Min date setting part
        cal.set(Calendar.MONTH, monthOfYearCheckIn);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonthCheckIn);
        cal.set(Calendar.YEAR, yearCheckIn);
        dpd.setMinDate(cal);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void showDatePickerForCheckIn() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        yearCheckIn = year;
                        monthOfYearCheckIn = monthOfYear;
                        dayOfMonthCheckIn = dayOfMonth;

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        mCalTime = calendar.getTimeInMillis();

                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        mCheckIn_et.setText(date);

                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMinDate(now);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }
}
