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
    private static final String EXTRA_SCHED_ID =
            "cse.osu.edu.flexscheduler.sched_id";

    private ViewPager mViewPager;
    private List<Sched> mScheds;

    public static Intent newIntent(Context packageContext, UUID schedId) {
        Intent intent = new Intent(packageContext, SchedPagerActivity.class);
        intent.putExtra(EXTRA_SCHED_ID, schedId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID schedId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_SCHED_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);

        mScheds = SchedLab.get(this).getScheds();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Sched sched = mScheds.get(position);
                return SchedFragment.newInstance(sched.getId());
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
                Sched sched = mScheds.get(position);
                if (sched.getTitle() != null) {
                    setTitle(sched.getTitle());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        for (int i = 0; i < mScheds.size(); i++) {
            if (mScheds.get(i).getId().equals(schedId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onSchedUpdated(Sched sched) {

    }
}
