/*
 *  Copyright (C) 2013 - 2014 Alexander "Evisceration" Martinz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.namelessrom.devicecontrol.modules.wizard.pages;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.namelessrom.devicecontrol.Logger;
import org.namelessrom.devicecontrol.R;
import org.namelessrom.devicecontrol.actions.ActionProcessor;
import org.namelessrom.devicecontrol.modules.wizard.setup.Page;
import org.namelessrom.devicecontrol.modules.wizard.setup.SetupDataCallbacks;
import org.namelessrom.devicecontrol.modules.wizard.ui.SetupPageFragment;
import org.namelessrom.devicecontrol.theme.AppResources;

import java.util.ArrayList;

public class ActionPage extends Page {
    private ActionFragment fragment;

    public ActionPage(Context context, SetupDataCallbacks callbacks, int titleResourceId) {
        super(context, callbacks, titleResourceId);
    }

    @Override public Fragment createFragment() {
        final Bundle args = new Bundle();
        args.putString(Page.KEY_PAGE_ARGUMENT, getKey());

        fragment = new ActionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void refresh() {
        if (fragment != null) {
            fragment.setUpPage();
        }
    }

    @Override public int getNextButtonResId() { return R.string.next; }

    public static class ActionFragment extends SetupPageFragment {

        @Override protected void setUpPage() {
            mPage.setRequired(true);
            final ListView listView = (ListView) mRootView.findViewById(android.R.id.list);

            final String category = mCallbacks.getSetupData().category;
            final ArrayList<ActionProcessor.Entry> actions = ActionProcessor.getActions(category);
            final ArrayList<String> entries = new ArrayList<>(actions.size());
            final ArrayList<String> values = new ArrayList<>(actions.size());
            for (final ActionProcessor.Entry entry : actions) {
                entries.add(entry.name);
                values.add(entry.value);
            }

            final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_single_choice, entries);

            listView.setAdapter(adapter);
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int i,
                        long l) {
                    Logger.i(this, "position: %s | values: %s", i, values.get(i));
                    setData(values.get(i));
                    mCallbacks.onPageLoaded(mPage);
                }
            });

            final String entry = mCallbacks.getSetupData().name;
            if (!TextUtils.isEmpty(entry)) {
                listView.setItemChecked(values.indexOf(entry), true);
                setData(entry);
            }
        }

        private void setData(final String entry) {
            mCallbacks.getSetupData().name = entry;
            mPage.setCompleted(true);
        }

        @Override protected int getLayoutResource() {
            if (AppResources.get().isLightTheme()) {
                return R.layout.wizard_page_list_light;
            }
            return R.layout.wizard_page_list_dark;
        }

        @Override protected int getTitleResource() { return R.string.setup_action; }

    }
}
