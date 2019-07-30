package jp.sblo.pandora.aGrep;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import apk.tool.patcher.R;

public class ColorPickerActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_COLOR = "EXTRA_COLOR";
    static final ColorName[] COLOR_NAMES = new ColorName[]{
            new ColorName(0x00000000, "Input Color Code"),
            new ColorName(0xFF000000, "Black"),
            new ColorName(0xFF000080, "Navy"),
            new ColorName(0xFF00008B, "DarkBlue"),
            new ColorName(0xFF0000CD, "MediumBlue"),
            new ColorName(0xFF0000FF, "Blue"),
            new ColorName(0xFF006400, "DarkGreen"),
            new ColorName(0xFF008000, "Green"),
            new ColorName(0xFF008080, "Teal"),
            new ColorName(0xFF008B8B, "DarkCyan"),
            new ColorName(0xFF00BFFF, "DeepSkyBlue"),
            new ColorName(0xFF00CED1, "DarkTurquoise"),
            new ColorName(0xFF00FA9A, "MediumSpringGreen"),
            new ColorName(0xFF00FF00, "Lime"),
            new ColorName(0xFF00FF7F, "SpringGreen"),
            new ColorName(0xFF00FFFF, "Aqua"),
            new ColorName(0xFF00FFFF, "Cyan"),
            new ColorName(0xFF191970, "MidnightBlue"),
            new ColorName(0xFF1E90FF, "DodgerBlue"),
            new ColorName(0xFF20B2AA, "LightSeaGreen"),
            new ColorName(0xFF228B22, "ForestGreen"),
            new ColorName(0xFF2E8B57, "SeaGreen"),
            new ColorName(0xFF2F4F4F, "DarksLateGray"),
            new ColorName(0xFF32CD32, "LimeGreen"),
            new ColorName(0xFF3CB371, "MediumSeaGreen"),
            new ColorName(0xFF40E0D0, "Turquoise"),
            new ColorName(0xFF4169E1, "RoyalBlue"),
            new ColorName(0xFF4682B4, "SteelBlue"),
            new ColorName(0xFF483D8B, "DarksLateBlue"),
            new ColorName(0xFF48D1CC, "MediumTurquoise"),
            new ColorName(0xFF4B0082, "Indigo"),
            new ColorName(0xFF556B2F, "DarkOliveGreen"),
            new ColorName(0xFF5F9EA0, "CadetBlue"),
            new ColorName(0xFF6495ED, "CornFlowerBlue"),
            new ColorName(0xFF66CDAA, "MediumAquamarine"),
            new ColorName(0xFF696969, "DimGray"),
            new ColorName(0xFF6A5ACD, "SlateBlue"),
            new ColorName(0xFF6B8E23, "OliveDrab"),
            new ColorName(0xFF708090, "SlateGray"),
            new ColorName(0xFF778899, "LightSlateGray"),
            new ColorName(0xFF7B68EE, "MediumSlateBlue"),
            new ColorName(0xFF7CFC00, "LawnGreen"),
            new ColorName(0xFF7FFF00, "Chartreuse"),
            new ColorName(0xFF7FFFD4, "Aquamarine"),
            new ColorName(0xFF800000, "Maroon"),
            new ColorName(0xFF800080, "Purple"),
            new ColorName(0xFF808000, "Olive"),
            new ColorName(0xFF808080, "Gray"),
            new ColorName(0xFF87CEEB, "SkyBlue"),
            new ColorName(0xFF87CEFA, "LightSkyBlue"),
            new ColorName(0xFF8A2BE2, "BlueViolet"),
            new ColorName(0xFF8B0000, "DarkRed"),
            new ColorName(0xFF8B008B, "DarkMagenta"),
            new ColorName(0xFF8B4513, "SaddleBrown"),
            new ColorName(0xFF8FBC8F, "DarkSeaGreen"),
            new ColorName(0xFF90EE90, "LightGreen"),
            new ColorName(0xFF9370DB, "MediumPurple"),
            new ColorName(0xFF9400D3, "DarkViolet"),
            new ColorName(0xFF98FB98, "PaleGreen"),
            new ColorName(0xFF9932CC, "DarkOrchid"),
            new ColorName(0xFF9ACD32, "YellowGreen"),
            new ColorName(0xFFA0522D, "Sienna"),
            new ColorName(0xFFA52A2A, "Brown"),
            new ColorName(0xFFA9A9A9, "DarkGray"),
            new ColorName(0xFFADD8E6, "LightBlue"),
            new ColorName(0xFFADFF2F, "GreenYellow"),
            new ColorName(0xFFAFEEEE, "PaleTurquoise"),
            new ColorName(0xFFB0C4DE, "LightSteelBlue"),
            new ColorName(0xFFB0E0E6, "PowderBlue"),
            new ColorName(0xFFB22222, "Firebrick"),
            new ColorName(0xFFB8860B, "DarkGoldenrod"),
            new ColorName(0xFFBA55D3, "MediumOrchid"),
            new ColorName(0xFFBC8F8F, "RosyBrown"),
            new ColorName(0xFFBDB76B, "DarkKhaki"),
            new ColorName(0xFFC0C0C0, "Silver"),
            new ColorName(0xFFC71585, "MediumVioletRed"),
            new ColorName(0xFFCD5C5C, "IndianRed"),
            new ColorName(0xFFCD853F, "Peru"),
            new ColorName(0xFFD2691E, "Chocolate"),
            new ColorName(0xFFD2B48C, "Tan"),
            new ColorName(0xFFD3D3D3, "LightGrey"),
            new ColorName(0xFFD8BFD8, "Thistle"),
            new ColorName(0xFFDA70D6, "Orchid"),
            new ColorName(0xFFDAA520, "GoldenRod"),
            new ColorName(0xFFDB7093, "PaleVioletRed"),
            new ColorName(0xFFDC143C, "Crimson"),
            new ColorName(0xFFDCDCDC, "GainsBoro"),
            new ColorName(0xFFDDA0DD, "Plum"),
            new ColorName(0xFFDEB887, "BurlyWood"),
            new ColorName(0xFFE0FFFF, "LightCyan"),
            new ColorName(0xFFE6E6FA, "Lavender"),
            new ColorName(0xFFE9967A, "DarkSalmon"),
            new ColorName(0xFFEE82EE, "Violet"),
            new ColorName(0xFFEEE8AA, "PaleGoldenRod"),
            new ColorName(0xFFF08080, "LightCoral"),
            new ColorName(0xFFF0E68C, "Khaki"),
            new ColorName(0xFFF0F8FF, "AliceBlue"),
            new ColorName(0xFFF0FFF0, "Honeydew"),
            new ColorName(0xFFF0FFFF, "Azure"),
            new ColorName(0xFFF4A460, "SandyBrown"),
            new ColorName(0xFFF5DEB3, "Wheat"),
            new ColorName(0xFFF5F5DC, "Beige"),
            new ColorName(0xFFF5F5F5, "WhiteSmoke"),
            new ColorName(0xFFF5FFFA, "MintCream"),
            new ColorName(0xFFF8F8FF, "GhostWhite"),
            new ColorName(0xFFFA8072, "Salmon"),
            new ColorName(0xFFFAEBD7, "AntiqueWhite"),
            new ColorName(0xFFFAF0E6, "Linen"),
            new ColorName(0xFFFAFAD2, "LightGoldenRodYellow"),
            new ColorName(0xFFFDF5E6, "Oldlace"),
            new ColorName(0xFFFF0000, "Red"),
            new ColorName(0xFFFF00FF, "Fuchsia"),
            new ColorName(0xFFFF00FF, "Magenta"),
            new ColorName(0xFFFF1493, "DeepPink"),
            new ColorName(0xFFFF4500, "OrangeRed"),
            new ColorName(0xFFFF6347, "Tomato"),
            new ColorName(0xFFFF69B4, "HotPink"),
            new ColorName(0xFFFF7F50, "Coral"),
            new ColorName(0xFFFF8C00, "DarkOrange"),
            new ColorName(0xFFFFA07A, "LightSalmon"),
            new ColorName(0xFFFFA500, "Orange"),
            new ColorName(0xFFFFB6C1, "LightPink"),
            new ColorName(0xFFFFC0CB, "Pink"),
            new ColorName(0xFFFFD700, "Gold"),
            new ColorName(0xFFFFDAB9, "PeachPuff"),
            new ColorName(0xFFFFDEAD, "NavajoWhite"),
            new ColorName(0xFFFFE4B5, "Moccasin"),
            new ColorName(0xFFFFE4C4, "Bisque"),
            new ColorName(0xFFFFE4E1, "MistyRose"),
            new ColorName(0xFFFFEBCD, "BlancheDalmond"),
            new ColorName(0xFFFFEFD5, "Papayawhip"),
            new ColorName(0xFFFFF0F5, "LavenderBlush"),
            new ColorName(0xFFFFF5EE, "SeaShell"),
            new ColorName(0xFFFFF8DC, "CornSilk"),
            new ColorName(0xFFFFFACD, "LemonChiffon"),
            new ColorName(0xFFFFFAF0, "FloralWhite"),
            new ColorName(0xFFFFFAFA, "Snow"),
            new ColorName(0xFFFFFF00, "Yellow"),
            new ColorName(0xFFFFFFE0, "LightYellow"),
            new ColorName(0xFFFFFFF0, "Ivory"),
            new ColorName(0xFFFFFFFF, "White"),
    };
    private int mColorIconHeight;
    private int mColorIconPadding;

    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_color_picker);

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        mTitle = intent.getStringExtra(EXTRA_TITLE);

        setTitle(mTitle);

        mColorIconHeight = (int) getResources().getDimension(R.dimen.color_icon_height);
        mColorIconPadding = (int) getResources().getDimension(R.dimen.color_icon_padding);

        final GridView gv = findViewById(R.id.colorgrid);
        gv.setAdapter(new ColorArrayAdapter(this, 0, COLOR_NAMES));
    }

    @Override
    public void onClick(View v) {
        final Object tag = v.getTag();
        if (tag != null && tag instanceof ColorName) {
            final ColorName cn = (ColorName) tag;
            if (cn.color != 0) {
                setCurrentColor(cn.color);
            } else {
                final EditText edtInput = new EditText(this);
                edtInput.setHint(R.string.color_picker_hint);
                edtInput.setSingleLine();
                edtInput.setFilters(new InputFilter[]{
                        new InputFilter() {
                            @Override
                            public CharSequence filter(CharSequence source, int start, int end,
                                                       Spanned dest, int dstart, int dend) {
                                if (dest.length() < 6 && source.toString().matches("[0-9a-fA-F]*")) {
                                    return source;
                                }
                                return "";
                            }
                        },
                });
                new AlertDialog.Builder(this)
                        .setTitle(R.string.color_picker_input)
                        .setView(edtInput)
                        .setPositiveButton(R.string.label_OK, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final String text = edtInput.getText().toString();
                                int code = 0;
                                try {
                                    code = Integer.parseInt(text, 16);
                                    setCurrentColor(code | 0xFF000000);
                                } catch (Exception e) {
                                    Toast.makeText(ColorPickerActivity.this, R.string.color_picker_hint, Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.label_CANCEL, null)
                        .show();
            }
        }
    }

    private void setCurrentColor(int color) {
        Intent intent = getIntent();
        intent.putExtra(EXTRA_COLOR, color);
        setResult(RESULT_OK, intent);
        finish();
        return;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    static class ColorName {
        int color;
        String name;

        public ColorName(int _color, String _name) {
            color = _color;
            name = _name;
        }
    }

    class ColorArrayAdapter extends ArrayAdapter<ColorName> {
        Context mContext;

        public ColorArrayAdapter(Context context, int textViewResourceId, ColorName[] objects) {
            super(context, textViewResourceId, objects);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = (TextView) convertView;
            if (tv == null) {
                tv = new TextView(mContext);
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, mColorIconHeight);
                tv.setLayoutParams(lp);
                tv.setPadding(mColorIconPadding, mColorIconPadding, mColorIconPadding, mColorIconPadding);
            }
            final ColorName cn = getItem(position);
            if (cn.color != 0) {

                tv.setText(String.format("%1$s\n#%2$06X", cn.name, (cn.color & 0xFFFFFF)));
                tv.setBackgroundColor(cn.color);

                {
                    float r = ((cn.color & 0xFF0000) >> 16) / 255.0F;
                    float g = ((cn.color & 0xFF00) >> 8) / 255.0F;
                    float b = ((cn.color & 0xFF)) / 255.0F;

                    float y = 0.299F * r + 0.587F * g + 0.114F * b;

                    if (y > 0.5F) {
                        tv.setTextColor(0xFF000000);
                    } else {
                        tv.setTextColor(0xFFFFFFFF);
                    }
                }
            } else {
                tv.setText(cn.name);
                tv.setBackgroundColor(0xFFCCCCCC);
                tv.setTextColor(0xFF000000);
            }
            tv.setTag(cn);
            tv.setOnClickListener(ColorPickerActivity.this);
            return tv;
        }

    }
}
