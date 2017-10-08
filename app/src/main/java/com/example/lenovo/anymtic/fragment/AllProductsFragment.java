package com.example.lenovo.anymtic.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.anymtic.R;
import com.example.lenovo.anymtic.activity.LoginActivity;
import com.example.lenovo.anymtic.activity.MainActivity;
import com.example.lenovo.anymtic.adapters.MyRecyclerViewAdapter;
import com.example.lenovo.anymtic.app.AppController;
import com.example.lenovo.anymtic.models.Subjects;
import com.example.lenovo.anymtic.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllProductsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    TextView txt;
    int mCurCheckPosition = 0;
    private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";

    ArrayList savedList;

    public AllProductsFragment() {}
    // TODO: Rename and change types and number of parameters
    public static AllProductsFragment newInstance(String param1, String param2) {
        AllProductsFragment fragment = new AllProductsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v  = inflater.inflate(R.layout.fragment_all, container, false);
        if (savedInstanceState != null) {
            super.onActivityCreated(savedInstanceState);
            Toast.makeText(getContext(), "Saved loaded", Toast.LENGTH_SHORT).show();
            return v;
        } else {
            mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new MyRecyclerViewAdapter(getDataSet());

            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            makeJsonArryReq();
            return v;
        }
    }


    private void makeJsonArryReq() {
        showProgressDialog();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest postRequest = new StringRequest(Request.Method.GET, Const.URL_PRODUCTS,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.d("Response", response);
                        int k =0;
                        ArrayList results = new ArrayList<Subjects>();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            Boolean error = jObj.getBoolean("error");
                            if (error == false) {
                                JSONArray user = jObj.getJSONArray("products");
                                for(int i=0; i<user.length(); i++){
                                    try {
                                        //Toast.makeText(getContext(), response.getJSONObject(i).getString("name"), Toast.LENGTH_SHORT).show();
                                        Subjects obj = new Subjects(
                                                user.getJSONObject(i).getString("name"),
                                                user.getJSONObject(i).getInt("price"),
                                                user.getJSONObject(i).getString("photoUrl"),
                                                user.getJSONObject(i).getString("_id"),
                                                0,
                                                user.getJSONObject(i).getString("ownerName")
                                        );
                                        results.add(k++, obj);
                                        mRecyclerView.setAdapter(new MyRecyclerViewAdapter(results));
                                    } catch (JSONException e) {
                                        Toast.makeText(getContext(), "Internet error has occured", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {

                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(getContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        hideProgressDialog();
                    }
                }
        );


        queue.add(postRequest);
        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_arry);
    }

    private ArrayList<Subjects> getDataSet() {
        ArrayList results = new ArrayList<Subjects>();
        for (int index = 0; index < 1; index++) {
            Subjects obj = new Subjects("Food name",
                    15, "url", "product_id", 0, "A2");
            results.add(index, obj);
        }
        return results;
    }

    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelableArrayList();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
