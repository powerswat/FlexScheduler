package cse.osu.edu.flexscheduler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class SchedPagerActivity extends AppCompatActivity
        implements SchedFragment.Callbacks {
    private static final String EXTRA_Sched_ID =
            "com.bignerdranch.android.criminalintent.Sched_id";

    private ViewPager mViewPager;
    private List<Sched> mScheds;

    public static Intent newIntent(Context packageContext, UUID SchedId) {
        Intent intent = new Intent(packageContext, SchedPagerActivity.class);
        intent.putExtra(EXTRA_Sched_ID, SchedId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sched_pager);

        UUID SchedId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_Sched_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_sched_pager_view_pager);

        mScheds = SchedLab.get(this).getScheds();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Sched Sched = mScheds.get(position);
                return SchedFragment.newInstance(Sched.getId());
            }

            @Override
            public int getCount() {
                return mScheds.size();
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                Sched Sched = mScheds.get(position);
                if (Sched.getTitle() != null) {
                    setTitle(Sched.getTitle());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        for (int i = 0; i < mScheds.size(); i++) {
            if (mScheds.get(i).getId().equals(SchedId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onSchedUpdated(Sched Sched) {

    }
}
