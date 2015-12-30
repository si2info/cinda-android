package info.si2.iista.volunteernetworks.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import info.si2.iista.volunteernetworks.AdapterSpinner;
import info.si2.iista.volunteernetworks.Contribution;
import info.si2.iista.volunteernetworks.DictionaryValue;
import info.si2.iista.volunteernetworks.R;
import info.si2.iista.volunteernetworks.apiclient.ItemModel;
import info.si2.iista.volunteernetworks.apiclient.ItemModelValue;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 8/9/15
 * Project: Virde
 */
public class Model {

    public static View getItem (Context c, ItemModel item, int id, boolean lastItem, boolean isDetail) {

        switch (item.getFieldType()) {
            case ItemModel.ITEM_DICTIONARY:
                return getItemDictionary(c, item, id, lastItem, isDetail);

            case ItemModel.ITEM_EDIT_TEXT:
            case ItemModel.ITEM_EDIT_TEXT_BIG:
            case ItemModel.ITEM_EDIT_NUMBER:
                return getItemEditText(c, item, id, lastItem, isDetail);

            case ItemModel.ITEM_DATE:
                return getItemDate(c, item, id, lastItem, isDetail);

            case ItemModel.ITEM_DATETIME:
                return getItemDateTime(c, item, id, lastItem);

            case ItemModel.ITEM_GEOPOS:
                return getItemLocation(c, item, id, lastItem, isDetail);

            case ItemModel.ITEM_IMAGE:
                return getItemImage(c, item, id, lastItem, isDetail);

            case ItemModel.ITEM_FILE:
                break;

            case ItemModel.ITEM_SPINNER:
                return getItemSpinner(c, item, id, lastItem, isDetail);

            case ItemModel.ITEM_DESCRIPTION:
                return getItemDescription(c, item, id, lastItem);
        }

        return null;

    }

