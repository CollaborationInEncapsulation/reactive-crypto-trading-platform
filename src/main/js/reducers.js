
import { combineReducers } from 'redux';

import appReducer from './components/App/reducers';

const createReducer = asyncReducers => combineReducers({
  // inject you reducers here
  rootReducer: () => null, // example
  app: appReducer,
  ...asyncReducers,
});

export default createReducer;
