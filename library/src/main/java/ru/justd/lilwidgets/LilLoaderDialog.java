package ru.justd.lilwidgets;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by defuera on 21/06/2016.
 * LilLoaderDialog is a singleton across the application and must be dismissed before changing view (particulary acitivity)
 * You may style and setup the dialog via styles.xml. Find example in the demo app.
 * <pre>
 * {@code
 * <style name="LilStyle" parent="Theme.AppCompat.Light.Dialog.Alert">
 * <item name="colorAccent">@color/colorAccent</item>
 * <item name="android:textColor">@color/black</item>
 * <item name="android:background">@color/white</item>
 * <item name="lilDefaultMessage">@string/loading</item>
 * <item name="lilTitleStyle">@style/Title</item>
 * <item name="lilMessageStyle">@style/Body</item>
 * <item name="lilProgressColor">@color/colorAccent</item>
 * </style>
 * }
 */
public class LilLoaderDialog extends DialogFragment {

    private static final String EXTRA_TITLE = "EXTRA_TITLE";
    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private static final String EXTRA_CANCELABLE = "EXTRA_CANCELABLE";
    private static final String EXTRA_HIDE_DEFAULT_MESSAGE = "EXTRA_HIDE_DEFAULT_MESSAGE";

    private ProgressBar progressBar;
    private TextView title;
    private TextView message;
    private DialogInterface.OnDismissListener dismissListener;
    private DialogInterface.OnCancelListener cancelListener;

    private void setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    private void setOnCancelListener(DialogInterface.OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loader, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        title = (TextView) view.findViewById(R.id.title);
        message = (TextView) view.findViewById(R.id.message);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setOnDismissListener(dismissListener);
            dialog.setOnCancelListener(cancelListener);

            //noinspection ConstantConditions
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            int progressColor = Utils.loadColorFromStyle(getActivity(), R.style.LilStyle, R.attr.lilLoaderDialogProgressColor);
            if (progressColor != 0) {
                Utils.setProgressColor(progressBar, progressColor);
            }

            if (getArguments() != null) {
                setCancelable(getArguments().getBoolean(EXTRA_CANCELABLE, true));

                String titleText = getArguments().getString(EXTRA_TITLE);
                if (!TextUtils.isEmpty(titleText)) {
                    title.setText(titleText);
                } else {
                    title.setVisibility(View.GONE);
                }

                String messageText = getArguments().getString(EXTRA_MESSAGE);
                if (!TextUtils.isEmpty(messageText)) {
                    message.setText(messageText);
                } else {
                    if (getArguments().getBoolean(EXTRA_HIDE_DEFAULT_MESSAGE, false) || TextUtils.isEmpty(message.getText().toString())) {
                        message.setVisibility(View.GONE);
                    } else {
                        message.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    public static void show(FragmentManager fragmentManager) {
        new LilLoaderDialog.Builder(fragmentManager).create();
    }

    public static void dismiss(FragmentManager fragmentManager) {
        Builder.handler.removeCallbacksAndMessages(null);

        LilLoaderDialog fragment = getTopProgressDialogFragment(fragmentManager);

        if (fragment != null) {
            fragment.dismiss();
        }
    }

    private static LilLoaderDialog getTopProgressDialogFragment(FragmentManager fragmentManager) {
        //noinspection RestrictedApi
        List<Fragment> fragments = fragmentManager.getFragments();
        int size = fragments == null ? 0 : fragments.size();

        if (size > 0) {

            Fragment topFragment = fragments.get(size - 1);
            if (topFragment instanceof LilLoaderDialog) {
                return (LilLoaderDialog) topFragment;
            }
        }

        return null;
    }

    public static final class Builder {

        private static Handler handler = new Handler();

        private final FragmentManager fragmentManager;
        private String title = null;
        private String message = null;
        private boolean cancelable = false;

        private boolean hideDefaultMessage = false;
        private long delayMillis = 0;
        private DialogInterface.OnDismissListener dismissListener;
        private DialogInterface.OnCancelListener cancelListener;

        public Builder(FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
            this.dismissListener = dismissListener;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener cancelListener) {
            this.cancelable = true;
            this.cancelListener = cancelListener;
            return this;
        }

        public Builder setDelay(long delayMillis) {
            this.delayMillis = delayMillis;
            return this;
        }

        public Builder hideDefaultMessage() {
            this.hideDefaultMessage = true;
            return this;
        }

        /**
         * build and show
         */
        public final LilLoaderDialog create() {
            //only one dialog can exist at a time
            dismiss(fragmentManager);

            final LilLoaderDialog fragment = new LilLoaderDialog();

            handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (getTopProgressDialogFragment(fragmentManager) != null) {
                                throw new IllegalStateException("Dialog is already shown");
                            }

                            Bundle bundle = new Bundle();
                            bundle.putString(EXTRA_TITLE, title);
                            bundle.putString(EXTRA_MESSAGE, message);
                            bundle.putBoolean(EXTRA_CANCELABLE, cancelable);
                            bundle.putBoolean(EXTRA_HIDE_DEFAULT_MESSAGE, hideDefaultMessage);
                            fragment.setArguments(bundle);

                            fragment.setStyle(STYLE_NORMAL, R.style.LilStyle);

                            fragment.show(fragmentManager, LilLoaderDialog.class.getSimpleName());
                            fragment.setOnDismissListener(dismissListener);
                            fragment.setOnCancelListener(cancelListener);
                        }
                    },
                    delayMillis);

            return fragment;
        }
    }
}
