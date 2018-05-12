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

package io.ombre.wallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.ombre.wallet.R;
import io.ombre.wallet.dialog.NodeFragment;
import io.ombre.wallet.layout.WalletInfoAdapter;
import io.ombre.wallet.model.WalletManager;
import io.ombre.wallet.util.Helper;
import io.ombre.wallet.util.NodeList;
import io.ombre.wallet.widget.DropDownEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class LoginFragment extends Fragment implements WalletInfoAdapter.OnInteractionListener,
        View.OnClickListener {

    private WalletInfoAdapter adapter;

    List<WalletManager.WalletInfo> walletList = new ArrayList<>();
    List<WalletManager.WalletInfo> displayedList = new ArrayList<>();

    ImageView ivGunther;
    Listener activityCallback;

    // Container Activity must implement this interface
    public interface Listener {
        SharedPreferences getPrefs();

        File getStorageRoot();

        boolean onWalletSelected(String wallet, String daemon, boolean testnet);

        void onWalletDetails(String wallet, boolean testnet);

        void onWalletReceive(String wallet, boolean testnet);

        void onWalletRename(String name);

        void onWalletBackup(String name);

        void onWalletArchive(String walletName);

        void onAddWallet(boolean testnet, String type);

        void showNet(boolean testnet);

        void setToolbarButton(int type);

        void setTitle(String title);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            this.activityCallback = (Listener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement Listener");
        }
    }

    @Override
    public void onPause() {
        Timber.d("onPause()");
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume()");
        activityCallback.setTitle(null);
        activityCallback.showNet(isTestnet());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ivGunther = (ImageView) view.findViewById(R.id.ivGunther);
        fabScreen = (FrameLayout) view.findViewById(R.id.fabScreen);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fabNew = (FloatingActionButton) view.findViewById(R.id.fabNew);
        fabKey = (FloatingActionButton) view.findViewById(R.id.fabKey);
        fabSeed = (FloatingActionButton) view.findViewById(R.id.fabSeed);

        fabNewL = (RelativeLayout) view.findViewById(R.id.fabNewL);
        fabKeyL = (RelativeLayout) view.findViewById(R.id.fabKeyL);
        fabSeedL = (RelativeLayout) view.findViewById(R.id.fabSeedL);

        fab_pulse = AnimationUtils.loadAnimation(getContext(), R.anim.fab_pulse);
        fab_open_screen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open_screen);
        fab_close_screen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close_screen);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fabNew.setOnClickListener(this);
        fabKey.setOnClickListener(this);
        fabSeed.setOnClickListener(this);
        fabScreen.setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        registerForContextMenu(recyclerView);
        this.adapter = new WalletInfoAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);

        setNet(false /* testnet */);
        activityCallback.showNet(false /* testnet */);
        return view;
    }

    public void setNet(boolean testnet) {
        this.testnet = testnet;
        activityCallback.showNet(testnet);
        loadList();
    }

    // Callbacks from WalletInfoAdapter
    @Override
    public void onInteraction(final View view, final WalletManager.WalletInfo infoItem) {
        String x = isTestnet() ? "Te" : "cash";
        if (x.indexOf(infoItem.address.charAt(0)) < 0) {
            Toast.makeText(getActivity(), getString(R.string.prompt_wrong_net), Toast.LENGTH_LONG).show();
            return;
        }

        if (activityCallback.onWalletSelected(infoItem.name, getDaemon(), isTestnet())) {
            //savePrefs();
        }
    }

    String getDaemon() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(NodeFragment.PREF_DAEMON, "wallet.ombre.io:4445");
    }

    @Override
    public boolean onContextInteraction(MenuItem item, WalletManager.WalletInfo listItem) {
        switch (item.getItemId()) {
            case R.id.action_info:
                showInfo(listItem.name);
                break;
            case R.id.action_receive:
                showReceive(listItem.name);
                break;
            case R.id.action_rename:
                activityCallback.onWalletRename(listItem.name);
                break;
            case R.id.action_backup:
                activityCallback.onWalletBackup(listItem.name);
                break;
            case R.id.action_archive:
                activityCallback.onWalletArchive(listItem.name);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    private void filterList() {
        displayedList.clear();
        String x = isTestnet() ? "Te" : "cash";
        for (WalletManager.WalletInfo s : walletList) {
            if (x.indexOf(s.address.charAt(0)) >= 0) displayedList.add(s);
        }
    }

    public void loadList() {
        Timber.d("loadList()");
        WalletManager mgr = WalletManager.getInstance();
        List<WalletManager.WalletInfo> walletInfos =
                mgr.findWallets(activityCallback.getStorageRoot());
        Timber.d("walletInfos: %d", walletInfos.size());

        walletList.clear();
        walletList.addAll(walletInfos);
        filterList();
        adapter.setInfos(displayedList);
        adapter.notifyDataSetChanged();

        // deal with Gunther & FAB animation
        if (displayedList.isEmpty()) {
            fab.startAnimation(fab_pulse);
            if (ivGunther.getDrawable() == null) {
                ivGunther.setImageResource(R.drawable.gunther_desaturated);
            }
        } else {
            fab.clearAnimation();
            if (ivGunther.getDrawable() != null) {
                ivGunther.setImageDrawable(null);
            }
        }
    }

    private void showInfo(@NonNull String name) {
        activityCallback.onWalletDetails(name, isTestnet());
    }

    private boolean showReceive(@NonNull String name) {
        activityCallback.onWalletReceive(name, isTestnet());
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
        /*
        Testnet is disabled for now.
        menu.findItem(R.id.action_testnet).setChecked(isTestnet());
         */
        super.onCreateOptionsMenu(menu, inflater);
    }

    boolean testnet = false;

    boolean isTestnet() {
        return testnet;
    }


    private boolean isFabOpen = false;
    private FloatingActionButton fab, fabNew, fabKey, fabSeed;
    private FrameLayout fabScreen;
    private RelativeLayout fabNewL, fabKeyL, fabSeedL;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward, fab_open_screen, fab_close_screen;
    private Animation fab_pulse;

    public boolean isFabOpen() {
        return isFabOpen;
    }

    public void animateFAB() {
        if (isFabOpen) {
            fabScreen.setVisibility(View.INVISIBLE);
            fabScreen.setClickable(false);
            fabScreen.startAnimation(fab_close_screen);
            fab.startAnimation(rotate_backward);
            fabNewL.startAnimation(fab_close);
            fabNew.setClickable(false);
            fabKeyL.startAnimation(fab_close);
            fabKey.setClickable(false);
            fabSeedL.startAnimation(fab_close);
            fabSeed.setClickable(false);
            isFabOpen = false;
        } else {
            fabScreen.setClickable(true);
            fabScreen.startAnimation(fab_open_screen);
            fab.startAnimation(rotate_forward);
            fabNewL.startAnimation(fab_open);
            fabNew.setClickable(true);
            fabKeyL.startAnimation(fab_open);
            fabKey.setClickable(true);
            fabSeedL.startAnimation(fab_open);
            fabSeed.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                animateFAB();
                break;
            case R.id.fabNew:
                fabScreen.setVisibility(View.INVISIBLE);
                isFabOpen = false;
                activityCallback.onAddWallet(isTestnet(), GenerateFragment.TYPE_NEW);
                break;
            case R.id.fabKey:
                animateFAB();
                activityCallback.onAddWallet(isTestnet(), GenerateFragment.TYPE_KEY);
                break;
            case R.id.fabSeed:
                animateFAB();
                activityCallback.onAddWallet(isTestnet(), GenerateFragment.TYPE_SEED);
                break;
            case R.id.fabScreen:
                animateFAB();
                break;
        }
    }
}
