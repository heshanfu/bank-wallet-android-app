package bitcoin.wallet.modules.wallet

import bitcoin.wallet.entities.CoinValue
import bitcoin.wallet.entities.CurrencyValue
import io.reactivex.subjects.BehaviorSubject

data class WalletBalanceViewItem(val adapterId: String, val coinValue: CoinValue, val exchangeValue: CurrencyValue, val currencyValue: CurrencyValue, val progress: BehaviorSubject<Double>?) {

    override fun equals(other: Any?): Boolean {
        if (other is WalletBalanceViewItem) {
            return adapterId == other.adapterId &&
                    coinValue == other.coinValue &&
                    exchangeValue == other.exchangeValue &&
                    currencyValue == other.currencyValue &&
                    progress == other.progress
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = adapterId.hashCode()
        result = 31 * result + coinValue.hashCode()
        result = 31 * result + exchangeValue.hashCode()
        result = 31 * result + currencyValue.hashCode()
        result = 31 * result + (progress?.hashCode() ?: 0)
        return result
    }

}
