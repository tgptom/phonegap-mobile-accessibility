/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.phonegap.plugin.mobileaccessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.CaptioningManager;
import android.webkit.WebView;

public class MobileAccessibilityHelper {
    private MobileAccessibility mMobileAccessibility;
    private WebView mWebView;
    private ViewParent mParent;
    private AccessibilityManager mAccessibilityManager;
    private CaptioningManager mCaptioningManager;
    private AccessibilityManager.AccessibilityStateChangeListener mAccessibilityStateChangeListener;
    private CaptioningManager.CaptioningChangeListener mCaptioningChangeListener;
    private AccessibilityManager.TouchExplorationStateChangeListener mTouchExplorationStateChangeListener;

    public void initialize(MobileAccessibility mobileAccessibility) {
        mMobileAccessibility = mobileAccessibility;
        mWebView = (WebView) mMobileAccessibility.webView.getEngine().getView();
        mParent = mWebView.getParentForAccessibility();
        mAccessibilityManager = (AccessibilityManager) mMobileAccessibility.cordova.getActivity().getSystemService(Context.ACCESSIBILITY_SERVICE);
        mCaptioningManager = (CaptioningManager) mMobileAccessibility.cordova.getActivity().getSystemService(Context.CAPTIONING_SERVICE);
    }

    public boolean isClosedCaptioningEnabled() {
        return mCaptioningManager != null && mCaptioningManager.isEnabled();
    }

    public boolean isScreenReaderRunning() {
        return mAccessibilityManager != null &&
            mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_BRAILLE | AccessibilityServiceInfo.FEEDBACK_SPOKEN).size() > 0;
    }

    public boolean isTouchExplorationEnabled() {
        return mAccessibilityManager != null && mAccessibilityManager.isTouchExplorationEnabled();
    }

    public void addStateChangeListeners() {
        if (mAccessibilityManager != null) {
            if (mAccessibilityStateChangeListener == null) {
                mAccessibilityStateChangeListener = new AccessibilityManager.AccessibilityStateChangeListener() {
                    @Override
                    public void onAccessibilityStateChanged(boolean enabled) {
                        mMobileAccessibility.onAccessibilityStateChanged(enabled);
                    }
                };
            }
            mAccessibilityManager.addAccessibilityStateChangeListener(mAccessibilityStateChangeListener);

            if (mTouchExplorationStateChangeListener == null) {
                mTouchExplorationStateChangeListener = new AccessibilityManager.TouchExplorationStateChangeListener() {
                    @Override
                    public void onTouchExplorationStateChanged(boolean enabled) {
                        mMobileAccessibility.onTouchExplorationStateChanged(enabled);
                    }
                };
            }
            mAccessibilityManager.addTouchExplorationStateChangeListener(mTouchExplorationStateChangeListener);
        }

        if (mCaptioningManager != null) {
            if (mCaptioningChangeListener == null) {
                mCaptioningChangeListener = new CaptioningManager.CaptioningChangeListener() {
                    @Override
                    public void onEnabledChanged(boolean enabled) {
                        mMobileAccessibility.onCaptioningEnabledChanged(enabled);
                    }
                };
            }
            mCaptioningManager.addCaptioningChangeListener(mCaptioningChangeListener);
        }
    }

    public void removeStateChangeListeners() {
        if (mCaptioningManager != null && mCaptioningChangeListener != null) {
            mCaptioningManager.removeCaptioningChangeListener(mCaptioningChangeListener);
            mCaptioningChangeListener = null;
        }

        if (mAccessibilityManager != null) {
            if (mTouchExplorationStateChangeListener != null) {
                mAccessibilityManager.removeTouchExplorationStateChangeListener(mTouchExplorationStateChangeListener);
                mTouchExplorationStateChangeListener = null;
            }

            if (mAccessibilityStateChangeListener != null) {
                mAccessibilityManager.removeAccessibilityStateChangeListener(mAccessibilityStateChangeListener);
                mAccessibilityStateChangeListener = null;
            }
        }
    }

    public void announceForAccessibility(CharSequence text) {
        if (mAccessibilityManager != null && mAccessibilityManager.isEnabled() && mParent != null) {
            mAccessibilityManager.interrupt();
            AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT);
            mWebView.onInitializeAccessibilityEvent(event);
            event.getText().add(text);
            event.setContentDescription(null);
            mParent.requestSendAccessibilityEvent(mWebView, event);
        }
    }

    public double getTextZoom() {
        return mWebView.getSettings().getTextZoom();
    }

    public void setTextZoom(double textZoom) {
        mWebView.getSettings().setTextZoom((int) textZoom);
    }
}
