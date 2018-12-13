package com.mtandev.inventoryappstageone;


import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;

import com.mtandev.inventoryappstageone.data.InventoryContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri mCurrentItemUri;

    private EditText mItemNameEditText;
    private EditText mItemPriceEditText;
    private EditText mItemQuantityEditText;
    private EditText mItemSupplierNumberEditText;
    private Spinner mItemSupplierNameSpinner;

    private int mSupplierName = InventoryContract.InventoryEntry.SUPPLIER_UNKNOWN;

    private boolean mItemHasChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mItemHasChanged = true;
            Log.d("message", "onTouch");
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("message", "onCreate");

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        if (mCurrentItemUri == null) {
            setTitle(getString(R.string.add_item));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_item));
            getSupportLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        mItemNameEditText = findViewById(R.id.edittext_itemname);
        mItemPriceEditText = findViewById(R.id.edittext_itemprice);
        mItemQuantityEditText = findViewById(R.id.edittext_itemquantity);
        mItemSupplierNameSpinner = findViewById(R.id.suppliername_spinner);
        mItemSupplierNumberEditText = findViewById(R.id.edittext_suppliernumber);

        mItemNameEditText.setOnTouchListener(touchListener);
        mItemPriceEditText.setOnTouchListener(touchListener);
        mItemQuantityEditText.setOnTouchListener(touchListener);
        mItemSupplierNameSpinner.setOnTouchListener(touchListener);
        mItemSupplierNumberEditText.setOnTouchListener(touchListener);

        setupSpinner();
    }

    private void setupSpinner() {

        ArrayAdapter productSupplierNameSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);

        productSupplierNameSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mItemSupplierNameSpinner.setAdapter(productSupplierNameSpinnerAdapter);

        mItemSupplierNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_amazon))) {
                        mSupplierName = InventoryContract.InventoryEntry.SUPPLIER_AMAZON;
                    } else if (selection.equals(getString(R.string.supplier_costco))) {
                        mSupplierName = InventoryContract.InventoryEntry.SUPPLIER_COSTCO;
                    } else if (selection.equals(getString(R.string.supplier_traderjoes))) {
                        mSupplierName = InventoryContract.InventoryEntry.SUPPLIER_TRADERJOES;
                        } else if (selection.equals(getString(R.string.supplier_target))) {
                            mSupplierName = InventoryContract.InventoryEntry.SUPPLIER_TARGET;
                    } else {
                        mSupplierName = InventoryContract.InventoryEntry.SUPPLIER_UNKNOWN;
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
               mSupplierName = InventoryContract.InventoryEntry.SUPPLIER_UNKNOWN;
           }
        });
    }

    private void saveItem() {
        String itemNameString = mItemNameEditText.getText().toString().trim();
        String itemPriceString = mItemPriceEditText.getText().toString().trim();
        String itemQuantityString = mItemQuantityEditText.getText().toString().trim();
        String itemSupplierPhoneNumberString = mItemSupplierNumberEditText.getText().toString().trim();

        if (mCurrentItemUri == null) {
            if (TextUtils.isEmpty(itemNameString)) {
                Toast.makeText(this, getString(R.string.name_required), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(itemPriceString)) {
                Toast.makeText(this, getString(R.string.price_required), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(itemQuantityString)) {
                Toast.makeText(this, getString(R.string.quantity_required), Toast.LENGTH_SHORT).show();
                return;
            }

            if (mSupplierName == InventoryContract.InventoryEntry.SUPPLIER_UNKNOWN) {
                Toast.makeText(this, getString(R.string.supplier_required), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(itemSupplierPhoneNumberString)) {
                Toast.makeText(this, getString(R.string.phone_required), Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues contentValues = new ContentValues();

            contentValues.put(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME, itemNameString);
            contentValues.put(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE, itemPriceString);
            contentValues.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, itemQuantityString);
            contentValues.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME, mSupplierName);
            contentValues.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER, itemSupplierPhoneNumberString);

            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, contentValues);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.save_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.save_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {

            if (TextUtils.isEmpty(itemNameString)) {
                Toast.makeText(this, getString(R.string.name_required), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(itemPriceString)) {
                Toast.makeText(this, getString(R.string.price_required), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(itemQuantityString)) {
                Toast.makeText(this, getString(R.string.quantity_required), Toast.LENGTH_SHORT).show();
                return;
            }

            if (mSupplierName == InventoryContract.InventoryEntry.SUPPLIER_UNKNOWN) {
                Toast.makeText(this, getString(R.string.supplier_required), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(itemSupplierPhoneNumberString)) {
                Toast.makeText(this, getString(R.string.phone_required), Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues contentValues = new ContentValues();

            contentValues.put(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME, itemNameString);
            contentValues.put(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE, itemPriceString);
            contentValues.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, itemQuantityString);
            contentValues.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME, mSupplierName);
            contentValues.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER, itemSupplierPhoneNumberString);

            int rowsChanged = getContentResolver().update(mCurrentItemUri, contentValues, null, null);

            if (rowsChanged == 0) {
                Toast.makeText(this, getString(R.string.update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_success),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
            public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        Log.d("message", "Open Main Activity");
        return true;
    }

    @Override
            public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.save_action:
                saveItem();
                return true;
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(MainActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(MainActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
            public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
            public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_ITEM_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER
        };

        return new android.support.v4.content.CursorLoader(this, mCurrentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            int itemNameColIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
            int itemPriceColIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
            int itemQuantityColIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY);
            int itemSupplierColIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME);
            int supplierNoColIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER);

            String currentName = data.getString(itemNameColIndex);
            int currentPrice = data.getInt(itemPriceColIndex);
            int currentQuantity = data.getInt(itemQuantityColIndex);
            int currentSupplier = data.getInt(itemSupplierColIndex);
            int currentSupNo = data.getInt(supplierNoColIndex);

            mItemNameEditText.setText(currentName);
            Integer.toString(currentPrice);
            Integer.toString(currentQuantity);
            mItemSupplierNumberEditText.setText(Integer.toString(currentSupNo));

            switch (currentSupplier) {
                case InventoryContract.InventoryEntry.SUPPLIER_AMAZON:
                    mItemSupplierNameSpinner.setSelection(1);
                    break;
                case InventoryContract.InventoryEntry.SUPPLIER_COSTCO:
                    mItemSupplierNameSpinner.setSelection(2);
                    break;
                case InventoryContract.InventoryEntry.SUPPLIER_TARGET:
                    mItemSupplierNameSpinner.setSelection(3);
                    break;
                case InventoryContract.InventoryEntry.SUPPLIER_TRADERJOES:
                    mItemSupplierNameSpinner.setSelection(4);
                    break;
                default:
                    mItemSupplierNameSpinner.setSelection(0);
                    break;
            }
        }
    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mItemNameEditText.setText("");
        mItemPriceEditText.setText("");
        mItemQuantityEditText.setText("");
        mItemSupplierNameSpinner.setSelection(0);
        mItemSupplierNumberEditText.setText("");

    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_changes);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.no_continue, new DialogInterface.OnClickListener() {
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
