import {
  SAVE_CHART_DATA,
  SAVE_TRADES_DATA,
  SAVE_WALLET_DATA,
} from './constants';

// The initial state of the App
const initialState = {
  chartData: [],
  tradesData: [],
  walletsData: [],
};

function appReducer(state = initialState, action) {
  switch (action.type) {
    case SAVE_TRADES_DATA:
      return {
        ...state,
        tradesData: [
          action.payload,
          ...state.tradesData
        ]
          .sort((a, b) => -1 * (a.timestamp > b.timestamp ? 1 : (a.timestamp < b.timestamp ? -1 : 0))) // eslint-disable-line
          .slice(0, 20)
      };
    case SAVE_WALLET_DATA:
      return {
        ...state,
        walletsData: [
          ...state.walletsData.filter(w => w.currency !== action.payload.currency),
          action.payload,
        ]
      };
    case SAVE_CHART_DATA:
      return {
        ...state,
        chartData: [
          ...state.chartData.slice(state.chartData.length - 1000, state.chartData.length),
          action.payload
        ]
      };
    default:
      return state;
  }
}

export default appReducer;
