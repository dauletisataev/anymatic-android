package com.example.lenovo.anymtic.fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.anymtic.R;
import com.example.lenovo.anymtic.activity.MainActivity;
import com.example.lenovo.anymtic.adapters.BusketAdapter;
import com.example.lenovo.anymtic.adapters.MyRecyclerViewAdapter;
import com.example.lenovo.anymtic.models.BusketItems;
import com.example.lenovo.anymtic.models.Subjects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BusketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusketFragment extends Fragment {
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
    private static String LOG_TAG = "BusketActivity";

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    TextView txt;
    Button addOrder;
    DBHelper dbHelper;
    private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";

    public BusketFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoviesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusketFragment newInstance(String param1, String param2) {
        BusketFragment fragment = new BusketFragment();
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
        View v = inflater.inflate(R.layout.fragment_busket, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.busket_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        dbHelper = new DBHelper(getContext());
        mAdapter = new BusketAdapter(getDataSet());

        addOrder = (Button) v.findViewById(R.id.addOrder);
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        dbHelper = new DBHelper(getContext());
        addOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // создаем объект для данных
                ContentValues cv = new ContentValues();
                // подключаемся к БД
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                // делаем запрос всех данных из таблицы mytable, получаем Cursor
                Cursor c = db.query("busket", null, null, null, null, null, null);

                showProgressDialog();
                // ставим позицию курсора на первую строку выборки
                // если в выборке нет строк, вернется false
                int i =0;
                if (c.moveToFirst()) {

                    // определяем номера столбцов по имени в выборке
                    int idColIndex = c.getColumnIndex("item_id");
                    int countColIndex = c.getColumnIndex("count");
                    int nameColIndex = c.getColumnIndex("name");
                    int priceColIndex = c.getColumnIndex("price");
                    int orderColIndex = c.getColumnIndex("order_id");
                    do {
                        // получаем значения по номерам столбцов и пишем все в лог
                        Log.d(LOG_TAG,
                                "orderId = " + c.getInt(orderColIndex) +
                                        ", item_id = " + c.getString(idColIndex) +
                                        ", count = " + c.getString(countColIndex));
                        makeOrder(c.getString(orderColIndex), c.getString(idColIndex), c.getString(nameColIndex), c.getInt(priceColIndex), c.getInt(countColIndex), i);
                        // переход на следующую строку
                        // а если следующей нет (текущая - последняя), то false - выходим из цикла
                        i++;
                    } while (c.moveToNext());
                    hideProgressDialog();
                    db.execSQL("delete from busket");
                } else{
                    hideProgressDialog();
                    Log.d(LOG_TAG, "0 rows");
                }

                c.close();
            }
        });
        return  v;
    }
    public  void makeOrder(final String order_id, final String item_id, final String name, final int price, final int count, final int pos){

        Toast.makeText(getContext(), order_id, Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://anymatic.herokuapp.com/orders/"+order_id.toString();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        // response
                        Log.d("Response", response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            Boolean error = jObj.getBoolean("error");
                            if (error == false) {
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                //db.execSQL("delete from busket");

                                    ((BusketAdapter) mRecyclerView.getAdapter()).deleteItem(0);
                                     mRecyclerView.getAdapter().notifyItemRemoved(0);

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
                        hideProgressDialog();
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("item_name", name);
                params.put("totalPrice", String.valueOf(price*count));
                params.put("totalQty", String.valueOf(count));
                params.put("item_id", item_id);

                return params;
            }
        };
        queue.add(postRequest);
    }
    private ArrayList<BusketItems> getDataSet() {
        // создаем объект для данных
        ContentValues cv = new ContentValues();
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("busket", null, null, null, null, null, null);
        ArrayList results = new ArrayList<BusketItems>();
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("item_id");
            int nameColIndex = c.getColumnIndex("name");
            int priceColIndex = c.getColumnIndex("price");
            int countColIndex = c.getColumnIndex("count");
            int i = 0;
            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG_TAG,
                        "ID = " + c.getString(idColIndex) +
                                ", name = " + c.getString(nameColIndex) +
                                ", count = " + c.getInt(countColIndex) +
                                ", price = " + c.getInt(priceColIndex));
                BusketItems obj = new BusketItems(c.getString(nameColIndex),
                        c.getInt(priceColIndex), c.getString(idColIndex), c.getInt(countColIndex));
                results.add(i, obj);

                i++;
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();



        mRecyclerView.setAdapter(new BusketAdapter(results));
        return results;

    }
    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "anymaticData", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table busket ("
                    + "id integer primary key autoincrement,"
                    + "item_id text,"
                    + "name text,"
                    + "order_id text,"
                    + "price integer,"
                    + "count integer" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
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
