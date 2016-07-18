package bobo.shanche;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tendcloud.tenddata.TCAgent;

/**
 * Created by bobo1 on 2016/7/17.
 */
public class coffeeDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TCAgent.onPageStart(getActivity().getApplicationContext(), "捐赠界面");
        return inflater.inflate(R.layout.dialog_coffee, container, false);
    }

    @Override
    public void onDestroyView() {
        TCAgent.onPageEnd(getActivity().getApplicationContext(), "捐赠界面");
        super.onDestroyView();
    }
}
