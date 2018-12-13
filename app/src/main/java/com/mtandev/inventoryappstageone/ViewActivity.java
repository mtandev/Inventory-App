package com.mtandev.inventoryappstageone;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mtandev.inventoryappstageone.data.InventoryContract;

public class ViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri mCurrentItemUri;

    private TextView mItemNameTV;
    private TextView mItemPriceTV;
    private TextView mItemQuantityTV;
    private TextView mItemSupplierNameSpinner;
    private TextView mItemSupplierPhoneNumberTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        mItemNameTV = findViewById(R.id.product_name_view_text);
        mItemPriceTV = findViewById(R.id.product_price_view_text);
        mItemQuantityTV = findViewById(R.id.product_quantity_view_text);
        mItemSupplierNameSpinner = findViewById(R.id.product_supplier_name_view_text);
        mItemSupplierPhoneNumberTV = findViewById(R.id.product_supplier_phone_number_view_text);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        if (mCurrentItemUri == null) {
            invalidateOptionsMenu();
        } else {
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        Log.d("message","onCreate ViewActivity");

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_ITEM_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(this, mCurrentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
           final int idColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
           int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
           int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
           int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY);
           int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME);
           int supplierNumberColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER);

           String currentName = cursor.getString(nameColumnIndex);
           final int currentPrice = cursor.getInt(priceColumnIndex);
           final int currentQuantity = cursor.getInt(quantityColumnIndex);
           int currentSupplierName = cursor.getInt(supplierNameColumnIndex);
           final int currentSupplierNumber = cursor.getInt(supplierNumberColumnIndex);

           mItemNameTV.setText(currentName);
           mItemPriceTV.setText(Integer.toString(currentPrice));
           mItemQuantityTV.setText(Integer.toString(currentQuantity));
           mItemSupplierPhoneNumberTV.setText(Integer.toString(currentSupplierNumber));

           switch (currentSupplierName) {
               case InventoryContract.InventoryEntry.SUPPLIER_AMAZON:
                   mItemSupplierNameSpinner.setText(getText(R.string.supplier_amazon));
                   break;

                   case InventoryContract.InventoryEntry.SUPPLIER_COSTCO:
                    mItemSupplierNameSpinner.setText(getText(R.string.supplier_costco));
                    break;

                    case InventoryContract.InventoryEntry.SUPPLIER_TARGET:
                        mItemSupplierNameSpinner.setText(getText(R.string.supplier_target));
                        break;

                        case InventoryContract.InventoryEntry.SUPPLIER_TRADERJOES:
                            mItemSupplierNameSpinner.setText(getText(R.string.supplier_traderjoes));
                            break;

                            default:
                                mItemSupplierNameSpinner.setText(getText(R.string.supplier_unknown));
                                break;
           }

            Button itemDecreaseButton = findViewById(R.id.decrease_button);
           itemDecreaseButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   decreaseCount(idColumnIndex, currentQuantity);
               }
           });

           final Button itemIncreaseButton = findViewById(R.id.increase_button);
           itemIncreaseButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   increaseCount(idColumnIndex, currentQuantity);
               }
           });

           Button itemDeleteButton = findViewById(R.id.delete_button);
           itemDeleteButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   showDeleteConfirmation();
               }
           });

           Button phoneButton = findViewById(R.id.phone_button);
           phoneButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String phone = String.valueOf(currentSupplierNumber);
                   Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                   startActivity(intent);
               }
           });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void decreaseCount(int itemID, int itemQuantity ) {
        itemQuantity = itemQuantity - 1;
        if (itemQuantity >= 0) {
            updateItem(itemQuantity);
            Toast.makeText(this, getString(R.string.quantity_changed), Toast.LENGTH_SHORT).show();
            Log.d("Log", " - itemID: " + itemID + " - quantity: " + itemQuantity + " , decreaseCount.");
        } else {
            Toast.makeText(this,getString(R.string.quantity_changed), Toast.LENGTH_SHORT).show();
        }
    }

    public void increaseCount(int itemID, int itemQuantity) {
        itemQuantity = itemQuantity + 1;
        if (itemQuantity >= 0) {
            updateItem(itemQuantity);
            Toast.makeText(this,getString(R.string.quantity_changed), Toast.LENGTH_SHORT).show();
            Log.d("Log", " + itemID: " + itemID + " + quantity: " + itemQuantity + " , increaseCount.");
        }
    }

    private void updateItem(int itemQuantity) {
        Log.d("message", "updateItem at ViewActivity");

        if (mCurrentItemUri == null) {
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, itemQuantity);

        if (mCurrentItemUri == null) {
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, contentValues);
            if (newUri == null) {
                Toast.makeText(this,getString(R.string.save_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,getString(R.string.save_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int changedRows = getContentResolver().update(mCurrentItemUri, contentValues, null, null);
            if (changedRows == 0) {
                Toast.makeText(this, getString(R.string.update_failed),
                        Toast.LENGTH_SHORT).show();
        } else {
                Toast.makeText(this, getString(R.string.update_success),
                        Toast.LENGTH_SHORT).show();
            }
    }
    }

    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int deletedRows = getContentResolver().delete(mCurrentItemUri, null,null);
            if (deletedRows == 0) {
                Toast.makeText(this, getString(R.string.delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showDeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
