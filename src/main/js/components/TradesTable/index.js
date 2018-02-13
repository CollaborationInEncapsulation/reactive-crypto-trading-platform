import React from 'react';
import PropTypes from 'prop-types';
import { Column, Table, AutoSizer } from 'react-virtualized';

import styles from './styles.scss';

const TradesTable = props => (
  <div className={styles.tradesTableBlock}>
    <AutoSizer disableHeight>
      {({ width }) => (
        <Table
          width={width}
          height={350}
          headerHeight={50}
          rowHeight={30}
          rowCount={props.data ? (props.data.length < 10 ? props.data.length : 10) : 0} // eslint-disable-line
          rowGetter={({ index }) => props.data[index]}
        >
          <Column
            label=''
            dataKey='data'
            width={width / 5}
            cellRenderer={({ cellData }) => (<span><i className={`fas ${cellData.amount < 0 ? 'fa-chevron-down' : 'fa-chevron-up'}`} /></span>)}
          />
          <Column
            label='TIME'
            dataKey='timestamp'
            width={width / 5}
            cellRenderer={({ cellData }) => new Date(cellData).toLocaleTimeString('en-GB')}
          />
          <Column
            label='MARKET'
            dataKey='market'
            width={width / 5}
          />
          <Column
            label='PRICE'
            dataKey='data'
            cellRenderer={({ cellData }) => Math.abs(cellData.price)}
            width={width / 5}
          />
          <Column
            label='AMOUNT'
            dataKey='data'
            width={width / 5}
            cellRenderer={({ cellData }) => Math.abs(cellData.amount)}
          />
        </Table>)}
    </AutoSizer>
  </div>
);

TradesTable.propTypes = {
  data: PropTypes.array
};


export default TradesTable;
