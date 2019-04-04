package com.gapestation.collegeapp_brian;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.Date;

import static com.backendless.media.SessionBuilder.TAG;

public class ProfileFragment extends Fragment {
    private TextView firstnametext;
    private TextView lastnametext;
    public Profile Profile1 = new Profile("Alan", "Turing");
    public Profile mProfile;
    public static final int REQUEST_DATE_OF_BIRTH = 0;
    Button DatePickerButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup view, Bundle bundle) {
        super.onCreateView(inflater, view, bundle);

        mProfile = new Profile("Brian", "Chamberlain");
        View rootView = inflater.inflate(R.layout.fragment_profile, view, false);
        firstnametext = rootView.findViewById(R.id.profilefirstname);
        lastnametext = rootView.findViewById(R.id.profilelastname);
        lastnametext.setText(Profile1.getLastName());
        firstnametext.setText(Profile1.getFirstName());
        DatePickerButton = (Button)rootView.findViewById(R.id.DatePickerButton);

        DatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mProfile.dateOfBirth);
                dialog.setTargetFragment(ProfileFragment.this, REQUEST_DATE_OF_BIRTH);
                dialog.show(fm, "DialogDateOfBirth");

            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        Log.i("ProfileFragment", "" + requestCode + " " + resultCode);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_DATE_OF_BIRTH){
                mProfile.dateOfBirth = (Date)intent.getSerializableExtra(DatePickerFragment.EXTRA_DATE_OF_BIRTH);
                Log.i("ProfileFragment", mProfile.dateOfBirth.toString());
                DatePickerButton.setText(mProfile.dateOfBirth.toString());
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences sharedPreferences =
                getActivity().getPreferences(Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(ApplicantActivity.EMAIL_PREF, null);
        if (mProfile.getEmail() == null) {
            mProfile.setEmail(email);
        }
        Backendless.Data.of(Profile.class).save(mProfile, new AsyncCallback<Profile>() {
            @Override
            public void handleResponse(Profile response) {
                Log.i(TAG, "Saved profile to Backendless");
            }
            public void handleFault(BackendlessFault fault) {
                Log.i(TAG, "Failed to save profile!" + fault.getMessage());
            }
        });
    }

}
