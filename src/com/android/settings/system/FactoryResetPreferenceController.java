/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package com.android.settings.system;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.Preference;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

import java.util.List;

public class FactoryResetPreferenceController extends AbstractPreferenceController
        implements PreferenceControllerMixin {
    /** Key of the "Factory reset" preference in {@link R.xml.reset_dashboard_fragment}. */
    private static final String KEY_FACTORY_RESET = "factory_reset";

    private final UserManager mUm;
    private final AccountManager mAm;

    public FactoryResetPreferenceController(Context context) {
        super(context);
        mUm = (UserManager) context.getSystemService(Context.USER_SERVICE);
        mAm = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
    }

    /** Hide "Factory reset" settings for secondary users, except demo users. */
    @Override
    public boolean isAvailable() {
        return mUm.isAdminUser() || Utils.isCarrierDemoUser(mContext);
    }

    @Override
    public String getPreferenceKey() {
        return KEY_FACTORY_RESET;
    }

    @Override
    public void updateState(Preference preference) {
        final List<UserInfo> profiles = mUm.getProfiles(UserHandle.myUserId());
        int accountsCount = 0;
        for (UserInfo userInfo : profiles) {
            final int profileId = userInfo.id;
            Account[] accounts = mAm.getAccountsAsUser(profileId);
            accountsCount += accounts.length;
        }
        if (accountsCount == 0) {
            preference.setSummary(R.string.master_clear_summary);
        } else {
            preference.setSummary(mContext.getResources().getQuantityString(
                    R.plurals.master_clear_with_account_summary,
                    accountsCount, accountsCount));
        }
    }
}
