package com.example.myevents.screens.main.events;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.myevents.API.authenticated.AuthenticatedApiManager;
import com.example.myevents.base.AuthenticatedScreen;
import com.example.myevents.base.BaseFragment;
import com.example.myevents.R;
import com.example.myevents.models.ApiError;
import com.example.myevents.models.Event;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewEventFragment extends BaseFragment implements AuthenticatedScreen {

    public static final String TAG = NewEventFragment.class.getSimpleName();

    private EditText nameEditText;
    private EditText locationEditText;
    private Button submitButton;

    private AuthenticatedApiManager authenticatedApiManager;

    private String name;
    private String location;

    private LocationFragmentListener mListener;

    public NewEventFragment() {
        // Required empty public constructor
    }

    public static NewEventFragment newInstance() {
        NewEventFragment fragment = new NewEventFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authenticatedApiManager = AuthenticatedApiManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_location, container, false);

        nameEditText = view.findViewById(R.id.name);
        locationEditText = view.findViewById(R.id.location);
        submitButton = view.findViewById(R.id.submitLocation);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString();
                location = locationEditText.getText().toString();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(location)) {
                    Event event = new Event(name, location);
                    authenticatedApiManager.createEvent(event).enqueue(new Callback<List<Event>>() {

                        @Override
                        public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                            if (response.isSuccessful()) {
                                mListener.onNewEvertCreatedSuccessfully();
                            } else {
                                try {
                                    String errorJson = response.errorBody().string();
                                    ApiError apiError = parseApiErrorString(errorJson);
                                    actBasedOnApiErrorCode(apiError);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Event>> call, Throwable t) {

                        }
                    });
                }
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LocationFragmentListener) {
            mListener = (LocationFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LocationFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void notLoggedInAnymore() {
        if (mListener != null) {
            mListener.onNewEvertCreationFailure();
        }
    }

    public interface LocationFragmentListener {
        void onNewEvertCreatedSuccessfully();

        void onNewEvertCreationFailure();
    }
}
