package bobo.shanche.Dosth;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pgyersdk.feedback.PgyFeedback;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;


import bobo.shanche.R;
import bobo.shanche.coffeeDialog;

/**
 * Created by bobo1 on 2016/7/16.
 */
public class SettingsFragment extends PreferenceFragment {
    public SettingsFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        addPreferencesFromResource(R.xml.settings);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        PgyUpdateManager.unregister();
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()){
            case "coffee":
                coffeeDialog coffeeDialog = new coffeeDialog();
                coffeeDialog.show(getFragmentManager(),"coffee");
                break;
            case "github":
                Intent intent_Github = new Intent("android.intent.action.VIEW");
                Uri uri_Github = Uri.parse("https://github.com/bobo4629/Shanche");
                intent_Github.setData(uri_Github);
                startActivity(intent_Github);
                break;
            case "GPL":
                Intent intent_GPL = new Intent("android.intent.action.VIEW");
                Uri uri_GPL = Uri.parse("https://www.gnu.org/licenses/gpl.html");
                intent_GPL.setData(uri_GPL);
                startActivity(intent_GPL);
                break;
            case "email":
                /*
                Intent intent_email = new Intent("android.intent.action.SENDTO");
                Uri uri_email = Uri.parse("mailto:bobosusu4629@gmail.com");
                intent_email.setData(uri_email);
                startActivity(intent_email);
                */
                PgyFeedback.getInstance().showDialog(getActivity());
                break;
            case "shanche":
                PgyUpdateManager.register(getActivity(),
                        new UpdateManagerListener() {

                            @Override
                            public void onUpdateAvailable(final String result) {

                                // 将新版本信息封装到AppBean中
                                final AppBean appBean = getAppBeanFromString(result);
                                new AlertDialog.Builder(getActivity().getApplicationContext())
                                        .setTitle("更新")
                                        .setMessage("")
                                        .setCancelable(true)
                                        .setNegativeButton(
                                                "确定",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int which) {
                                                        startDownloadTask(
                                                                getActivity(),
                                                                appBean.getDownloadURL());
                                                    }
                                                }).show();


                            }

                            @Override
                            public void onNoUpdateAvailable() {
                                Toast.makeText(getActivity(), "当前为最新版本。", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case "note":
                break;
            default:
                break;

        }

        return true;
    }
}
