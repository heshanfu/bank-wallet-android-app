package bitcoin.wallet.blockchain

import android.content.res.AssetManager
import android.security.keystore.UserNotAuthenticatedException
import bitcoin.wallet.bitcoin.BitcoinBlockchainService
import bitcoin.wallet.bitcoin.BitcoinJWrapper
import bitcoin.wallet.core.ILocalStorage
import java.io.File

object BlockchainManager {

    lateinit var localStorage: ILocalStorage

    private val blockchainServices = mapOf<String, IBlockchainService>(
            "BTC" to BitcoinBlockchainService
    )

    fun init(filesDir: File, assetManager: AssetManager, storage: BlockchainStorage, testMode: Boolean) {
        BitcoinBlockchainService.init(BitcoinJWrapper(filesDir, assetManager, testMode), storage)
    }

    fun startServices() {
        localStorage.savedWords?.let {
            BitcoinBlockchainService.start(it)
        }
    }

    @Throws(UserNotAuthenticatedException::class)
    fun initNewWallet(words: List<String>) {
        localStorage.saveWords(words)
        BitcoinBlockchainService.initNewWallet(words)
    }

    @Throws(UserNotAuthenticatedException::class)
    fun restoreWalletAsync(words: List<String>) {
        localStorage.saveWords(words)
        BitcoinBlockchainService.restoreWalletAsync(words)
    }

    @Throws(NotEnoughFundsException::class, InvalidAddress::class)
    fun sendCoins(coinCode: String, address: String, value: Long) {
        getServiceByCoinCode(coinCode).sendCoins(address, value)
    }

    private fun getServiceByCoinCode(coinCode: String) =
            blockchainServices[coinCode] ?: throw UnsupportedBlockchain(coinCode)

    @Throws(Exception::class)
    fun getReceiveAddress(coinCode: String): String {
        return getServiceByCoinCode(coinCode).getReceiveAddress()
    }

}

interface IBlockchainService {

    fun sendCoins(address: String, value: Long)

    fun getReceiveAddress(): String

}

class NotEnoughFundsException(cause: Throwable) : Exception(cause)

class InvalidAddress(cause: Throwable) : Exception(cause)

class UnsupportedBlockchain(coinCode: String) : Exception("Unsupported blockchain $coinCode")