package info.si2.iista.volunteernetworks.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import info.si2.iista.bolunteernetworks.apiclient.ItemModel;
import info.si2.iista.volunteernetworks.Contribution;
import info.si2.iista.volunteernetworks.R;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 8/9/15
 * Project: Virde
 */
public class Model {

    public static View getItem (Context c, ItemModel item, int id) {

        switch (item.getFieldType()) {
            case ItemModel.ITEM_EDIT_TEXT:
            case ItemModel.ITEM_EDIT_TEXT_BIG:
            case ItemModel.ITEM_EDIT_NUMBER:
                return getItemEditText(c, item, id);

            case ItemModel.ITEM_DATE:
                return getItemDate(c, item, id);

            case ItemModel.ITEM_DATETIME:
                break;

            case ItemModel.ITEM_GEOPOS:
                return getItemLocation(c, item, id);

            case ItemModel.ITEM_IMAGE:
                return getItemImage(c, item, id);

            case ItemModel.ITEM_FILE:
                break;

            case ItemModel.ITEM_SPINNER:
                break;
        }

        return null;

    }

    public static View getItemEditText (Context c, ItemModel item, int id) {

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

    public static View getItemLocation (Context c, ItemModel item, int id) {

        // ImageView - Map Image
        ImageView imageView = new ImageView(c);
        imageView.setTag(item.getFieldType());
        imageView.setBackgroundColor(Color.GRAY);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // LayoutParamas - ImageView
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.height = Util.convertDpToPixel(c, 220);
        imageView.setLayoutParams(p);

        // LinearLayout - Content
        LinearLayout layout = new LinearLayout(c);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setId(item.getId());
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

    public static View getItemImage (Context c, ItemModel item, int id) {

        // ImageView - Map Image
        ImageView imageView = new ImageView(c);
        imageView.setTag(item.getFieldType());
        imageView.setBackgroundColor(Color.GRAY);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // LayoutParamas - ImageView
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.height = Util.convertDpToPixel(c, 220);
        imageView.setLayoutParams(p);

        // LinearLayout - Content
        LinearLayout layout = new LinearLayout(c);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setId(item.getId());
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

    public static View getItemDate (final Context c, ItemModel item, int id) {

        final String itemLabel = item.getFieldLabel();
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog dialog = new DatePickerDialog();
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                try { // Establecer fecha seleccionada por el usuario

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String stringDate = String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear) + "-" + String.valueOf(year);
                    Date date = sdf.parse(stringDate);
                    stringDate = parseDateToString(date);

                    ((Contribution) c).setDate(itemLabel + ": " + stringDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        };
        dialog.setOnDateSetListener(dateSetListener);

        Date today = myCalendar.getTime();
        String todayString = parseDateToString(today);

        // Texto contendor de la fecha
        TextView dateText = new TextView(c);
        dateText.setText(itemLabel + ": " + todayString);
        dateText.setTag(item.getFieldType());
        dateText.setTextSize(16);

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
        textView.setLayoutParams(p2);

        // LinearLayout - Content
        LinearLayout layout = new LinearLayout(c);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setId(item.getId());
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

    public static String parseDateToString (Date date) {

        return DateFormat.format("dd MMM yyyy", date).toString();

    }


















}