    /**
     * Construye una vista de tipo RelativeLayout contenedora de un TextView
     * @param c Context donde se creará la vista
     * @param item ItemModel que contiente los datos de la vista
     * @param id ID de la vista anterior para situar esta debajo
     * @return View RelativeLayout
     */
    public static View getItemDictionary (final Context c, final ItemModel item, int id, boolean lastItem, boolean isDetail) {

        LayoutInflater inflater = LayoutInflater.from(c);
        View modelDictionary = inflater.inflate(R.layout.model_dictionary, null, false);
        TextView textView = (TextView)modelDictionary.findViewById(R.id.textDictionary);
        TextView title = (TextView)modelDictionary.findViewById(R.id.title);
        TextView info = (TextView)modelDictionary.findViewById(R.id.info);

        textView.setText(c.getString(R.string.dic_hint));
        title.setText(item.getFieldLabel());

        // TextView - Select value dictionary
        if (!isDetail) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(c, DictionaryValue.class);
                    intent.putExtra("code", Integer.valueOf(item.getFieldOptions()));
                    ((Contribution) c).startActivityForResult(intent, Contribution.DICTIONARY_VALUE_REQUEST);
                }
            });
        }

        // TextView - Description
        if (item.getFieldDescription().equals("")) {
            info.setVisibility(View.INVISIBLE);
        } else {
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    infoDialogFragment dialog = infoDialogFragment.newInstance(item.getFieldLabel(), item.getFieldDescription());
                    dialog.show(((Contribution) c).getFragmentManager(), "Info");
                }
            });
        }

        // RelativeLayout - Params, view resultante
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (id == -1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.addRule(RelativeLayout.BELOW, id);
        }
        if (lastItem)
            params.bottomMargin = Util.convertDpToPixel(c, 16);

        modelDictionary.setLayoutParams(params);

        modelDictionary.setTag(item.getFieldType());
        modelDictionary.setId(item.getId());

        return modelDictionary;

    }

    /**
     * Construye una vista de tipo RelativeLayout contenedora de un TextView
     * @param c Context donde se creará la vista
     * @param item ItemModel que contiente los datos de la vista
     * @param id ID de la vista anterior para situar esta debajo
     * @return View RelativeLayout
     */
    public static View getItemDateTime (final Context c, final ItemModel item, int id, boolean lastItem) {

        LayoutInflater inflater = LayoutInflater.from(c);
        View modelDateTime = inflater.inflate(R.layout.model_datetime, null, false);
        TextView title = (TextView)modelDateTime.findViewById(R.id.title);
        TextView datetime = (TextView)modelDateTime.findViewById(R.id.dateTime);

        // Date Time
        Date date = new Date();
        String dateSt = Util.parseDateToString("dd/MM/yyyy HH:mm", date);
        datetime.setText(dateSt);
        title.setText(item.getFieldLabel());

        // RelativeLayout - Params, view resultante
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (id == -1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.addRule(RelativeLayout.BELOW, id);
        }
        if (lastItem)
            params.bottomMargin = Util.convertDpToPixel(c, 16);

        modelDateTime.setLayoutParams(params);

        modelDateTime.setTag(item.getFieldType());
        modelDateTime.setId(item.getId());

        return modelDateTime;

    }

    /**
     * Construye una vista de tipo RelativeLayout contenedora de un TextView
     * @param c Context donde se creará la vista
     * @param item ItemModel que contiente los datos de la vista
     * @param id ID de la vista anterior para situar esta debajo
     * @return View RelativeLayout
     */
    public static View getItemDescription (final Context c, final ItemModel item, int id, boolean lastItem) {

        LayoutInflater inflater = LayoutInflater.from(c);
        View modelDescription = inflater.inflate(R.layout.model_description, null, false);
        TextView textView = (TextView)modelDescription.findViewById(R.id.description);

        // Description
        String description = "<i>" + item.getFieldDescription() + "</i>";
        textView.setText(Html.fromHtml(description));

        // RelativeLayout - Params, view resultante
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (id == -1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.addRule(RelativeLayout.BELOW, id);
        }
        if (lastItem)
            params.bottomMargin = Util.convertDpToPixel(c, 16);

        modelDescription.setLayoutParams(params);

        modelDescription.setTag(item.getFieldType());
        modelDescription.setId(item.getId());

        return modelDescription;

    }

    /**
     * Construye una vista de tipo LinearLayout contenedora de un EditText
     * @param c Context donde se creará la vista
     * @param item ItemModel que contiente los datos de la vista
     * @param id ID de la vista anterior para situar esta debajo
     * @return View de tipo EditText
     */
    public static View getItemEditText (final Context c, final ItemModel item, int id, boolean lastItem, boolean isDetail) {

        LayoutInflater inflater = LayoutInflater.from(c);
        View modelEditText = inflater.inflate(R.layout.model_edit_text, null, false);
        EditText editText = (EditText)modelEditText.findViewById(R.id.editText);
        TextView title = (TextView)modelEditText.findViewById(R.id.title);
        TextView info = (TextView)modelEditText.findViewById(R.id.info);

        // Title
        title.setText(item.getFieldLabel());

        // Construct View
        editText.setHint(item.getFieldLabel());

        if (isDetail) {
            editText.setEnabled(false);
        }

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

        // TextView - Description
        if (item.getFieldDescription().equals("")) {
            info.setVisibility(View.INVISIBLE);
        } else {
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    infoDialogFragment dialog = infoDialogFragment.newInstance(item.getFieldLabel(), item.getFieldDescription());
                    dialog.show(((Contribution) c).getFragmentManager(), "Info");
                }
            });
        }

        // RelativeLayout - Params, view resultante
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (id == -1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.addRule(RelativeLayout.BELOW, id);
        }
        if (lastItem)
            params.bottomMargin = Util.convertDpToPixel(c, 16);

        modelEditText.setLayoutParams(params);

        modelEditText.setTag(item.getFieldType());
        modelEditText.setId(item.getId());

        return modelEditText;

    }

    /**
     * Construye una vista de tipo LinearLayout contenedora de un ImageView que contendrá un tile de la posición del usuario
     * @param c Context donde se creará la vista
     * @param item ItemModel que contiente los datos de la vista
     * @param id ID de la vista anterior para situar esta debajo
     * @return View de tipo ImageView
     */
    public static View getItemLocation (final Context c, final ItemModel item, int id, boolean lastItem, boolean isDetail) {

        LayoutInflater inflater = LayoutInflater.from(c);
        View modelMap = inflater.inflate(R.layout.model_location, null, false);
        TextView textLocation = (TextView)modelMap.findViewById(R.id.textLocation);
        TextView info = (TextView)modelMap.findViewById(R.id.info);
        ImageView imgMap = (ImageView)modelMap.findViewById(R.id.mapLocation);

        // TextView - Location
        textLocation.setText(item.getFieldLabel());
        textLocation.setTypeface(Util.getRobotoLight(c));

        // TextView - Description
        if (item.getFieldDescription().equals("")) {
            info.setVisibility(View.INVISIBLE);
        } else {
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    infoDialogFragment dialog = infoDialogFragment.newInstance(item.getFieldLabel(), item.getFieldDescription());
                    dialog.show(((Contribution) c).getFragmentManager(), "Info");
                }
            });
        }

        // ImageView - Map Image
        imgMap.setTag(item.getFieldType());

        // ImageView - OnClick
        if (!isDetail) {
            imgMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Contribution) c).actionMap();
                }
            });
        }

        // RelativeLayout - Params, view resultante
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (id == -1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.addRule(RelativeLayout.BELOW, id);
        }
        if (lastItem)
            params.bottomMargin = Util.convertDpToPixel(c, 16);

        modelMap.setLayoutParams(params);

        modelMap.setTag(item.getFieldType());
        modelMap.setId(item.getId());

        return modelMap;

    }

    /**
     * Construye una vista de tipo LinearLayout contenedora de un ImageView en la que el usuario podrá seleccionar o hacer una foto
     * @param c Context donde se creará la vista
     * @param item ItemModel que contiente los datos de la vista
     * @param id ID de la vista anterior para situar esta debajo
     * @return View de tipo ImageView
     */
    public static View getItemImage (final Context c, final ItemModel item, int id, boolean lastItem, boolean isDetail) {

        LayoutInflater inflater = LayoutInflater.from(c);
        View modelImage = inflater.inflate(R.layout.model_image, null, false);
        TextView textImage = (TextView)modelImage.findViewById(R.id.title);
        TextView info = (TextView)modelImage.findViewById(R.id.info);
        ImageView imgToSelect = (ImageView)modelImage.findViewById(R.id.imageSelected);

        final int idImage = item.getId();

        // TextView - Image;
        textImage.setText(item.getFieldLabel());

        // TextView - Description
        if (item.getFieldDescription().equals("")) {
            info.setVisibility(View.INVISIBLE);
        } else {
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    infoDialogFragment dialog = infoDialogFragment.newInstance(item.getFieldLabel(), item.getFieldDescription());
                    dialog.show(((Contribution) c).getFragmentManager(), "Info");
                }
            });
        }

        // ImageView - OnClick
        if (!isDetail) {
            imgToSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Contribution) c).checkCameraAndStoragePermission(idImage);
                }
            });
        }

        // RelativeLayout - Params, view resultante
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (id == -1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.addRule(RelativeLayout.BELOW, id);
        }
        if (lastItem)
            params.bottomMargin = Util.convertDpToPixel(c, 16);

        modelImage.setLayoutParams(params);

        modelImage.setId(item.getId());
        modelImage.setTag(item.getFieldType());

        return modelImage;

    }

    /**
     * Construye una vista de tipo LinearLayout contenedora de un Spinner
     * @param c Context donde se creará la vista
     * @param item ItemModel que contiente los datos de la vista
     * @param id ID de la vista anterior para situar esta debajo
     * @return View de tipo Spinner
     */
    public static View getItemSpinner (final Context c, final ItemModel item, int id, boolean lastItem, boolean isDetail) {

        LayoutInflater inflater = LayoutInflater.from(c);
        View modelSpinner = inflater.inflate(R.layout.model_spinner, null, false);
        AppCompatSpinner spinner = (AppCompatSpinner)modelSpinner.findViewById(R.id.spinner);
        TextView title = (TextView)modelSpinner.findViewById(R.id.textSpinner);
        TextView info = (TextView)modelSpinner.findViewById(R.id.info);

        // Title
        title.setText(item.getFieldLabel());

        // Spinner data
        String[] items = item.getFieldOptions().split("\\|");

        for (int i=0; i<items.length; i++) {
            items[i] = items[i].trim();
        }

        // Spinner view
        AdapterSpinner adapter = new AdapterSpinner(c, R.layout.spinner_style, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (isDetail) {
            spinner.setEnabled(false);
        }

        // TextView - Description
        if (item.getFieldDescription().equals("")) {
            info.setVisibility(View.INVISIBLE);
        } else {
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    infoDialogFragment dialog = infoDialogFragment.newInstance(item.getFieldLabel(), item.getFieldDescription());
                    dialog.show(((Contribution) c).getFragmentManager(), "Info");
                }
            });
        }

        // RelativeLayout - Params, view resultante
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (id == -1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.addRule(RelativeLayout.BELOW, id);
        }
        if (lastItem)
            params.bottomMargin = Util.convertDpToPixel(c, 16);

        modelSpinner.setLayoutParams(params);

        modelSpinner.setId(item.getId());
        modelSpinner.setTag(item.getFieldType());

        return modelSpinner;

    }

    /**
     * Construye una vista de tipo LinearLayout contenedora de un TextView que contendrá la fecha que seleccione el usuario
     * @param c Context donde se creará la vista
     * @param item ItemModel que contiente los datos de la vista
     * @param id ID de la vista anterior para situar esta debajo
     * @return View de tipo TextView
     */
    public static View getItemDate (final Context c, final ItemModel item, int id, boolean lastItem, boolean isDetail) {

        LayoutInflater inflater = LayoutInflater.from(c);
        View modelDate = inflater.inflate(R.layout.model_date, null, false);
        TextView title = (TextView)modelDate.findViewById(R.id.title);
        final TextView textDate = (TextView)modelDate.findViewById(R.id.textDate);
        TextView info = (TextView)modelDate.findViewById(R.id.info);

        title.setText(item.getFieldLabel());
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog dialog = new DatePickerDialog();

        Date today = myCalendar.getTime();
        String todayString = Util.parseDateToString(today);

        // Texto contendor de la fecha
        textDate.setText(todayString);
        textDate.setTypeface(Util.getRobotoLight(c));
        textDate.setTag(item.getFieldType());

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

                    textDate.setText(stringDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        };

        dialog.setOnDateSetListener(dateSetListener);

        // ImageView - Description
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialogFragment dialog = infoDialogFragment.newInstance(item.getFieldLabel(), item.getFieldDescription());
                dialog.show(((Contribution) c).getFragmentManager(), "Info");
            }
        });

        if (!isDetail) {
            textDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.show(((Activity) c).getFragmentManager(), "DatePickerDialog");
                }
            });
        }

        // RelativeLayout - Params, view resultante
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (id == -1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.addRule(RelativeLayout.BELOW, id);
        }
        if (lastItem)
            params.bottomMargin = Util.convertDpToPixel(c, 16);

        modelDate.setLayoutParams(params);

        modelDate.setTag(item.getFieldType());
        modelDate.setId(item.getId());

        return modelDate;

    }

    /**
     * Obtiene el valor de un campo de tipo TextInputLayout
     * @param layout Layout contenedora de los datos
     * @return String del TextInputLayout
     */
    public static String[] getEditText (RelativeLayout layout) {

        String[] values = new String[2];

        EditText editText = (EditText) layout.getChildAt(2);

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
    public static String[] getDate (RelativeLayout layout) {

        String[] values = new String[2];

        TextView text = (TextView) layout.getChildAt(0);

        String parseDate = text.getText().toString();
        parseDate = Util.parseDateToStringServer(Util.parseStringToDate("dd, MMM yyyy", parseDate));

        values[0] = layout.getTag().toString();
        values[1] = parseDate;

        return values;
    }

    /**
     * Obtiene el valor de un campo de tipo fecha
     * @param layout Layout contenedora de los datos
     * @return String de la fecha en formato yyyy-MM-dd
     */
    public static String[] getDateTime (RelativeLayout layout) {

        String[] values = new String[2];

        TextView text = (TextView) layout.getChildAt(1);

        String date = text.getText().toString();

        values[0] = layout.getTag().toString();
        values[1] = date;

        return values;
    }

    /**
     * Obtiene el valor seleccionado del Spinner
     * @param layout Layout contenedora de los datos
     * @return Opción seleccionada
     */
    public static String[] getStringSpinner (RelativeLayout layout) {

        String[] values = new String[2];

        Spinner spinner = (Spinner)layout.getChildAt(2);
        String result = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();

        values[0] = layout.getTag().toString();
        values[1] = result;

        return values;
    }

    /**
     * Modo vista de contribución - Setea el valor al campo para su visualización
     * @param view View contenedora del item a setear
     * @param value Value a setear
     */
    public static void setDataToEditText (RelativeLayout view, ItemModelValue value) {

        if (!value.getValue().equals("") && !value.getValue().equals("false")) {
            EditText editText = (EditText) view.findViewById(R.id.editText);
            editText.setText(value.getValue());
        } else {
            view.setVisibility(View.GONE);
        }

    }

    /**
     * Modo vista de contribución - Setea el valor al campo para su visualización
     * @param view View contenedora del item a setear
     * @param value Value a setear
     */
    public static void setDataToDate (RelativeLayout view, ItemModelValue value) {

        if (!value.getValue().equals("") && !value.getValue().equals("false")) {
            TextView textView = (TextView)view.findViewById(R.id.textDate);
            textView.setText(value.getValue());
        } else {
            view.setVisibility(View.GONE);
        }

    }

    /**
     * Modo vista de contribución - Setea el valor al campo para su visualización
     * @param view View contenedora del item a setear
     * @param value Value a setear
     */
    public static void setDataToDateTime (RelativeLayout view, ItemModelValue value) {

        if (!value.getValue().equals("") && !value.getValue().equals("false")) {
            TextView textView = (TextView) view.findViewById(R.id.dateTime);
            textView.setText(value.getValue());
        } else {
            view.setVisibility(View.GONE);
        }

    }

    /**
     * Modo vista de contribución - Setea el valor al campo para su visualización
     * @param view View contenedora del item a setear
     * @param value Value a setear
     */
    public static void setDataToSpinner (Context c, RelativeLayout view, ItemModelValue value) {

        // Spinner view
        if (!value.getValue().equals("") && !value.getValue().equals("false")) {

            Spinner spinner = (Spinner)view.findViewById(R.id.spinner);

            String[] items = new String[1];
            items[0] = value.getValue();
            AdapterSpinner adapter = new AdapterSpinner(c, R.layout.spinner_style, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

        } else {
            view.setVisibility(View.GONE);
        }

    }

    /**
     * Modo vista de contribución - Setea el valor al campo para su visualización
     * @param view View contenedora del item a setear
     * @param value Value a setear
     */
    public static void setDataToDictionary (RelativeLayout view, ItemModelValue value) {

        if (!value.getValue().equals("") && !value.getValue().equals("false")) {
            TextView textView = (TextView) view.findViewById(R.id.textDictionary);
            textView.setText(value.getValue());
        } else {
            view.setVisibility(View.GONE);
        }

    }

    /**
     * Modo vista de contribución - Setea el valor al campo para su visualización
     * @param view View contenedora del item a setear
     * @param value Value a setear
     */
    public static void setDataToImage (Context c, RelativeLayout view, ItemModelValue value) {

        if (value.getValue() != null) {
            ImageView imageView = (ImageView)view.findViewById(R.id.imageSelected);
            if (!value.getValue().equals("") && !value.getValue().equals("false")) {
                Picasso.with(c)
                        .load(value.getValue())
                        .into(imageView);
            } else {
                view.setVisibility(View.GONE);
            }
        } else {
            view.setVisibility(View.GONE);
        }

    }

    /**
     * Check if position[] have a correct lat and lng
     * @param value Value to check
     * @param view View to hide if value is null or zero
     * @return Boolean true if have a correct value
     */
    public static boolean checkItemMap (RelativeLayout view, ItemModelValue value) {

        if (value.getValue() != null) {
            if (!value.getValue().equals("")) {
                String[] position = value.getValue().split(",");
                if (position.length == 2) {
                    if (!position[0].equals("0.0") && !position[1].equals("0.0")) {
                        return true;
                    } else {
                        view.setVisibility(View.GONE);
                    }
                } else {
                    view.setVisibility(View.GONE);
                }
            } else {
                view.setVisibility(View.GONE);
            }
        } else {
            view.setVisibility(View.GONE);
        }

        return false;

    }

    /** Util **/

    public static class infoDialogFragment extends DialogFragment {

        private String title, message;

        static infoDialogFragment newInstance(String title, String message) {
            infoDialogFragment f = new infoDialogFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("message", message);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            title = getArguments().getString("title");
            message = getArguments().getString("message");
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setMessage(message);
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

}
