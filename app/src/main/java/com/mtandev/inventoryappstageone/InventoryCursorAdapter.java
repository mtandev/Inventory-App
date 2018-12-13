package com.mtandev.inventoryappstageone;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mtandev.inventoryappstageone.data.InventoryContract;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {


        Log.d("Position " + cursor.getPosition() + ":", " bindView() has been called.");

        TextView itemNameTextView = view.findViewById(R.id.name_item_text_view);
        TextView itemPriceTextView = view.findViewById(R.id.item_price_text_view);
        TextView itemQuantityTextView = view.findViewById(R.id.item_quantity_text_view);
        Button itemSaleButton = view.findViewById(R.id.sale_button);

        final int columnIdIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
        int itemNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
        int itemPriceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
        int itemQuantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY);

        final String itemID = cursor.getString(columnIdIndex);
        String itemName = cursor.getString(itemNameColumnIndex);
        String itemPrice = cursor.getString(itemPriceColumnIndex);
        final String itemQuantity = cursor.getString(itemQuantityColumnIndex);

        itemSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InventoryActivity Activity = (InventoryActivity) context;
                Activity.itemSaleCount(Integer.valueOf(itemID), Integer.valueOf(itemQuantity));
            }
        });

        itemNameTextView.setText(itemID + " ) " + itemName);
        itemPriceTextView.setText(context.getString(R.string.item_price) + " : " + itemPrice + "  " + context.getString(R.string.item_price_currency));
        itemQuantityTextView.setText(context.getString(R.string.item_quantity) + " : " + itemQuantity);

        Button productEditButton = view.findViewById(R.id.edit_button);
        productEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                Uri currentItemUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, Long.parseLong(itemID));
                intent.setData(currentItemUri);
                context.startActivity(intent);
            }
        });

    }
}
