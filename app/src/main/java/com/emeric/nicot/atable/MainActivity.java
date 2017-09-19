package com.emeric.nicot.atable;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicot Emeric on 05/06/2017.
 */

public class MainActivity extends FragmentActivity {

    TabLayout tabs;
    ViewPager viewPager;
    TextView textView;
    Button button;
    SessionManagement session;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_salon);

        //session = new SessionManagement(getApplicationContext());
        textView = (TextView) findViewById(R.id.textViewUser);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            textView.setText(user.getEmail());
        } else {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        }





        // Set ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside toolbar
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(1).setIcon(R.drawable.ic_notifications);
        button = (Button) findViewById(R.id.buttonLogOut);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*session.logoutUser();
                session.checkLogin();*/
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter2 adapter2 = new Adapter2(getSupportFragmentManager());
        adapter2.addFragment(new SalonContentFragment(), "Mes Salons");
        adapter2.addFragment(new NotifContentFragment(), "");
        viewPager.setAdapter(adapter2);

       /* android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(adapter2.getItem(0),"tabsSalon").commit();
        fragmentManager.beginTransaction().add(adapter2.getItem(1),"tabsNotif").commit();*/

    }


    class Adapter2 extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter2(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}