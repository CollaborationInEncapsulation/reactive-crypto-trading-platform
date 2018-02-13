import React from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';

import styles from './styles.scss';

const renderWallet = wallet => (
  <tr key={wallet.currency}>
    <td>
      <span className={styles.walletTableText}>{wallet.currency}</span>
    </td>
    <td>
      <span className={styles.walletTableText}>
        {wallet.data || null}
      </span>
    </td>
  </tr>
);

const Wallet = props => (
  <div className={styles.wallet}>
    <h5>Wallets</h5>
    <table className={styles.walletTable}>
      <tbody>
        {_.sortBy(props.wallets, 'currency').map(wallet => renderWallet(wallet))}
      </tbody>
    </table>
  </div>
);

Wallet.propTypes = {
  wallets: PropTypes.array
};

export default Wallet;
