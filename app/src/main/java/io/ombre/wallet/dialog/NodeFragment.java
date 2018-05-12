/*
 * Copyright (c) 2017 m2049r
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ombre.wallet.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import io.ombre.wallet.R;
import io.ombre.wallet.util.Helper;
import io.ombre.wallet.util.NodeList;
import io.ombre.wallet.widget.DropDownEditText;
import timber.log.Timber;

public class NodeFragment extends DialogFragment {
    static final String TAG = "NodeFragment";

    ArrayAdapter<String> nodeAdapter;
    DropDownEditText etDaemonAddress;
    EditText etDummy;
    Context context;

    public static NodeFragment newInstance() {
        return new NodeFragment();
    }

    private static final String PREF_DAEMON_MAINNET = "daemon_mainnet";
    public static final String PREF_DAEMON = "wallet.ombre.io:4445";


    private static final String PREF_DAEMONLIST_MAINNET =
            "wallet.ombre.io:4445";

    private NodeList daemonMainNet = null;

    void loadPrefs() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        daemonMainNet = new NodeList(sharedPref.getString(PREF_DAEMON_MAINNET, PREF_DAEMONLIST_MAINNET));
        setDaemon(daemonMainNet);
    }

    void savePrefs() {
        daemonMainNet.setRecent(etDaemonAddress.getText().toString().trim());
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_DAEMON_MAINNET, daemonMainNet.toString());
        editor.apply();
    }

    void setDaemon(NodeList nodeList) {
        Timber.d("setDaemon() %s", nodeList.toString());
        String[] nodes = nodeList.getNodes().toArray(new String[0]);
        nodeAdapter.clear();
        nodeAdapter.addAll(nodes);
        etDaemonAddress.getText().clear();
        if (nodes.length > 0) {
            etDaemonAddress.setText(nodes[0]);
        }
        etDaemonAddress.dismissDropDown();
        etDummy.requestFocus();
        Helper.hideKeyboard(getActivity());

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_DAEMON, etDaemonAddress.getText().toString().trim());
        editor.apply();
    }

    public static void display(FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        NodeFragment.newInstance().show(ft, TAG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_node, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setNegativeButton(R.string.about_close,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        savePrefs();
                        dialog.dismiss();
                    }
                });




        etDummy = (EditText) view.findViewById(R.id.etDummy);
        etDaemonAddress = (DropDownEditText) view.findViewById(R.id.etDaemonAddress);
        nodeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line);
        etDaemonAddress.setAdapter(nodeAdapter);

        Helper.hideKeyboard(getActivity());

        etDaemonAddress.setThreshold(0);
        etDaemonAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etDaemonAddress.showDropDown();
                Helper.showKeyboard(getActivity());
            }
        });

        etDaemonAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !getActivity().isFinishing() && etDaemonAddress.isLaidOut()) {
                    etDaemonAddress.showDropDown();
                    Helper.showKeyboard(getActivity());
                }
            }
        });

        etDaemonAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Helper.hideKeyboard(getActivity());
                    etDummy.requestFocus();
                    return true;
                }
                return false;
            }
        });

        etDaemonAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                Helper.hideKeyboard(getActivity());
                etDummy.requestFocus();
            }
        });

        loadPrefs();















        return builder.create();
    }
}