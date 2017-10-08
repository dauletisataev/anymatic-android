package com.example.lenovo.anymtic.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.anymtic.R;
import com.example.lenovo.anymtic.adapters.OrderAdapter;
import com.example.lenovo.anymtic.helpers.SQLiteHandler;
import com.example.lenovo.anymtic.helpers.SessionManager;
import com.example.lenovo.anymtic.models.Orders;
import com.example.lenovo.anymtic.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ProgressDialog pDialog;
    private OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView totalPrice;
    private ListView list;
    ArrayAdapter<String> adapter;


    private SQLiteHandler db;
    private SessionManager session;

    public OrdersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrdersFragment newInstance(String param1, String param2) {
        OrdersFragment fragment = new OrdersFragment();
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
        View v =  inflater.inflate(R.layout.fragment_orders, container, false);
        totalPrice = (TextView) v.findViewById(R.id.total_price);
        list = (ListView) v.findViewById(R.id.list_order);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.orders_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new OrderAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getContext());

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        String order_id = user.get("order_id");
       // makeJsonArryReq(order_id);
        return v;
    }

     public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private void makeJsonArryReq(String order_id) {
        showProgressDialog();
        String url = Const.URL_PRODUCTS + order_id;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.d("Response", response);
                        int k =0;
                        ArrayList orders = new ArrayList<Orders>();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            Boolean error = jObj.getBoolean("error");
                            if (error == false) {
                                JSONArray cart = jObj.getJSONObject("order").getJSONArray("cart");
                                ArrayList<String> listItems=new ArrayList<String>();
                                for(int i=0; i<cart.length(); i++){
                                    ArrayList<String> itemsList = new ArrayList<String>();
                                    try {
                                        JSONObject oneOrder= cart.getJSONObject(i);
                                        JSONArray orderItems = oneOrder.getJSONArray("items");
                                        for(int a=0; a<orderItems.length(); a++){
                                            itemsList.add(cart.getJSONObject(i).getString("name") + "  "
                                                    + String.valueOf(cart.getJSONObject(i).getInt("totalQty"))+ " "
                                                    +String.valueOf(cart.getJSONObject(i).getInt("totalPrice"))+"тг");
                                        }
                                        Orders item = new Orders(
                                                oneOrder.getString("created_at"),
                                                oneOrder.getInt("totalPrice"),
                                                listItems);

                                        orders.add(k++, item);
                                        mRecyclerView.setAdapter(new OrderAdapter(orders));
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
                        Log.e("Error", "Login Error: " + error.getMessage());
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

    private ArrayList<Orders> getDataSet() {
        ArrayList results = new ArrayList<Orders>();
        for (int index = 0; index < 4; index++) {
            ArrayList<String> items = new ArrayList<String>();
            items.add("Бауырсаки  5  250тг");
            items.add("Плов       1  450тг");
            items.add("Чай        2  100тг");
            items.add("Хлеб       3  30тг");
            Orders obj = new Orders("0"+String.valueOf(3+index)+".10.2017",
                    500+50*index, items);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
