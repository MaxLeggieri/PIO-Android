package com.livelife.pioalert;

import android.*;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

import static android.view.animation.Animation.RELATIVE_TO_SELF;
import static com.livelife.pioalert.R.id.giorna_et;

/**
 * Created by Shoeb on 14/10/17.
 */

public class CreateAdActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CreateAdActivity.class.getSimpleName();
    private Button create_ad_btn;
    private List<CategoriesModal.Response.Datum> mCategoryList = new ArrayList<>();

    RecyclerView parent_cat_rv;
    TextView logout_tv;
    RelativeLayout prim_cat_rl;
    ImageView primarycategory_arrow_iv;
    CategoryAdapter mCategoryAdapter;
    EditText title_et;
    EditText description_et;
    EditText products_et;
    EditText link_et;
    EditText youtube_link_et;
    EditText hash_tags_et;
    EditText expiry_date_et;

    Spinner coupon_spinner;
    Spinner km_ray_spinner;

    Spinner location_spinner;
    Spinner alert_type_spinner;

    ImageView ad_img_iv;
    ProgressBar progress_pb;
    ImageButton backButton;
    private String fileToUploadPathImg = "";
    private String mCategoryId = "";
    private String mExpirationTimeStr;
    private long mExpirationTimeMS;
    private int dayOfMonth;
    public int year;
    public int monthOfYear;
    private List<CompanyLocationResponseModal.Response.Data.Product.Loc> mLocationsFromAPI = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_ad_activity);

        ad_img_iv = findViewById(R.id.ad_img_iv);
        backButton = findViewById(R.id.backButton);
        progress_pb = findViewById(R.id.progress_pb);
        create_ad_btn = findViewById(R.id.create_ad_btn);
        parent_cat_rv = findViewById(R.id.parent_cat_rv);
        prim_cat_rl = findViewById(R.id.prim_cat_rl);
        title_et = findViewById(R.id.title_et);
        description_et = findViewById(R.id.description_et);
        products_et = findViewById(R.id.products_et);
        link_et = findViewById(R.id.link_et);
        youtube_link_et = findViewById(R.id.youtube_link_et);
        hash_tags_et = findViewById(R.id.hash_tags_et);
        expiry_date_et = findViewById(R.id.expiry_date_et);


        coupon_spinner = findViewById(R.id.coupon_spinner);
        km_ray_spinner = findViewById(R.id.km_ray_spinner);


        location_spinner = findViewById(R.id.location_spinner);
        alert_type_spinner = findViewById(R.id.alert_type_spinner);


        logout_tv = findViewById(R.id.logout_tv);


        primarycategory_arrow_iv = findViewById(R.id.primarycategory_arrow_iv);
        expiry_date_et.setOnClickListener(this);
        create_ad_btn.setOnClickListener(this);
        prim_cat_rl.setOnClickListener(this);
        ad_img_iv.setOnClickListener(this);
        logout_tv.setOnClickListener(this);
        backButton.setOnClickListener(this);


        callCategoriesAPI();
        callCCompaniesLocationsAPI();
        initView();
    }

    private void callCCompaniesLocationsAPI() {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String uid = String.valueOf(PioUser.getInstance().uid);
        String device_token = "";
        int rec = 20;
        double lat = PioUser.getInstance().location.getLatitude();
        double lng = PioUser.getInstance().location.getLongitude();
        int mPageNumber = 1;
        int idcom = 59;
        Call<JsonElement> call = apiService.getCompaniesWithLocation(uid,
                device_token,
                rec,
                lat,
                lng,
                mPageNumber,
                idcom
        );
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                System.out.println(call);
                if (response.code() == 200) {
                    Gson mGson = new Gson();
                    CompanyLocationResponseModal modalMain = mGson.fromJson(response.body().toString(), CompanyLocationResponseModal.class);
                    ArrayList<String> mCompanyName = new ArrayList<String>();
                    if (modalMain.getResponse().getResponse()) {
                        List<CompanyLocationResponseModal.Response.Data.Product> mProducts
                                = modalMain.getResponse().getData().getProducts();
                        mLocationsFromAPI = mProducts.get(0).getLoc();
                        for (CompanyLocationResponseModal.Response.Data.Product.Loc bean :
                                mLocationsFromAPI) {
                            mCompanyName.add(bean.getName());
                        }
                        if (mCompanyName.size() > 0) {
                            /*Set Spinner for location*/
                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_selected_textview, mCompanyName);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            location_spinner.setAdapter(adapter);

                        }
                    }
                } else {
                    Toast.makeText(CreateAdActivity.this, getResources().getString(R.string.someerrorText), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                System.out.println(call);

                Toast.makeText(CreateAdActivity.this, getResources().getString(R.string.someerrorText), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initView() {
        mCategoryAdapter = new CategoryAdapter(this, mCategoryList);
        parent_cat_rv.setLayoutManager(new LinearLayoutManager(this));
        parent_cat_rv.setItemAnimator(new DefaultItemAnimator());
        parent_cat_rv.setAdapter(mCategoryAdapter);


        parent_cat_rv.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        CategoriesModal.Response.Datum modalMain = mCategoryList.get(position);
                        if (modalMain.isPrimarySelected()) {
                            modalMain.setPrimarySelected(false);
                        } else {
                            modalMain.setPrimarySelected(true);
                        }
                        mCategoryAdapter.updateItem(modalMain, position);

                        mCategoryId = modalMain.getId();
                        if (modalMain.getName().contains(" ")) {
                            String mHashTags = "";

                            String[] splited = modalMain.getName().split("\\s+");
                            if (splited.length > 0) {
                                for (String mName :
                                        splited) {
                                    if (mHashTags.equalsIgnoreCase("")) {
                                        mHashTags = "#" + mName + " ";
                                    } else {
                                        mHashTags = mHashTags + " " + "#" + mName;
                                    }
                                }
                            }
                            hash_tags_et.setText(mHashTags);

                        }else{
                            hash_tags_et.setText("#"+modalMain.getName());

                        }
                        updatePrimaryCatRV();


                    }
                })
        );
    }

    private void updatePrimaryCatRV() {
        if (parent_cat_rv.getVisibility() == View.VISIBLE) {
            parent_cat_rv.setVisibility(View.GONE);
            prim_cat_rl.setBackground(getResources().getDrawable(R.drawable.white_gray_border));
            animateCollapseRotateBy180(primarycategory_arrow_iv);
        } else {
            parent_cat_rv.setVisibility(View.VISIBLE);
            animateExpandRotateBy180(primarycategory_arrow_iv);
            prim_cat_rl.setBackground(getResources().getDrawable(R.drawable.white_gray_border_top_rounded));


        }
    }


    private void animateExpandRotateBy180(ImageView mView) {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        mView.setAnimation(rotate);
    }

    private void animateCollapseRotateBy180(ImageView mView) {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        mView.setAnimation(rotate);
    }

    private void callCategoriesAPI() {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<JsonElement> call = apiService.getAllCategories();
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                System.out.println(call);
                Gson mGson = new Gson();
                CategoriesModal bean = mGson.fromJson(response.body().toString(), CategoriesModal.class);
                if (bean.getResponse().getResponse()) {
                    mCategoryList = bean.getResponse().getData();
                    setCategoryData();

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                System.out.println(call);
            }
        });

    }

    private void setCategoryData() {
        if (mCategoryList == null) {
            return;
        }
        if (mCategoryList.size() == 0) {
            return;
        }
        mCategoryAdapter.addItems(mCategoryList);


    }

    private void callWS() {
        progress_pb.setVisibility(View.VISIBLE);
//        Request construction:
        Map<String, RequestBody> map = new HashMap<>();
        //map.put("idad", RequestBody.create(MediaType.parse("text/plain"), "0"));
        map.put("idcom", RequestBody.create(MediaType.parse("text/plain"), "59"));
        map.put("title", RequestBody.create(MediaType.parse("text/plain"), title_et.getText().toString().trim()));
        map.put("description", RequestBody.create(MediaType.parse("text/plain"), description_et.getText().toString().trim()));
        map.put("products", RequestBody.create(MediaType.parse("text/plain"), products_et.getText().toString().trim()));
        map.put("link", RequestBody.create(MediaType.parse("text/plain"), link_et.getText().toString().trim()));
        map.put("youtube", RequestBody.create(MediaType.parse("text/plain"), youtube_link_et.getText().toString().trim()));
        map.put("coupon", RequestBody.create(MediaType.parse("text/plain"), coupon_spinner.getSelectedItem().toString()));
        map.put("categories", RequestBody.create(MediaType.parse("text/plain"), mCategoryId));
        map.put("hashtags", RequestBody.create(MediaType.parse("text/plain"), hash_tags_et.getText().toString().trim()));
        map.put("raykm", RequestBody.create(MediaType.parse("text/plain"), km_ray_spinner.getSelectedItem().toString()));


        String mLocationid = "";
        for (int i = 0; i < mLocationsFromAPI.size(); i++) {
            CompanyLocationResponseModal.Response.Data.Product.Loc bean = mLocationsFromAPI.get(i);
            if (location_spinner.getSelectedItem().toString().equalsIgnoreCase(bean.getName())) {
                mLocationid = bean.getIdlocation();
                break;
            }
        }

        map.put("locations", RequestBody.create(MediaType.parse("text/plain"), mLocationid));
        map.put("beacons", RequestBody.create(MediaType.parse("text/plain"), "154"));
        map.put("alertkind", RequestBody.create(MediaType.parse("text/plain"), alert_type_spinner.getSelectedItem().toString()));
        map.put("start", RequestBody.create(MediaType.parse("text/plain"), ""));
        map.put("expiration", RequestBody.create(MediaType.parse("text/plain"), "" + mExpirationTimeMS));
        map.put("RelatedProducts", RequestBody.create(MediaType.parse("text/plain"), ""));
        map.put("method", RequestBody.create(MediaType.parse("text/plain"), "createAd"));
        map.put("userid", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(PioUser.getInstance().uid)));
        File mImageFile = new File(fileToUploadPathImg);
        if (mImageFile.exists()) {
            map.put("image", RequestBody.create(MediaType.parse("image/*"), mImageFile));

        }

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<JsonElement> call = apiService.postCreateAd(map);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                progress_pb.setVisibility(View.GONE);

                System.out.println(call);
                Gson mGson = new Gson();
                CreateAdResponse bean = mGson.fromJson(response.body().toString(), CreateAdResponse.class);
                if (bean.getResponse().getResponse()) {
                    Toast.makeText(CreateAdActivity.this, "Ad created", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateAdActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CreateAdActivity.this, bean.getResponse().getReason(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "onFailure : " + call.toString());
                t.printStackTrace();

                progress_pb.setVisibility(View.GONE);

                Toast.makeText(CreateAdActivity.this, getResources().getString(R.string.someerrorText), Toast.LENGTH_SHORT).show();


            }
        });
    }


    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkPermission(final Activity context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePhotoFromCamGalleryDialog();


                } else {
                    //code for deny
                }
                break;
        }
    }

    private void showTimePicker() {
        final Calendar now = Calendar.getInstance();

        TimePickerDialog mTimePickerDialog =
                new TimePickerDialog().newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePickerDialog view,
                                                  int hourOfDay, int minute, int second) {

                                final Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, second);
                                calendar.set(Calendar.MILLISECOND, 0);

                                mExpirationTimeMS = calendar.getTimeInMillis();

                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                                expiry_date_et.setText(date.concat(" ").concat(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute)));


                            }
                        }, now.get(Calendar.HOUR_OF_DAY)
                        , now.get(Calendar.MINUTE)
                        , now.get(Calendar.SECOND), false);

        mTimePickerDialog.setTitle(getResources().getString(R.string.chooseOraText));

