package excal.rave.Activities;

import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import excal.rave.Assistance.DeviceDetailFragment;
import excal.rave.Assistance.DeviceListFragment;
import excal.rave.R;


public class Tab extends AppCompatActivity {
    Party party = null;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static DeviceListFragment listFragment = null;
    public static DeviceDetailFragment detailFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);


//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        //party.discoverDevices();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent fromMain2Activity = getIntent();
        String role = fromMain2Activity.getStringExtra("ROLE");
        party = new Party(Tab.this,getApplicationContext(),role);
        party.setup();
        party.Resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        party.Pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        party.Destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.atn_direct_enable:
                party.checkWifiEnable();
                return true;

            case R.id.atn_direct_discover:
                party.discoverDevices();
                return true;

            case R.id.noOfClients:
                if(party.role.equals("MASTER"))
                    Toast.makeText(this, "No of clients connected: "+ DeviceDetailFragment.client_list.size(), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Beware! You are only a client..", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.tabs:
                Intent intent = new Intent(this,Tab.class);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        listFragment = new DeviceListFragment();
        adapter.addFragment(listFragment, "PEERS");
/*        if(Party.role.equals("MASTER")){
            adapter.addFragment(new TwoFragment(), "ALL SONG");
        }*/
//        adapter.addFragment(new ThreeFragment(), "SHARED SONGS");
        adapter.addFragment(new DeviceListFragment(), "Extra");
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

