/*
 * Copyright 2026 Marco Gomiero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.prof18.secureqrreader.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.ui.revenuecatui.ExperimentalPreviewRevenueCatUIPurchasesAPI
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener
import com.revenuecat.purchases.ui.revenuecatui.Paywall
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions
import com.revenuecat.purchases.ui.revenuecatui.utils.Resumable

@OptIn(ExperimentalPreviewRevenueCatUIPurchasesAPI::class)
@Composable
fun SupportScreen(
    onBackPressed: () -> Unit,
    onPurchaseStarted: () -> Unit,
    onPurchaseCancelledOrFailed: () -> Unit,
) {
    val listener = remember(onPurchaseStarted, onPurchaseCancelledOrFailed) {
        object : PaywallListener {
            override fun onPurchasePackageInitiated(packageToPurchase: Package, resumable: Resumable) {
                resumable.resume(true)
            }

            override fun onPurchaseStarted(packageToPurchase: Package) = onPurchaseStarted()

            override fun onPurchaseCompleted(customerInfo: CustomerInfo, storeTransaction: StoreTransaction) = Unit

            override fun onPurchaseError(error: PurchasesError) = onPurchaseCancelledOrFailed()

            override fun onPurchaseCancelled() = onPurchaseCancelledOrFailed()

            override fun onRestoreStarted() = Unit

            override fun onRestoreCompleted(customerInfo: CustomerInfo) = Unit

            override fun onRestoreError(error: PurchasesError) = Unit
        }
    }
    val options = remember(onBackPressed, listener) {
        PaywallOptions.Builder(dismissRequest = onBackPressed)
            .setListener(listener)
            .build()
    }
    Paywall(options = options)
}
