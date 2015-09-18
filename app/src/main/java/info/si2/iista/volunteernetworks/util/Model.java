package info.si2.iista.volunteernetworks.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import info.si2.iista.volunteernetworks.Contribution;
import info.si2.iista.volunteernetworks.R;
import info.si2.iista.volunteernetworks.apiclient.ItemModel;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 8/9/15
 * Project: Virde
 */
public class Model {

    public static View getItem (Context c, ItemModel item, int id, boolean lastItem) {

        switch (item.getFieldType()) {
            case ItemModel.ITEM_EDIT_TEXT:
            case ItemModel.ITEM_EDIT_TEXT_BIG:
            case ItemModel.ITEM_EDIT_NUMBER:
                return getItemEditText(c, item, id, lastItem);

            case ItemModel.ITEM_DATE:
                return getItemDate(c, item, id, lastItem);

            case ItemModel.ITEM_DATETIME:
                break;

            case ItemModel.ITEM_GEOPOS:
                return getItemLocation(c, item, id, lastItem);

            case ItemModel.ITEM_IMAGE:
                return getItemImage(c, item, id, lastItem);

            case ItemModel.ITEM_FILE:
                break;

            case ItemModel.ITEM_SPINNER:
                return getItemSpinner(c, item, id, lastItem);
        }

        return null;

    }

    /**
     * Construye una vista de tipo LinearLayout contenedora de un EditText
     * @param c Context donde se creará la vista
     * @param item ItemModel que contiente los datos de la vista
     * @param id ID de la vista anterior para situar esta debajo
     * @return View de tipo EditText
     */
    public static View getItemEditText (Context c, ItemModel item, int id, boolean lastItem) {

        // Construct View
        TextInputLayout textInputLayout = new TextInputLayout(c);
        textInputLayout.setHint(item.getFieldLabel());

        // EditText - Title hint
        AppCompatEditText editText = new AppCompatEditText(c);

        switch (item.getFieldType()) { // Tipo de texto
            case ItemModel.ITEM_EDIT_TEXT:
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case ItemModel.ITEM_EDIT_TEXT_BIG:
                editText.setSingleLine(false);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                break;
            case ItemModel.ITEM_EDIT_NUMBER:
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
        }

        textInputLayout.addView(editText);
        textInputLayout.setTag(item.getFieldType());

        // TextView - Description
        TextView textView = new TextView(c);
        textView.setText(item.getFieldDescription());
        textView.setTextSize(12);

        // LinearLayout - Content
        LinearLayout layout = new LinearLayout(c);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setId(item.getId());
        layout.setTag(item.getFieldName());
        layout.addView(textInputLayout);
        layout.addView(textView);

        // LinearLayout - LayoutParams
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p1.topMargin = Util.convertDpToPixel(c, 16);
        p1.leftMargin = Util.convertDpToPixel(c, 12);
        p1.rightMargin = Util.convertDpToPixel(c, 12);
        textInputLayout.setLayoutParams(p1);

        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p2.leftMargin = Util.convertDpToPixel(c, 16);
        p2.rightMargin = Util.convertDpToPixel(c, 16);

        if (lastItem)
            p2.bottomMargin = Util.convertDpToPixel(c, 16);

        textView.setLayoutParams(p2);

        // RelativeLayout - Params, view resultante
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (id == -1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.addRule(RelativeLayout.BELOW, id);
        }
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layout.setLayoutParams(params);

        return layout;

    }

