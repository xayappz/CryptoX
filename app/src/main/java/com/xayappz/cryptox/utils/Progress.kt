package com.xayappz.cryptox.utils

import android.app.ProgressDialog
import androidx.fragment.app.FragmentActivity
import com.xayappz.cryptox.R

object Progress {
    var PD: ProgressDialog? = null


    fun loadProgress(Ctx: FragmentActivity) {
        PD = ProgressDialog(Ctx, R.style.AppCompatAlertDialogStyle)
        PD?.apply {
            this.setTitle("Please Wait")
            this.setCancelable(false)
            this.setMessage("Loading Data")
            this.show()
        }
    }

    fun cancelDialog() {
        PD?.apply {
            this.dismiss()

        }
    }


}