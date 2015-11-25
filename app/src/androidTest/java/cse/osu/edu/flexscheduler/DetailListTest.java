package cse.osu.edu.flexscheduler;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by Young Suk Cho on 11/24/2015.
 */
public class DetailListTest extends ActivityInstrumentationTestCase2<DetailList>{

    private DetailList mDetailList;

    public DetailListTest() {
        super(DetailList.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mDetailList = getActivity();
    }

    public void testPrecondition(){
        assertNotNull(mDetailList);
    }

    public void testFindBestSpot() {
        
    }

    @Override
    protected void tearDown() throws Exception {
        mDetailList.finish();
    }
}
