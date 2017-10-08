package com.example.lenovo.anymtic.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.lenovo.anymtic.R;
import com.example.lenovo.anymtic.app.AppController;
import com.example.lenovo.anymtic.helpers.SQLiteHandler;
import com.example.lenovo.anymtic.helpers.SessionManager;
import com.example.lenovo.anymtic.models.Subjects;
import com.example.lenovo.anymtic.utils.Const;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {



    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static ArrayList<Subjects> mDataset;
    private static MyClickListener myClickListener;
    private SQLiteHandler db;
    private SessionManager session;
    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        DBHelper dbHelper;
        ImageView img, likeImageView,shareImageView;
        TextView name , price, store;

        public DataObjectHolder(final View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.coverImageView);
            name = (TextView) itemView.findViewById(R.id.name);
            price = (TextView) itemView.findViewById(R.id.price);
            store = (TextView) itemView.findViewById(R.id.store_name);
            likeImageView = (ImageView)  itemView.findViewById(R.id.likeImage);
            shareImageView = (ImageView)  itemView.findViewById(R.id.shareImage);

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
            // создаем объект для создания и управления версиями БД
            dbHelper = new DBHelper(itemView.getContext());
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLiteHandler userdb = new SQLiteHandler(itemView.getContext());
                    HashMap<String, String> user = userdb.getUserDetails();

                    String orderId = user.get("order_id");
                    // создаем объект для данных
                    ContentValues cv = new ContentValues();
                    // подключаемся к БД
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    String item_id = mDataset.get(getAdapterPosition()).getId();
                    Log.d(LOG_TAG, "Clicked id of item is:  "+item_id);
                    int id = mDataset.get(getAdapterPosition()).getInOrder();
                    if( id == 0){
                        mDataset.get(getAdapterPosition()).setInOrder(1);
                        likeImageView.setImageResource(R.drawable.cart_active);

                        Log.d(LOG_TAG, "--- Insert in mytable: ---");
                        // подготовим данные для вставки в виде пар: наименование столбца - значение

                        cv.put("item_id", item_id);
                        cv.put("name", mDataset.get(getAdapterPosition()).getName());
                        cv.put("price", mDataset.get(getAdapterPosition()).getPrice());
                        cv.put("order_id", orderId);
                        cv.put("count", 1);
                        // вставляем запись и получаем ее ID
                        long rowID = db.insert("busket", null, cv);
                        Log.d(LOG_TAG, "row inserted, ID = " + rowID);

                        Toast.makeText(itemView.getContext(), name.getText()+" added to favourites",Toast.LENGTH_SHORT).show();

                    }else{
                        mDataset.get(getAdapterPosition()).setInOrder(0);
                        likeImageView.setImageResource(R.drawable.cart);
                        //makeImageRequest(img);
                        long rowID = db.delete("busket", "item_id"+ "='" + item_id +"'", null);
                        Log.d(LOG_TAG, "row deleted, ID = " + rowID);
                        Toast.makeText(itemView.getContext(),name.getText()+" removed from favourites",Toast.LENGTH_SHORT).show();


                    }

                }
            });


        }

        @Override
        public void onClick(View v) {
            //myClickListener.onItemClick(getAdapterPosition(), v);

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
                        + "order_id text,"
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

    public MyRecyclerViewAdapter(ArrayList<Subjects> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.name.setText(mDataset.get(position).getName());
        holder.price.setText(String.valueOf(mDataset.get(position).getPrice()) + " тенге");
        holder.store.setText("от "+String.valueOf(mDataset.get(position).getStore()));
        makeImageRequest(holder.img, mDataset.get(position).getUrl());
        holder.likeImageView.setTag(R.drawable.cart);

    }


    public void addItem(Subjects dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void makeImageRequest(final ImageView img, String imgUrl) {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        imageLoader.get(imgUrl, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Image Load Error: " + error.getMessage());
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    // load image into imageview
                    img.setImageBitmap(response.getBitmap());
                }
            }
        });

    }


}