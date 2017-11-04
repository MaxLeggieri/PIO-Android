package com.livelife.pioalert;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity {

    static final String tag = ProfileActivity.class.getSimpleName();
    CategoryAdapter itemsAdapter;
    ListView listView;
    Button doneButton;
    ArrayList<Integer> selectedCategories = new ArrayList<>(); // Comma separated ids
    ArrayList<Category> allCats;
    ArrayList<String> userCats;

    MainActivity mainActivity;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedCategories.size() < 3) {
                    doneButton.setText(R.string.choose_more);
                } else {
                    final String cats = TextUtils.join(",",selectedCategories);
                    Log.v(tag,"User selected: "+cats);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!Utility.isNetworkConnected(ProfileActivity.this)){
                                Toast.makeText(ProfileActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                                return ;
                            }
                            WebApi.getInstance().setUserCategories(cats);
                            ProfileActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    PioUser.getInstance().setProfiled(true);
                                    finish();
                                }
                            });
                        }
                    }).start();

                    /*
                    if(WebApi.getInstance().setUserCategories(cats)) {
                        PioUser.getInstance().setProfiled(true);
                        finish();
                    }
                    */
                }
            }
        });
        listView = (ListView) findViewById(R.id.listView);
        if (!Utility.isNetworkConnected(this)){
            Toast.makeText(this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
            return ;
        }
        if (!Utility.isNetworkConnected(ProfileActivity.this)){
            Toast.makeText(ProfileActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
            return ;
        }
        allCats = WebApi.getInstance().getAllCategories();
        userCats = WebApi.getInstance().getUserCats();

        for (Category c: allCats) {
            String cid = ""+c.cid+"";

            if (userCats.contains(cid)) {
                selectedCategories.add(c.cid);
                c.selected = true;
            }
        }

        itemsAdapter = new CategoryAdapter(this,allCats);

        if (selectedCategories.size() >= 3) {
            doneButton.setText(R.string.done);
        } else {
            doneButton.setText(R.string.choose_how_much);
        }


        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Category item = itemsAdapter.getItem(i);
                Log.v(tag,"Cat: "+item.name);
                item.selected = !item.selected;

                if (item.selected) {
                    selectedCategories.add(item.cid);
                } else {
                    selectedCategories.remove((Integer) item.cid);
                }

                Log.v(tag,"Selected: "+selectedCategories.toString());

                if (selectedCategories.size() >= 3) {
                    doneButton.setText(R.string.done);
                } else {
                    doneButton.setText(R.string.choose_how_much);
                }

                itemsAdapter.notifyDataSetChanged();

            }
        });



    }


    public class CategoryAdapter extends ArrayAdapter<Category> {
        public CategoryAdapter(Context context, ArrayList<Category> categories) {
            super(context, 0, categories);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Category cat = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.button_category, parent, false);
            }

            // Lookup view for data population

            ImageView catImg = (ImageView) convertView.findViewById(R.id.catImage);
            TextView catName = (TextView) convertView.findViewById(R.id.catName);
            LinearLayout container = (LinearLayout) convertView.findViewById(R.id.container);

            Picasso.with(ProfileActivity.this).load(cat.imgPath).into(catImg);
            catName.setText(cat.name);

            if (cat.selected) {
                container.setBackgroundColor(Color.parseColor("#FCEEC0"));
            } else {
                container.setBackgroundColor(Color.TRANSPARENT);
            }
            /*
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
            // Populate the data into the template view using the data object
            tvName.setText(user.name);
            tvHome.setText(user.hometown);
            // Return the completed view to render on screen
            */

            return convertView;
        }


    }
}
