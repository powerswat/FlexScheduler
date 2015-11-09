package cse.osu.edu.flexscheduler;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class SchedListActivity extends SingleFragmentActivity
        implements SchedListFragment.Callbacks, SchedFragment.Callbacks {

    private String uId;

    @Override
    protected Fragment createFragment() {
        Intent i = getIntent();
        uId = i.getExtras().getString("UID");
        return new SchedListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onSchedSelected(Sched sched) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = SchedPagerActivity.newIntent(this, sched.getId());
            intent.putExtra("UID", uId);
            startActivity(intent);

        } else {
            /* Fragment newDetail = SchedFragment.newInstance(sched.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();*/
            startActivity(new Intent(SchedListActivity.this, DetailList.class));
        }
        // startActivity(new Intent(SchedListActivity.this, DetailList.class));
    }

    @Override
    public void onSchedUpdated(Sched sched) {
        SchedListFragment listFragment = (SchedListFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
