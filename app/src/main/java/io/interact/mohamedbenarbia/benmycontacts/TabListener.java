package io.interact.mohamedbenarbia.benmycontacts;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;

/**
 *  Listens to tab actions.
 */
public class TabListener< T extends Fragment> implements ActionBar.TabListener {

    private static final String LogTag = "TabListener" ;

    private Fragment mFragment;
    private final Activity mActivity;
    private final String mTag;
    private final Class<T> mClass;



    /** Constructor used each time a new tab is created.
     * @param activity  The host Activity, used to instantiate the fragment
     * @param tag  The identifier tag for the fragment
     * @param clz  The fragment's Class, used to instantiate the fragment
     */
    public TabListener(Activity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
    }

    /* The following are each of the ActionBar.TabListener callbacks */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // Check if the fragment is already initialized
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            Log.d("Fragment", "Adding fragment"+ mFragment.getTag() +"in Tab") ;

            ft.add(android.R.id.content, mFragment, mTag);
        } else {
            // If it exists, simply attach it in order to show it
            Log.d("Fragment", "Attaching fragment"+ mFragment.getTag() +"in Tab") ;


            ft.attach(mFragment);

        }


        if(this.mTag.equals(mActivity.getText(R.string.contacts_tab_tag))) {
            Log.d("Tab", "Tag equals to contacts") ;
            tab.setIcon(R.drawable.contacts_icon);
        } else if (this.mTag.equals(mActivity.getText(R.string.interactions_tab_tag))) {

            Log.d("Tab", "Tag equals to interactions") ;
            tab.setIcon(R.drawable.interactions_icon);
        }
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            // Detach the fragment, because another one is being attached
            ft.detach(mFragment);
        }

        if(this.mTag.equals(mActivity.getText(R.string.contacts_tab_tag))) {
           tab.setIcon(R.drawable.contacts_icon_off);
        } else if (this.mTag.equals(mActivity.getText(R.string.interactions_tab_tag))) {
            tab.setIcon(R.drawable.interactions_icon_off);
        }
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }
}