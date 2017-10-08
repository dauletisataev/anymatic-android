package com.example.lenovo.anymtic.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.lenovo.anymtic.R;
import com.example.lenovo.anymtic.app.AppController;
import com.example.lenovo.anymtic.models.Orders;
import com.example.lenovo.anymtic.utils.Const;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class OrderAdapter extends RecyclerView
        .Adapter<OrderAdapter
        .DataObjectHolder> {



    private static String LOG_TAG = "OrderAdapter";
    private static ArrayList<Orders> mDataset;
    private static MyClickListener myClickListener;

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }


    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        ListView list;
        TextView totalPrice, date;

        public DataObjectHolder(final View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.date);
            totalPrice = (TextView) itemView.findViewById(R.id.total_price);
            list = (ListView) itemView.findViewById(R.id.list_order);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);

        }
    }



    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;

    }

    public OrderAdapter(ArrayList<Orders> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        holder.totalPrice.setText("Итого: " + mDataset.get(position).getPrice());
        holder.date.setText( mDataset.get(position).getDate() );

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(
                holder.list.getContext(), android.R.layout.simple_list_item_1, mDataset.get(position).getItems()
        );
        holder.list.setAdapter(listAdapter);
    }



    public void addItem(Orders dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public  void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Orders getItem(int position) {
        return mDataset.get(position);
    }



}