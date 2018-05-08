package com.emeric.nicot.atable.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.fragment.NotifContentFragment;
import com.emeric.nicot.atable.fragment.SalonContentFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "debug main ";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar mToolbar;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId, facebookId;
    private ImageView imageViewProfilPic;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salon_header);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mToolbar = findViewById(R.id.toolbarMain);

        if (user != null) {
            userId = user.getUid();
            if (!user.getProviderData().isEmpty() && user.getProviderData().get(1).getProviderId().equals("facebook.com") ){
                Log.d(TAG, "provider id : " + user.getProviderData().get(1).getUid());
                facebookId = user.getProviderData().get(1).getUid();
            }
            else{
                Log.d(TAG, "provider id : " + user.getProviderData().get(1).getUid());
            }
        } else {
            Intent i = new Intent(getApplicationContext(), LoginChoiceActivity.class);
            startActivity(i);
            finish();
        }
        // Set ViewPager
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set TabLayout inside toolbar
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_group);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_palette);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Integer position = tab.getPosition();
                if (position == 0) {
                    getSupportActionBar().setTitle("Salon");
                } else {
                    getSupportActionBar().setTitle("Création");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Salon");
    }

    public boolean onPrepareOptionsMenu(Menu menu){
        final MenuItem profilPicItem = menu.findItem(R.id.profil_pic_menu);
        FrameLayout rootView = (FrameLayout) profilPicItem.getActionView();
        imageViewProfilPic = rootView.findViewById(R.id.image_view_profil_pic_menu);

        //TODO Work on placeholder & different url than facebook
        Glide.with(this)
                .load("https://graph.facebook.com/" + facebookId + "/picture?type=normal")
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_checked)
                        .error(R.drawable.ic_checked)
                        .circleCrop()
                        .dontAnimate())
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageViewProfilPic.setImageDrawable(resource);
                    }
                });

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(profilPicItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_notification:
                Intent i = new Intent(getApplicationContext(), NotificationActivity.class);
                i.putExtra("userId",user.getUid());
                startActivity(i);
                return true;

            case R.id.profil_pic_menu:
                Intent j = new Intent(getApplicationContext(), ProfilSettingsActivity.class);
                j.putExtra("facebookId", facebookId);
                startActivity(j);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SalonContentFragment(), "");
        adapter.addFragment(new NotifContentFragment(), "");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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