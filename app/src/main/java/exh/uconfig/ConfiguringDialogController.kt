package exh.uconfig

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.base.controller.DialogController
import eu.kanade.tachiyomi.util.lang.launchUI
import eu.kanade.tachiyomi.util.system.toast
import kotlin.concurrent.thread
import timber.log.Timber

class ConfiguringDialogController : DialogController() {
    private var materialDialog: MaterialDialog? = null

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        if (savedViewState == null) {
            thread {
                try {
                    EHConfigurator(activity!!).configureAll()
                    launchUI {
                        activity?.toast(activity?.getString(R.string.eh_settings_successfully_uploaded))
                    }
                } catch (e: Exception) {
                    activity?.let {
                        it.runOnUiThread {
                            MaterialDialog(it)
                                .title(R.string.eh_settings_configuration_failed)
                                .message(text = it.getString(R.string.eh_settings_configuration_failed_message, e.message))
                                .positiveButton(android.R.string.ok)
                                .show()
                        }
                    }
                    Timber.e(e, "Configuration error!")
                }
                launchUI {
                    finish()
                }
            }
        }

        return MaterialDialog(activity!!)
            .title(R.string.eh_settings_uploading_to_server)
            .message(R.string.eh_settings_uploading_to_server_message)
            .cancelable(false)
            .also {
                materialDialog = it
            }
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        materialDialog = null
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        finish()
    }

    fun finish() {
        router.popController(this)
    }
}
