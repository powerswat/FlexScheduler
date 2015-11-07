package cse.osu.edu.flexscheduler;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class SchedFragment extends Fragment {

    private static final String ARG_Sched_ID = "Sched_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO= 2;

    private Sched mSched;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckbox;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Callbacks mCallbacks;

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        void onSchedUpdated(Sched Sched);
    }

    public static SchedFragment newInstance(UUID SchedId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_Sched_ID, SchedId);

        SchedFragment fragment = new SchedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID SchedId = (UUID) getArguments().getSerializable(ARG_Sched_ID);
        mSched = SchedLab.get(getActivity()).getSched(SchedId);
        mPhotoFile = SchedLab.get(getActivity()).getPhotoFile(mSched);
    }

    @Override
    public void onPause() {
        super.onPause();

        SchedLab.get(getActivity())
                .updateSched(mSched);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sched, container, false);

        mTitleField = (EditText) v.findViewById(R.id.Sched_title);
        mTitleField.setText(mSched.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getActivity() == null) {
                    return;
                }
                mSched.setTitle(s.toString());
                updateSched();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.Sched_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mSched.getDate());
                dialog.setTargetFragment(SchedFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckbox = (CheckBox) v.findViewById(R.id.Sched_solved);
        mSolvedCheckbox.setChecked(mSched.isSolved());
        mSolvedCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSched.setSolved(isChecked);
                updateSched();
            }
        });

        mReportButton = (Button)v.findViewById(R.id.Sched_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getSchedReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.sched_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));

                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button)v.findViewById(R.id.Sched_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mSched.getSuspect() != null) {
            mSuspectButton.setText(mSched.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.Sched_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.Sched_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoFile == null || mPhotoFile.getPath() == null) {
                    return;
                }

                Intent intent = ImageActivity.newIntent(getActivity(), Uri.parse(mPhotoFile.getPath()));
                ImageActivity.startWithTransition(getActivity(), intent, mPhotoView);
            }
        });
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mSched.setDate(date);
            updateSched();
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for.
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME,
            };
            // Perform your query - the contactUri is like a "where"
            // clause here
            ContentResolver resolver = getActivity().getContentResolver();
            Cursor c = resolver
                    .query(contactUri, queryFields, null, null, null);

            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data -
                // that is your suspect's name.
                c.moveToFirst();

                String suspect = c.getString(0);
                mSched.setSuspect(suspect);
                updateSched();
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            updateSched();
            updatePhotoView();
        }
    }

    private void updateSched() {
        SchedLab.get(getActivity()).updateSched(mSched);
        mCallbacks.onSchedUpdated(mSched);
    }

    private void updateDate() {
        mDateButton.setText(mSched.getDate().toString());
    }

    private String getSchedReport() {
        String solvedString = null;
        if (mSched.isSolved()) {
            solvedString = getString(R.string.sched_report_solved);
        } else {
            solvedString = getString(R.string.sched_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mSched.getDate()).toString();
        String suspect = mSched.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.sched_report_no_suspect);
        } else {
            suspect = getString(R.string.sched_report_suspect, suspect);
        }
        String report = getString(R.string.sched_report,
                mSched.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
