import React, { Component } from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';

import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

import styles from './styles.scss';

const interval = 90000;

const margin = {
  top: 20, right: 30, left: 20, bottom: 10
};

const hashCode = (str) => { // java String#hashCode
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash); // eslint-disable-line
  }
  return hash;
};

const intToRGB = (i) => {
  const c = (i & 0x00FFFFFF) // eslint-disable-line
    .toString(16)
    .toUpperCase();

  return '00000'.substring(0, 6 - c.length) + c;
};

const color = str => intToRGB(hashCode(str));

const convertLineData = arr => _.groupBy(arr
  .filter(item => item.type === 'PRICE')
  .map(item => ({
    market: item.market,
    label: new Date(item.timestamp).toLocaleTimeString('en-GB'),
    name: item.timestamp,
    amt: item.data,
    price: item.data,
  })), 'market');

class CurrencyChart extends Component {
  state = {
    y: {
      max: 0,
      min: undefined
    }
  }

  render() {
    let data = this.props.data || null;

    if (data && data.length > 0) {
      const lastTimestamp = data[data.length - 1].timestamp;
      let diff;
      let i = 0;

      do {
        diff = lastTimestamp - data[i].timestamp;
      }
      while (diff >= interval && ++i);

      data = data.slice(i, data.length);
    }

    const lineData = convertLineData(data);

    return (
      <div className={styles.currencyChart}>
        <ResponsiveContainer width='100%' height={350}>
          <LineChart
            margin={margin}
          >
            <XAxis
              dataKey='name'
              tickFormatter={v => new Date(v).toLocaleTimeString('en-GB')}
              type='number'
              allowDuplicatedCategory={false}
              height={60}
              domain={[dataMin => (dataMin), dataMax => (dataMax)]}
            />
            <YAxis
              dataKey='amt'
              type='number'
              domain={[(dataMin) => {
                const min = (this.state.y.min !== undefined)
                                        ? Math.min(this.state.y.min, Math.abs(dataMin - 5))
                                        : Math.abs(dataMin - 5);
                this.state = { // eslint-disable-line
                  y: {
                    min,
                    max: this.state.y.max
                  }
                };
                return min;
              }, (dataMax) => {
                const max = Math.max(this.state.y.max, Math.abs(dataMax + 5));
                this.state = { // eslint-disable-line
                  y: {
                    min: this.state.y.min,
                    max
                  }
                };
                return max;
              }]}
            />
            <CartesianGrid strokeDasharray='3 3' />
            <Tooltip />
            { Object.keys(lineData).map(key => <Line data={lineData[key]} key={key} type='monotone' name={key} dataKey='price' stroke={`#${color(key)}`} />) }
          </LineChart>
        </ResponsiveContainer>
      </div>
    );
  }
}

CurrencyChart.propTypes = {
  data: PropTypes.array,
};

export default CurrencyChart;

