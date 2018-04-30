package com.example.RestoApp.screens.main.events;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.RestoApp.API.authenticated.AuthenticatedApiManager;
import com.example.RestoApp.base.AuthenticatedScreen;
import com.example.RestoApp.data.local.LocalStorageManager;
import com.example.RestoApp.MyApplication;
import com.example.RestoApp.R;
import com.example.RestoApp.base.BaseFragment;
import com.example.RestoApp.models.ApiError;
import com.example.RestoApp.models.Event;
import com.example.RestoApp.models.dao.DaoSession;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsListFragment extends BaseFragment implements AuthenticatedScreen {

    private EventsListFragmentListener mListener;

    private AuthenticatedApiManager authenticatedApiManager;
    private LocalStorageManager localStorageManager;

    @BindView(R.id.list)
    RecyclerView eventsList;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public EventsListFragment() {
        // Required empty public constructor
    }

    public static EventsListFragment newInstance() {
        EventsListFragment fragment = new EventsListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authenticatedApiManager = AuthenticatedApiManager.getInstance(getActivity());
        localStorageManager = LocalStorageManager.getInstance(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgressBar();
        fetchAllEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        eventsList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void showEventsList(List<Event> events) {
        eventsList.setAdapter(new EventsListAdapter(events));
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void fetchAllEvents() {

        authenticatedApiManager.getEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                hideProgressBar();
                if (response.isSuccessful()) {
                    List<Event> events = response.body();
                    saveEventsInLocalDatabase(events);
                    showEventsList(events);
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
                hideProgressBar();
                getEventsInLocalDatabase();
            }
        });
    }

    private void saveEventsInLocalDatabase(List<Event> events) {
        DaoSession daoSession = ((MyApplication) (getActivity().getApplication())).getDaoSession();
        localStorageManager.saveEventsInLocalDatabase(daoSession, events);
    }

    private void getEventsInLocalDatabase() {
        DaoSession daoSession = ((MyApplication) (getActivity().getApplication())).getDaoSession();
        List<Event> eventsFromDatabase = localStorageManager.getEventsInLocalDatabase(daoSession);
        showEventsList(eventsFromDatabase);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.new_event)
    public void requestShowNewEventPage() {
        if (mListener != null) {
            mListener.onRequestCreateNewEvent();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EventsListFragmentListener) {
            mListener = (EventsListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EventsListFragmentListener");
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
            mListener.onErrorFetchingEvents();
        }
    }

    public interface EventsListFragmentListener {
        void onRequestCreateNewEvent();

        void onErrorFetchingEvents();
    }

    class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ViewHolder> {

        private List<Event> events;

        EventsListAdapter(List<Event> events) {
            this.events = events;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_event, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Event event = events.get(position);
            holder.eventNameTextView.setText(event.getName());
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.event_name)
            TextView eventNameTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
