package bitcoin.wallet.core.security

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import bitcoin.wallet.LauncherActivity
import bitcoin.wallet.R
import bitcoin.wallet.core.IEncryptionManager
import bitcoin.wallet.core.managers.Factory
import bitcoin.wallet.lib.AlertDialogFragment

class EncryptionManager : IEncryptionManager {

    private val keyStoreWrapper = KeyStoreWrapper()

    @Synchronized
    override fun encrypt(data: String): String {

        var masterKey = keyStoreWrapper.getAndroidKeyStoreSymmetricKey(MASTER_KEY)

        if (masterKey == null) {
            masterKey = keyStoreWrapper.createAndroidKeyStoreSymmetricKey(MASTER_KEY)
        }
        return CipherWrapper().encrypt(data, masterKey)
    }

    @Synchronized
    override fun decrypt(data: String): String {
        val masterKey = keyStoreWrapper.getAndroidKeyStoreSymmetricKey(MASTER_KEY)
                ?: throw KeyPermanentlyInvalidatedException()
        return CipherWrapper().decrypt(data, masterKey)
    }

    companion object {
        const val MASTER_KEY = "MASTER_KEY"

        fun showAuthenticationScreen(activity: Activity, requestCode: Int) {
            val mKeyguardManager = activity.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val intent: Intent? = mKeyguardManager.createConfirmDeviceCredentialIntent("Authorization required", "")
            activity.startActivityForResult(intent, requestCode)
        }

        fun showAuthenticationScreen(fragment: Fragment, requestCode: Int) {
            val mKeyguardManager = fragment.activity?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val intent: Intent? = mKeyguardManager.createConfirmDeviceCredentialIntent("Authorization required", "")
            fragment.startActivityForResult(intent, requestCode)
        }

        fun showKeysInvalidatedAlert(activity: FragmentActivity) {
            AlertDialogFragment.newInstance(R.string.alert_keys_invalidated_title, R.string.alert_keys_invalidated_description, R.string.alert_ok,
                    object : AlertDialogFragment.Listener {
                        override fun onButtonClick() {
                            KeyStoreWrapper().removeAndroidKeyStoreKey(MASTER_KEY)
                            Factory.preferencesManager.clearAll()

                            val intent = Intent(activity, LauncherActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            activity.startActivity(intent)

                            activity.finish()
                        }
                    }).show(activity.supportFragmentManager, "keys_invalidated_alert")
        }

        fun showNoDeviceLockWarning(activity: FragmentActivity) {
            AlertDialogFragment.newInstance(R.string.alert_no_device_lock_title, R.string.alert_no_device_lock_description, R.string.alert_close,
                    object : AlertDialogFragment.Listener {
                        override fun onButtonClick() {
                            activity.finish()
                        }
                    }).show(activity.supportFragmentManager, "no_device_lock_alert")
        }

        fun isDeviceLockEnabled(ctx: Context): Boolean {
            val keyguardManager = ctx.getSystemService(Activity.KEYGUARD_SERVICE) as KeyguardManager
            return keyguardManager.isKeyguardSecure
        }



    }

}
