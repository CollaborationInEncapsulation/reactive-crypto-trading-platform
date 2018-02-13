import React, { Component } from 'react';
import PropTypes from 'prop-types';

import styles from './styles.scss';

class OrderFrom extends Component {
  state = {
    inputValue: undefined,
  }

  updateInputValue(evt) {
    this.setState({
      inputValue: evt.target.value,
    });
  }

  render() {
    return (
      <form className={styles.orderForm}>
        <div className='row mb-3'>
          <div className='col'>
            <label htmlFor='amount'>Amout btc</label>
            <input type='number' onChange={e => this.updateInputValue(e)} className='form-control' id='amount' />
          </div>
        </div>
        <div className='row mb-2'>
          <div className='col'>
            <button
              type='button'
              onClick={() => this.props.offerTrade({
                type: 'TRADE',
                timestamp: Date.now(),
                currency: 'BTC',
                market: 'Local',
                data: {
                  amount: this.state.inputValue,
                  price: this.props.price,
                },
              })}
              className='btn btn-success'
            >Buy
            </button>
          </div>
          <div className='col'>
            <button
              type='button'
              onClick={() => this.props.offerTrade({
                type: 'TRADE',
                timestamp: Date.now(),
                currency: 'BTC',
                market: 'Local',
                data: {
                  amount: -this.state.inputValue,
                  price: this.props.price,
                },
              })}
              className='btn btn-danger'
            >Sell
            </button>
          </div>
        </div>
      </form>
    );
  }
}

OrderFrom.propTypes = {
  price: PropTypes.number,
  offerTrade: PropTypes.func,
};

export default OrderFrom;
