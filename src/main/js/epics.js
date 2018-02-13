import { combineEpics } from 'redux-observable';

import websocketTradesEpic from './components/App/epics';

export default combineEpics(websocketTradesEpic);