//        mTimePickerDialog.setMinTime(now.get(Calendar.HOUR), now.get(Calendar.MINUTE), now.get(Calendar.SECOND));

        mTimePickerDialog.show(getFragmentManager(), "Timepickerdialog");


    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        CreateAdActivity.this.year = year;
                        CreateAdActivity.this.monthOfYear = monthOfYear;
                        CreateAdActivity.this.dayOfMonth = dayOfMonth;

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        showTimePicker();

                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMinDate(now);
        dpd.show(getFragmentManager(), "Datepickerdialog");


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_ad_btn:
                if (isValidData()) {
                    callWS();
                }
            case R.id.prim_cat_rl:
                updatePrimaryCatRV();

                break;
            case R.id.backButton:
                finish();

                break;
            case R.id.expiry_date_et:
                showDatePicker();
                break;
            case R.id.logout_tv:
                MyPreference.resetAllData();
                Intent intent = new Intent(CreateAdActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                break;
            case R.id.ad_img_iv:
                boolean result = checkPermission(CreateAdActivity.this);

                if (result)
                    choosePhotoFromCamGalleryDialog();
                break;
            default:
                break;
        }
    }

    private void choosePhotoFromCamGalleryDialog() {
        final CharSequence colors[] = new CharSequence[]{"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateAdActivity.this);
        builder.setTitle("Choose Photo");

        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (colors[position].toString()) {
                    case "Camera":
                        cameraIntentPhoto();

                        break;
                    case "Gallery":

                        galleryIntentPhoto();
                        break;
                    case "Cancel":

                        dialog.dismiss();
                        break;
                    default:

                        dialog.dismiss();
                        break;
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_PHOTO) {
                onCaptureImageResultPhoto();
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                onSelectFromGalleryResultPhoto(data);
            }
        }


    }


    private void onCaptureImageResultPhoto() {
        if (imgUri == null) {

            File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Azoog" + File.separator);
            if (!root.exists()) {
                root.mkdir();
            }
            File imageDir = new File(root, UUID.randomUUID() + ".png");

            imgUri = Uri.fromFile(imageDir);

        }
        fileToUploadPathImg = imgUri.getEncodedPath();
        setImageIntoImageView(fileToUploadPathImg);


    }

    private void onSelectFromGalleryResultPhoto(Intent data) {

        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        fileToUploadPathImg = cursor.getString(columnIndex);
        setImageIntoImageView(fileToUploadPathImg);

    }

    private void setImageIntoImageView(String url) {
        Picasso.with(this).load("file://" + url).centerCrop().resize(120, 120).into(ad_img_iv);

    }

    private void galleryIntentPhoto() {

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(pickIntent, "Select Photo"), REQUEST_GALLERY_PHOTO);
    }


    private Uri imgUri;


    /**
     * Request code
     */
    private static final int REQUEST_CAMERA_PHOTO = 1;

    private static final int REQUEST_GALLERY_PHOTO = 2;

    private void cameraIntentPhoto() {

        File root = new File(Environment.getExternalStorageDirectory() + File.separator + "PIO" + File.separator);
        if (!root.exists()) {
            root.mkdir();
        }

        File imageDir = new File(root, UUID.randomUUID() + ".png");

        imgUri = Uri.fromFile(imageDir);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, REQUEST_CAMERA_PHOTO);
    }

    private boolean isValidData() {
        if (title_et.getText().toString().trim().isEmpty()) {
            Toast.makeText(CreateAdActivity.this, "Please enter title", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
