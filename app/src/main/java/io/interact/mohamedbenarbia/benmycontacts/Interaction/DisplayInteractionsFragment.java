package io.interact.mohamedbenarbia.benmycontacts.Interaction;

import android.app.ListFragment;
import android.os.Bundle;

/**
 * Fragment that handel user's interaction.
 */
public class DisplayInteractionsFragment extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        (new UserInteractionsRetrieverAsyncTask(this)).execute();
    }


}
