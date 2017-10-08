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
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.lenovo.anymtic.R;
import com.example.lenovo.anymtic.app.AppController;
import com.example.lenovo.anymtic.models.BusketItems;
import com.example.lenovo.anymtic.utils.Const;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class BusketAdapter extends RecyclerView
        .Adapter<BusketAdapter
        .DataObjectHolder> {



    private static String LOG_TAG = "BusketAdapter";
    private static ArrayList<BusketItems> mDataset;
    private static MyClickListener myClickListener;

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }


    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        DBHelper dbHelper;
        NumberPicker countPicker;
        TextView name , price;
        ImageView delete;

        public DataObjectHolder(final View itemView) {
            super(itemView);

            countPicker = (NumberPicker) itemView.findViewById(R.id.numberPicker);
            name = (TextView) itemView.findViewById(R.id.item_name);
            price = (TextView) itemView.findViewById(R.id.item_price);
            delete = (ImageView) itemView.findViewById(R.id.deleteItem);
            countPicker.setMinValue(1);
            countPicker.setMaxValue(10);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
            // создаем объект для создания и управления версиями БД
            dbHelper = new DBHelper(itemView.getContext());
            countPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    mDataset.get(getAdapterPosition()).setCount(newVal);
                    // создаем объект для данных
                    ContentValues cv = new ContentValues();
                    // подключаемся к БД
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    // подготовим значения для обновления
                    cv.put("count", newVal);
                    // обновляем по id
                    int updCount = db.update("busket", cv, "item_id = '"+mDataset.get(getAdapterPosition()).getId()+"'",
                            null);

                    Log.d(LOG_TAG, "updated rows count = " + updCount);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Count is "+mDataset.get(getAdapterPosition()).getCount() , Toast.LENGTH_SHORT).show();
                }
            });

        }
        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);

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
                        + "price integer,"
                        + "count integer" + ");");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        }
    }



    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;

    }

    public BusketAdapter(ArrayList<BusketItems> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.busket_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        holder.name.setText(mDataset.get(position).getName());
        holder.price.setText(String.valueOf(mDataset.get(position).getPrice()) + " тенге");
        holder.countPicker.setValue(mDataset.get(position).getCount());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position);
            }
        });
    }


    public void addItem(BusketItems dataObj, int index) {
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

    public BusketItems getItem(int position) {
        return mDataset.get(position);
    }



}