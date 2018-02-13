import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';

import CurrencyPreview from '../CurrencyPreview';
import Wallet from '../Wallet';
import OrderForm from '../OrderForm';
import CurrencyChart from '../CurrencyChart';
import TradesTable from '../TradesTable';

import { startStream, stopStream, offerTrade } from './actions';


import styles from './styles.scss';

class App extends Component {
  state = {} // eslint-disable-line object-curly-newline

  componentDidMount() {
    this.props.startStream();
  }

  componentWillUnmount() {
    this.props.stopStream();
  }

  getCurrentBTCPrice() {
    const data = this.props.chartData;
    const lastPrice = data[data.length - 1];

    return lastPrice && lastPrice.data;
  }

  getCurrentTrades() {
    const data = this.props.tradesData;

    return [...data.slice(data.length - 10, data.length)];
  }

  render() {
    return (
      <div className={styles.wrapper}>
        <div className='container-fluid'>
          <div className='row'>
            <div className='col-sm-12 col-md-3 col-lg-3'>
              <CurrencyPreview price={this.getCurrentBTCPrice()} />
              <Wallet wallets={this.props.walletsData} />
              <OrderForm offerTrade={this.props.offerTrade} price={this.getCurrentBTCPrice()} />
            </div>
            <div className='col-sm-12 col-md-9 col-lg-9'>
              <CurrencyChart data={this.props.chartData} />
              <TradesTable data={this.getCurrentTrades()} />
            </div>
          </div>
        </div>
      </div>
    );
  }
}

App.propTypes = {
  startStream: PropTypes.func,
  stopStream: PropTypes.func,
  offerTrade: PropTypes.func,
  chartData: PropTypes.array,
  tradesData: PropTypes.array,
  walletsData: PropTypes.array,
};

const mapStateToProps = state => ({
  tradesData: state.app.tradesData,
  chartData: state.app.chartData,
  walletsData: state.app.walletsData,
}); // eslint-disable-line object-curly-newline

function mapDispatchToProps(dispatch) {
  return {
    dispatch,
    startStream: () => dispatch(startStream()),
    stopStream: () => dispatch(stopStream()),
    offerTrade: data => dispatch(offerTrade(data))
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(App);
