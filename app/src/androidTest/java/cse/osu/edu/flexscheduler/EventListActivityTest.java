package cse.osu.edu.flexscheduler;

        import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by Young Suk Cho on 11/24/2015.
 */
public class EventListActivityTest extends ActivityInstrumentationTestCase2<EventListActivity>{

    private EventListActivity mEventListActivity;

    public EventListActivityTest() {
        super(EventListActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mEventListActivity = getActivity();
    }

    public void testPrecondition(){
        assertNotNull(mEventListActivity);
    }

    @Override
    protected void tearDown() throws Exception {
        mEventListActivity.finish();
    }
}
