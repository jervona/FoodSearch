package nyc.c4q.foodsearch.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nyc.c4q.foodsearch.mode.view.Business;
import nyc.c4q.foodsearch.recycleview.BusinessAdapter;
import nyc.c4q.foodsearch.mode.view.BusinessModel;
import nyc.c4q.foodsearch.R;
import nyc.c4q.foodsearch.api.YelpService;
import nyc.c4q.foodsearch.constants.Constant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment {

    private View v;
    private String term;
    private RecyclerView rv;
    List <Business> businessList = new ArrayList <>();
    private EditText userinput;

    private BusinessAdapter adapter;
    AHBottomNavigation bottom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_second, container, false);
        bottom = getActivity().findViewById(R.id.bottom_navigation);
        rv = v.findViewById(R.id.food_rv);
        rv.addItemDecoration(new DividerItemDecoration(v.getContext(), DividerItemDecoration.VERTICAL));
        rv.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new BusinessAdapter(businessList);
        rv.setAdapter(adapter);
        setupRetrofit(term);
        userinput = v.findViewById(R.id.search_edit);
//        setup();
        search();
        return v;

    }

    public void setupRetrofit(String term) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.yelp.com/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        YelpService yelpService = retrofit.create(YelpService.class);
        Call <BusinessModel> call = yelpService.getResults
                ("Bearer " + Constant.API_KEY, term, -73.9415728, 40.743309);
        call.enqueue(new Callback <BusinessModel>() {
            @Override
            public void onResponse(Call <BusinessModel> call, Response <BusinessModel> response) {
                try {
                    if (response.isSuccessful()) {
                        BusinessModel businessModel = response.body();
                        businessList = businessModel.getBusinesses();
                        adapter.swap(businessList);
                        Log.d("onResponse: ", "" + businessList);
                    } else
                        Log.d("onResponse: ", response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call <BusinessModel> call, Throwable t) {
                Log.d("onFailure: ", "" + t.getMessage());
            }
        });
    }

    public void search() {
        userinput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if ((keyCode == KeyEvent.KEYCODE_ENTER) && !TextUtils.isEmpty(userinput.getText().toString())) {
                        hideSoftKeyboard();
                        String searchText = userinput.getText().toString();
                        setupRetrofit(searchText);
                    }
                }
                return false;
            }
        });
    }

    public void setup() {
//        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//
//                float tran = bottom.getTranslationY() + dy;
//
//                boolean scrooldown = dy > 0;
//
//                if (scrooldown) {
//                    tran = Math.min(tran, bottom.getHeight());
//                } else {
//                    tran = Math.max(tran, 0f);
//                }
//                bottom.setTranslationY(tran);
//            }
//        });
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(v.getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(userinput.getWindowToken(), 0);
    }


}