    /**
     * Construye una vista de tipo LinearLayout contenedora de un ImageView que contendrá un tile de la posición del usuario
     * @param c Context donde se creará la vista
     * @param item ItemModel que contiente los datos de la vista
     * @param id ID de la vista anterior para situar esta debajo
     * @return View de tipo ImageView
     */
    public static View getItemLocation (final Context c, ItemModel item, int id, boolean lastItem) {

        // ImageView - Map Image
        ImageView imageView = new ImageView(c);
        imageView.setTag(item.getFieldType());
        imageView.setBackgroundColor(Color.GRAY);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // ImageView - OnClick
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Contribution) c).actionMap();
            }
        });

        // LayoutParamas - ImageView
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.height = Util.convertDpToPixel(c, 220);

        if (lastItem)
            p.bottomMargin = Util.convertDpToPixel(c, 16);

        imageView.setLayoutParams(p);

        // LinearLayout - Content
        LinearLayout layout = new LinearLayout(c);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setId(item.getId());
        layout.setTag(item.getFieldName());
        layout.addView(imageView);

        // RelativeLayout - Params, view resultante
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (id == -1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.addRule(RelativeLayout.BELOW, id);
        }
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.leftMargin = Util.convertDpToPixel(c, 16);
        params.rightMargin = Util.convertDpToPixel(c, 16);
        params.topMargin = Util.convertDpToPixel(c, 16);

        layout.setLayoutParams(params);

        return layout;

    }

    /**
     * Construye una vista de tipo LinearLayout contenedora de un ImageView en la que el usuario podrá seleccionar o hacer una foto
     * @param c Context donde se creará la vista
     * @param item ItemModel que contiente los datos de la vista
     * @param id ID de la vista anterior para situar esta debajo
     * @return View de tipo ImageView
     */
    public static View getItemImage (final Context c, ItemModel item, int id, boolean lastItem) {

        final int idImage = item.getId();

        // ImageView - Map Image
        final ImageView imageView = new ImageView(c);
        imageView.setId(item.getId());
        imageView.setTag(item.getFieldType());
        imageView.setBackgroundColor(Color.GRAY);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // ImageView - OnClick
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Contribution)c).intentCameraGallery(idImage);
            }
        });

        // LayoutParamas - ImageView
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.height = Util.convertDpToPixel(c, 220);

        if (lastItem)
            p.bottomMargin = Util.convertDpToPixel(c, 16);

        imageView.setLayoutParams(p);

        // LinearLayout - Content
        LinearLayout layout = new LinearLayout(c);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setId(item.getId());
        layout.setTag(item.getFieldName());
        layout.addView(imageView);

        // RelativeLayout - Params, view resultante
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (id == -1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.addRule(RelativeLayout.BELOW, id);
        }
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.leftMargin = Util.convertDpToPixel(c, 16);
        params.rightMargin = Util.convertDpToPixel(c, 16);
        params.topMargin = Util.convertDpToPixel(c, 16);

        layout.setLayoutParams(params);

        return layout;

    }

    /**
     * Construye una vista de tipo LinearLayout contenedora de un Spinner
     * @param c Context donde se creará la vista
     * @param item ItemModel que contiente los datos de la vista
     * @param id ID de la vista anterior para situar esta debajo
     * @return View de tipo Spinner
     */
    public static View getItemSpinner (Context c, ItemModel item, int id, boolean lastItem) {

        // Spinner data
        String[] items = item.getFieldOptions().split("\\|");

        for (int i=0; i<items.length; i++) {
            items[i] = items[i].trim();
        }

        // Spinner view
        AppCompatSpinner spinner = new AppCompatSpinner(c);
        spinner.setTag(item.getFieldType());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(c, android.R.layout.simple_spinner_item, items);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        // LayoutParamas - ImageView
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p1.topMargin = Util.convertDpToPixel(c, 16);
        p1.leftMargin = Util.convertDpToPixel(c, 8);
        p1.rightMargin = Util.convertDpToPixel(c, 12);
        spinner.setLayoutParams(p1);

        // View - Separator
        View separator = new View(c);
        separator.setBackgroundColor(ContextCompat.getColor(c, R.color.separator));

        // LayoutParams - Separator
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.height = Util.convertDpToPixel(c, 1);
        p.topMargin = Util.convertDpToPixel(c, 4);
        p.leftMargin = Util.convertDpToPixel(c, 16);
        p.rightMargin = Util.convertDpToPixel(c, 16);
        separator.setLayoutParams(p);

        // TextView - Description
        TextView textView = new TextView(c);
        textView.setText(item.getFieldLabel());
        textView.setTextSize(12);

        // LayoutParams - Description
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p2.topMargin = Util.convertDpToPixel(c, 4);
        p2.leftMargin = Util.convertDpToPixel(c, 16);
        p2.rightMargin = Util.convertDpToPixel(c, 16);

        if (lastItem)
            p2.bottomMargin = Util.convertDpToPixel(c, 16);

        textView.setLayoutParams(p2);

        // LinearLayout - Content
        LinearLayout layout = new LinearLayout(c);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setId(item.getId());
        layout.setTag(item.getFieldName());
        layout.addView(spinner);
        layout.addView(separator);
        layout.addView(textView);

        // RelativeLayout - Params, view resultante
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (id == -1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.addRule(RelativeLayout.BELOW, id);
        }
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        layout.setLayoutParams(params);

        return layout;

    }

    /**
     * Construye una vista de tipo LinearLayout contenedora de un TextView que contendrá la fecha que seleccione el usuario
     * @param c Context donde se creará la vista
     * @param item ItemModel que contiente los datos de la vista
     * @param id ID de la vista anterior para situar esta debajo
     * @return View de tipo TextView
     */
    public static View getItemDate (final Context c, ItemModel item, int id, boolean lastItem) {

        final String itemLabel = item.getFieldLabel() + ": %s";
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog dialog = new DatePickerDialog();

        Date today = myCalendar.getTime();
        String todayString = Util.parseDateToString(today);

        // Texto contendor de la fecha
        final TextView dateText = new TextView(c);
        dateText.setText(String.format(itemLabel, todayString));
        dateText.setTag(item.getFieldType());
        dateText.setTextSize(16);

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                try { // Establecer fecha seleccionada por el usuario

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String stringDate = String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(year);
                    Date date = sdf.parse(stringDate);
                    stringDate = Util.parseDateToString(date);

                    dateText.setText(String.format(itemLabel, stringDate));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        };
        dialog.setOnDateSetListener(dateSetListener);

        // View - Separator
        View separator = new View(c);
        separator.setBackgroundColor(ContextCompat.getColor(c, R.color.separator));

        // LayoutParams - Separator
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.height = Util.convertDpToPixel(c, 1);
        p.topMargin = Util.convertDpToPixel(c, 4);
        separator.setLayoutParams(p);

        // TextView - Description
        TextView textView = new TextView(c);
        textView.setText(item.getFieldDescription());
        textView.setTextSize(12);

        // LayoutParams - Description
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p2.topMargin = Util.convertDpToPixel(c, 4);

        if (lastItem)
            p2.bottomMargin = Util.convertDpToPixel(c, 16);

        textView.setLayoutParams(p2);

        // LinearLayout - Content
        LinearLayout layout = new LinearLayout(c);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setId(item.getId());
        layout.setTag(item.getFieldName());
        layout.addView(dateText);
        layout.addView(separator);
        layout.addView(textView);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show(((Activity) c).getFragmentManager(), "DatePickerDialog");
            }
        });

        // RelativeLayout - Params, view resultante
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (id == -1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.addRule(RelativeLayout.BELOW, id);
        }
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.leftMargin = Util.convertDpToPixel(c, 16);
        params.rightMargin = Util.convertDpToPixel(c, 16);
        params.topMargin = Util.convertDpToPixel(c, 16);
        layout.setLayoutParams(params);

        return layout;

    }

    /**
     * Obtiene el valor de un campo de tipo TextInputLayout
     * @param layout Layout contenedora de los datos
     * @return String del TextInputLayout
     */
    public static String[] getEditText (LinearLayout layout) {

        String[] values = new String[2];

        TextInputLayout textInputLayout = (TextInputLayout) layout.getChildAt(0);
        EditText editText = textInputLayout.getEditText();

        values[0] = layout.getTag().toString();
        if (editText != null) {
            values[1] = editText.getText().toString();
        } else {
            values[1] = "";
        }

        return values;
    }

    /**
     * Obtiene el valor de un campo de tipo fecha
     * @param layout Layout contenedora de los datos
     * @return String de la fecha en formato yyyy-MM-dd
     */
    public static String[] getDate (LinearLayout layout) {

        String[] values = new String[2];

        TextView text = (TextView) layout.getChildAt(0);

        String parseDate = text.getText().toString();
        int pos = parseDate.indexOf(":") + 2;
        parseDate = parseDate.substring(pos);
        parseDate = Util.parseDateToStringServer(Util.parseStringToDate(parseDate));

        values[0] = layout.getTag().toString();
        values[1] = parseDate;

        return values;
    }

    public static String[] getStringSpinner (LinearLayout layout) {

        String[] values = new String[2];

        Spinner spinner = (Spinner)layout.getChildAt(0);
        String result = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();

        values[0] = layout.getTag().toString();
        values[1] = result;

        return values;
    }










}
